package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/save")
    public Result save(@RequestBody TbOrder order) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);
        order.setSourceType("2");//PC端下单
        try {
            orderService.saveOrder(order);
            return new Result(true, "保存订单成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存订单失败!");
        }
    }
}