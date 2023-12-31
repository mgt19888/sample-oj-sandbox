package com.mgt.ojcodesandbox.core;


import com.mgt.ojcodesandbox.model.ExecuteCodeRequest;
import com.mgt.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
