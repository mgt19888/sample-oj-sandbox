package com.mgt.ojcodesandbox.core.codedocker;

import cn.hutool.core.io.resource.ResourceUtil;
import com.mgt.ojcodesandbox.model.CodeSandboxCmd;
import com.mgt.ojcodesandbox.model.ExecuteCodeRequest;
import com.mgt.ojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class CDockerCodeSandbox extends DockerCodeSandboxTemplate {

    private static final long TIME_OUT = 15000L;

    // 如果是第一次运行则设置为true，后面再运行则设置为false即可，这样不用每次初始化都去下载镜像 
    private static final Boolean FIRST_INIT = false;

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.cpp";

    private static final String IMAGE = "eclipse/cpp_gcc:latest";

    public CDockerCodeSandbox() {
        super.globalCodeFileName = GLOBAL_JAVA_CLASS_NAME;
        super.firstInit = FIRST_INIT;
        super.image = IMAGE;
        super.timeOut = TIME_OUT;
    }

    public static void main(String[] args) {
        CDockerCodeSandbox javaNativeCodeSandbox = new CDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.cpp", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("cpp");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("gcc -finput-charset=UTF-8 -fexec-charset=UTF-8 %s -o %s", userCodePath, userCodePath.substring(0, userCodePath.length() - 4)))
                .runCmd("/app/Main")
                .build();
    }
}



