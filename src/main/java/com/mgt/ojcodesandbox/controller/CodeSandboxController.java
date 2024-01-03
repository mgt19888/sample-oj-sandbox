package com.mgt.ojcodesandbox.controller;

import com.mgt.ojcodesandbox.core.CodeSandboxFactory;
import com.mgt.ojcodesandbox.core.codedocker.DockerCodeSandboxTemplate;
import com.mgt.ojcodesandbox.model.ExecuteCodeRequest;
import com.mgt.ojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/codesandbox")
public class CodeSandboxController {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "lihasjtdhs72731dnas82j";

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 基本的认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        DockerCodeSandboxTemplate sandboxTemplate = CodeSandboxFactory.getInstance(executeCodeRequest.getLanguage());

        return sandboxTemplate.executeCode(executeCodeRequest);
    }
}
