package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.CartVo;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) throws Exception {
        System.out.println("添加商品至购物车,itemId=" + itemId + ",num=" + num);
        try {
            //获取登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("当前登录用户名username=" + username);
            //获取购物车列表（从cookie中）
            List<CartVo> cartList = this.findCartList();
            //调用service添加商品至购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)) {//未登录情况
                //添加回cookie
                CookieUtil.setCookie(request, response, "cartList", JSONObject.toJSONString(cartList), 3600, "UTF-8");
                System.out.println("向cookie中存入新购物车内容");
            } else {//已登录情况
                cartService.saveCartListToRedis(username, cartList);
                System.out.println("向redis缓存中存入新购物车内容");
            }
            return new Result(true, "添加商品成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加商品失败!");
        }
    }

    @RequestMapping("/findCartList")
    public List<CartVo> findCartList() throws Exception {
        //获取登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("无论是否登录,先从cookie中获取已有的购物车列表");
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (null == cartListStr || "".equals(cartListStr.trim())) {
            cartListStr = "[]";
        }
        List<CartVo> cartCookieList = JSONObject.parseArray(cartListStr, CartVo.class);
        if ("anonymousUser".equals(username)) {//未登录情况,从cookie中获取购物车
            System.out.println("用户未登录,从cookie中返回购物车列表");
            return cartCookieList;
        } else {//已登录情况,从redis获取购物车
            System.out.println("用户已登录,先从redis缓存中获取购物车列表");
            List<CartVo> redisCartList = cartService.findCartListFromRedis(username);
            //判断cookie中是否存在购物车列表,存在则合并后返回,否则直接返回redis中的购物车列表
            if (!CollectionUtils.isEmpty(cartCookieList)) {
                System.out.println("用户在cookie中存在购物车,将cookie和redis中的购物车合并后再返回购物车列表");
                redisCartList = cartService.mergeCartList(cartCookieList, redisCartList);
                //删除cookie中的购物车
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的购物车列表再存入redis中
                cartService.saveCartListToRedis(username, redisCartList);
            }
            return redisCartList;
        }
    }
}