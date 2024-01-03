package com.mgt.ojcodesandbox.core;

import com.mgt.ojcodesandbox.core.codedocker.*;

public class CodeSandboxFactory {
    public static DockerCodeSandboxTemplate getInstance(String language) {
        switch (language) {
            case "java":
                return new JavaDockerCodeSandbox();
            case "python":
                return new PythonDockerCodeSandbox();
            case "c":
                return new CDockerCodeSandbox();
            case "cpp":
                return new CPPDockerCodeSandbox();
            default:
                throw new RuntimeException("暂不支持");
        }
    }
}
