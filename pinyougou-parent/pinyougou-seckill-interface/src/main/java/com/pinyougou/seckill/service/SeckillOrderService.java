package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

public interface SeckillOrderService {

    void submitOrder(Long seckillGoodsId, String userId) throws Exception;

    TbSeckillOrder searchOrderFromRedisByUserId(String userId) throws Exception;

    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) throws Exception;

    void deleteOrderFromRedis(String userId, Long orderId) throws Exception;
}