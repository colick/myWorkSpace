package com.pinyougou.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class LoginServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("用户登陆,username值: " + username);
        try {
            TbSeller seller = sellerService.findById(username);
            User user = new User(seller.getSellerId(), seller.getPassword(), "1".equals(seller.getStatus()),
                    true, true, true, generateAuthorities());
            System.out.println("用户登陆,user返回值: " + JSONObject.toJSONString(user));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<SimpleGrantedAuthority> generateAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        return list;
    }
}
