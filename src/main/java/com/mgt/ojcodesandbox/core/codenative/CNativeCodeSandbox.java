package com.mgt.ojcodesandbox.core.codenative;

import com.mgt.ojcodesandbox.model.CodeSandboxCmd;
import org.springframework.stereotype.Component;

/**
 * CPP 原生代码沙箱实现（直接复用模板方法）
 */
@Component
public class CNativeCodeSandbox extends NativeCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final String GLOBAL_CPP_CLASS_NAME = "Main.cpp";

    public CNativeCodeSandbox() {
        super.globalCodeFileName = GLOBAL_CPP_CLASS_NAME;
        super.timeOut = TIME_OUT;
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("gcc -finput-charset=UTF-8 -fexec-charset=UTF-8 %s -o %s", userCodePath, userCodePath.substring(0, userCodePath.length() - 4)))
                .runCmd(String.format("%s/Main", userCodeParentPath))
                .build();
    }
}
