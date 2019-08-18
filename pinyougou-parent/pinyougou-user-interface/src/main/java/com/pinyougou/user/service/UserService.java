package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {

    void registerUser(TbUser user) throws Exception;

    void createVerifyCode(String phone) throws Exception;

    boolean checkVerifyCode(String phone, String verifyCode) throws Exception;
}