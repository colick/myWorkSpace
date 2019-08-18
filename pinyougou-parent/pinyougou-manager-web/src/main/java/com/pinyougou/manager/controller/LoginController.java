package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public Map<String, String> getLoginName() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        resultMap.put("loginName", name);
        return resultMap;
    }
}