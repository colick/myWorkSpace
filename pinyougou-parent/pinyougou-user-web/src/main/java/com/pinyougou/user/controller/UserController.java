package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.PhoneFormatCheckUtils;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/register")
    public Result register(@RequestBody TbUser user, String verifyCode) throws Exception {
        try {
            //校验验证码
            if (!userService.checkVerifyCode(user.getPhone(), verifyCode)) {
                return new Result(false, "验证码输入错误!");
            }
            userService.registerUser(user);
            return new Result(true, "注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败!");
        }
    }

    @RequestMapping("/sendVerifyCode")
    public Result sendVerifyCode(String phone) throws Exception {
        //校验验手机号码
        if (!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)) {
            return new Result(false, "手机号格式不正确!");
        }
        try {
            userService.createVerifyCode(phone);
            return new Result(true, "验证码发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码发送失败!");
        }
    }
}