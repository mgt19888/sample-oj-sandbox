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

    private static final long TIME_OUT = 5000L;

    private static final Boolean FIRST_INIT = true;

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final String IMAGE = "openjdk:8-alpine";

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
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("javac -encoding utf-8 %s", userCodePath))
                .runCmd("java -Xmx256m -Dfile.encoding=UTF-8 -cp /app Main")
//                .runCmd("java -cp /app Main")
                .build();
    }
}



