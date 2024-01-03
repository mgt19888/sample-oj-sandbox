package com.mgt.ojcodesandbox.core.codedocker;

import cn.hutool.core.io.resource.ResourceUtil;
import com.mgt.ojcodesandbox.model.CodeSandboxCmd;
import com.mgt.ojcodesandbox.model.ExecuteCodeRequest;
import com.mgt.ojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class PythonDockerCodeSandbox extends DockerCodeSandboxTemplate {

    private static final long TIME_OUT = 15000L;

    // 如果是第一次运行则设置为true，后面再运行则设置为false即可，这样不用每次初始化都去下载镜像
    private static final Boolean FIRST_INIT = false;

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.py";

    private static final String IMAGE = "python:3.8";

    public PythonDockerCodeSandbox() {
        super.globalCodeFileName = GLOBAL_JAVA_CLASS_NAME;
        super.firstInit = FIRST_INIT;
        super.image = IMAGE;
        super.timeOut = TIME_OUT;
    }

    public static void main(String[] args) {
        PythonDockerCodeSandbox javaNativeCodeSandbox = new PythonDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.py", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("python");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(null)
                .runCmd(String.format("python3 /app/Main.py", userCodeParentPath))
//                .runCmd("java -cp /app Main")
                .build();
    }
}



