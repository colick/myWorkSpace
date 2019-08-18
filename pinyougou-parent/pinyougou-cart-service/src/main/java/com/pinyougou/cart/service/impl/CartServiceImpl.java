package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<CartVo> addGoodsToCartList(List<CartVo> cartList, Long itemId, Integer num) throws Exception {
        if (null == itemId) {
            throw new Exception("ID Is Empty!");
        }

        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new Exception("Item Is Empty!");
        }

        String sellerId = item.getSellerId();
        if (null == sellerId || "".equals(sellerId.trim())) {
            throw new Exception("SellerId Is Empty!");
        }

        //判断传入的购物车中是否含有sellerId对应的数据
        CartVo cart = this.filterCartBySellerId(cartList, sellerId);
        if (null == cart) {//原购物车未含有本次添加的商家商品时添加一份新数据
            cart = new CartVo();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> itemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            itemList.add(orderItem);
            cart.setItemList(itemList);
            cartList.add(cart);
        } else {//原购物车含有本次添加的商家商品时追加一份数据
            //判断原购物车明细列表中是否含有本次须添加的商家商品明细数据
            TbOrderItem orderItem = this.filterOrderItemByItemId(cart.getItemList(), item.getId());
            if (null != orderItem) {//存在时追加数量
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                if (orderItem.getNum() <= 0) {//若果商品详情数量小于等于0时，则从集合中移除对应商品详情
                    cart.getItemList().remove(orderItem);
                }
                if (cart.getItemList().size() == 0) {//若商家商品详情列表为空时，则移除整个商家数据
                    cartList.remove(cart);
                }
            } else {//不存在时追加整个商品明细到明细列表
                orderItem = createOrderItem(item, num);
                cart.getItemList().add(orderItem);
            }
        }
        return cartList;
    }

    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (null == num || num <= 0) {
            throw new RuntimeException("Num Is Illegally!");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }


    private TbOrderItem filterOrderItemByItemId(List<TbOrderItem> itemList, Long itemId) {
        if (CollectionUtils.isEmpty(itemList)) {
            return null;
        } else {
            for (TbOrderItem orderItem : itemList) {
                if (itemId.equals(orderItem.getItemId())) {
                    return orderItem;
                }
            }
            return null;
        }
    }

    private CartVo filterCartBySellerId(List<CartVo> cartList, String sellerId) throws Exception {
        if (CollectionUtils.isEmpty(cartList)) {
            return null;
        } else {
            for (CartVo cart : cartList) {
                if (sellerId.equals(cart.getSellerId())) {
                    return cart;
                }
            }
            return null;
        }
    }

    @Override
    public List<CartVo> findCartListFromRedis(String username) throws Exception {
        System.out.println("根据用户名:" + username + "从redis中获取购物车列表");
        List<CartVo> cartList = (List<CartVo>) redisTemplate.boundHashOps("cartList").get(username);
        if (CollectionUtils.isEmpty(cartList)) {
            System.out.println("redis中不存在,返回空集合");
            return new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<CartVo> cartList) throws Exception {
        System.out.println("向redis缓存中存入购物车数据,username=" + username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<CartVo> mergeCartList(List<CartVo> cartCookieList, List<CartVo> redisCartList) throws Exception {
        System.out.println("将cookie中的购物车列表与redis中的购物车列表进行合并操作并返回");
        for (CartVo cart : cartCookieList) {
            for (TbOrderItem orderItem : cart.getItemList()) {
                redisCartList = this.addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCartList;
    }
}