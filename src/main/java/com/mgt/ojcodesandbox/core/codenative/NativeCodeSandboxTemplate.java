package com.mgt.ojcodesandbox.core.codenative;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.mgt.ojcodesandbox.core.CodeSandbox;
import com.mgt.ojcodesandbox.model.*;
import com.mgt.ojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 代码沙箱模板方法的实现
 */
@Slf4j
public abstract class NativeCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    long timeOut;

    String globalCodeFileName;


    /**
     * 每个实现类必须实现编译以及运行的cmd
     *
     * @param userCodeParentPath 代码所在的父目录
     * @param userCodePath       代码所在目录
     * @return {@link CodeSandboxCmd}
     */
    public abstract CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath);

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

//        1. 把用户的代码保存为文件
        File userCodeFile = saveCodeToFile(code);
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        String userCodePath = userCodeFile.getAbsolutePath();
        CodeSandboxCmd cmdFromLanguage = getCmd(userCodeParentPath, userCodePath);
        String compileCmd = cmdFromLanguage.getCompileCmd();
        String runCmd = cmdFromLanguage.getRunCmd();

//        2. 编译代码，得到 class 文件
        ExecuteMessage compileFileExecuteMessage = compileFile(compileCmd);
        System.out.println(compileFileExecuteMessage);

        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runFile(runCmd, inputList);

//        4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

//        5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        return outputResponse;
    }


    /**
     * 1. 把用户的代码保存为文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + globalCodeFileName;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2、编译代码
     * @param compileCmd
     * @return
     */
    public ExecuteMessage compileFile(String compileCmd) {
        if (compileCmd == null) {
            return new ExecuteMessage();
        }
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 3、执行文件，获得执行结果列表
     * @param runCmd
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(String runCmd, List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String curRunCmd = String.format("%s %s", runCmd, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(curRunCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(timeOut);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
//                ExecuteMessage executeMessage = ProcessUtils.runInteractProcessAndGetMessage(runProcess, inputArgs);
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4、获取输出结果
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        // 取用时最大值，便于判断是否超时
        long maxTime = 0;
        long maxMemory = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage().trim());
            Long time = executeMessage.getTime();
            Long memory = executeMessage.getMemory();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
            if (memory != null) {
                maxMemory = Math.max(maxMemory, memory);
            }
        }
        // 正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 5、删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

    /**
     * 6、获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
