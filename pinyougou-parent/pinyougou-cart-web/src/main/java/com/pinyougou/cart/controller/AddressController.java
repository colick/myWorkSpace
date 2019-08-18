package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/findListByUserId")
    public List<TbAddress> findListByUserId() throws Exception {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByUserId(userName);
    }

    @RequestMapping("/findById")
    public TbAddress findById(Long id) throws Exception {
        return addressService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbAddress address) throws Exception {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (null == address.getId()) {//新增时填入userName
            address.setUserId(userName);
        } else {
            if (!userName.equals(address.getUserId())) {
                return new Result(false, "操作非法!");
            }
        }
        try {
            addressService.save(address);
            return new Result(true, "保存地址成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存地址失败!");
        }
    }

    @RequestMapping("/deleteById")
    public Result deleteById(Long id) throws Exception {
        try {
            addressService.deleteById(id);
            return new Result(true, "删除地址成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除地址失败!");
        }
    }
}