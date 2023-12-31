package com.mgt.ojcodesandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/health")
public class HealthController {
    @GetMapping("/check")
    public String healthCheck() {
        return "ok";
    }
}
