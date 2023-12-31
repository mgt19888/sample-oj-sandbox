package com.mgt.ojcodesandbox.core.codedocker;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.mgt.ojcodesandbox.model.CodeSandboxCmd;
import com.mgt.ojcodesandbox.model.ExecuteCodeRequest;
import com.mgt.ojcodesandbox.model.ExecuteCodeResponse;
import com.mgt.ojcodesandbox.model.ExecuteMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JavaDockerCodeSandbox extends DockerCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final Boolean FIRST_INIT = true;

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final String IMAGE = "openjdk:8-alpine";

    public JavaDockerCodeSandbox() {
        super.globalCodeFileName = GLOBAL_JAVA_CLASS_NAME;
        super.firstInit = FIRST_INIT;
        super.image = IMAGE;
        super.timeOut = TIME_OUT;

    }

    public static void main(String[] args) {
        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
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



