package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueSmsDestination;

    @Override
    public void registerUser(TbUser user) throws Exception {
        if (user != null) {
            user.setCreated(new Date());//创建日期
            user.setUpdated(user.getCreated());//修改日期
            user.setStatus("Y");//默认正常
            user.setSourceType("1");//默认PC端
            //MD5加密密码
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void createVerifyCode(final String phone) throws Exception {
        final String verifyCode = (long) (Math.random() * 1000000) + "";
        System.out.println("生成的验证码:" + verifyCode);
        redisTemplate.boundHashOps("verifyCode").put(phone, verifyCode);
        //调用MQ发送消息
        jmsTemplate.send(queueSmsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);//手机号
                mapMessage.setString("template_code", "SMS_171118446");//模板编号
                mapMessage.setString("sign_name", "colick商城");//签名
                Map<String, String> map = new HashMap<>();
                map.put("number", verifyCode);
                mapMessage.setString("param", JSONObject.toJSONString(map));//参数
                return mapMessage;
            }
        });

    }

    @Override
    public boolean checkVerifyCode(String phone, String verifyCode) throws Exception {
        if (redisTemplate.boundHashOps("verifyCode") == null || !redisTemplate.boundHashOps("verifyCode").hasKey(phone)) {
            return false;
        }
        String sysCode = (String) redisTemplate.boundHashOps("verifyCode").get(phone);
        System.out.println("系统的验证码:" + sysCode);
        if (sysCode == null) {
            return false;
        }
        if (!sysCode.equals(verifyCode)) {
            return false;
        }
        return true;
    }
}