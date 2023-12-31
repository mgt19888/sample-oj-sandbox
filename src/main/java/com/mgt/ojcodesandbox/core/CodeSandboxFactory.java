package com.mgt.ojcodesandbox.core;

import com.mgt.ojcodesandbox.core.codenative.*;

public class CodeSandboxFactory {
    public static NativeCodeSandboxTemplate getInstance(String language) {
        switch (language) {
            case "java":
                return new JavaNativeCodeSandbox();
            case "python":
                return new PythonNativeCodeSandbox();
            case "c":
                return new CNativeCodeSandbox();
            case "cpp":
                return new CPPNativeCodeSandbox();
            default:
                throw new RuntimeException("暂不支持");
        }
    }
}
