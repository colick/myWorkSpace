package com.pinyougou.cart.service;

import com.pinyougou.vo.CartVo;

import java.util.List;

public interface CartService {

    List<CartVo> addGoodsToCartList(List<CartVo> cartList, Long itemId, Integer num) throws Exception;

    List<CartVo> findCartListFromRedis(String username) throws Exception;

    void saveCartListToRedis(String username, List<CartVo> cartList) throws Exception;

    List<CartVo> mergeCartList(List<CartVo> cartCookieList, List<CartVo> redisCartList) throws Exception;
}