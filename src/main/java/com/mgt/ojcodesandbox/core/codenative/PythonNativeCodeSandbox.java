package com.mgt.ojcodesandbox.core.codenative;

import com.mgt.ojcodesandbox.model.CodeSandboxCmd;
import com.mgt.ojcodesandbox.model.ExecuteMessage;
import com.mgt.ojcodesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 原生代码沙箱实现（直接复用模板方法）
 */
@Component
public class PythonNativeCodeSandbox extends NativeCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final String GLOBAL_PYTHON_CLASS_NAME = "Main.py";

    public PythonNativeCodeSandbox() {
        super.globalCodeFileName = GLOBAL_PYTHON_CLASS_NAME;
        super.timeOut = TIME_OUT;
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(null)
                .runCmd(String.format("PYTHONIOENCODING=utf-8 python3 %s/Main.py", userCodeParentPath))
                .build();
    }
}
