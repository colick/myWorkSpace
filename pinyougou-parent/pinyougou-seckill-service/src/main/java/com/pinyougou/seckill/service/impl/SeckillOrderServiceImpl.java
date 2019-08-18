package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.IdWorker;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void submitOrder(Long seckillGoodsId, String userId) throws Exception {
        TbSeckillGoods seckillGoods = seckillGoodsService.findByIdFromRedis(seckillGoodsId);
        if (null == seckillGoods) {
            throw new Exception("商品不存在!");
        }
        if (0 == seckillGoods.getStockCount()) {
            throw new Exception("商品已秒杀完!");
        }
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //放回缓存
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoodsId, seckillGoods);//放回缓存
        if (seckillGoods.getStockCount() == 0) {//如果已经被秒光
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//同步到数据库
            //将商品清除缓存
            redisTemplate.boundHashOps("seckillGoods").delete(seckillGoodsId);
        }
        //保存订单
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillGoodsId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//状态
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }

    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) throws Exception {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) throws Exception {
        System.out.println("支付成功后保存订单至数据库,userId=" + userId + "orderId=" + orderId + "transactionId=" + transactionId);
        TbSeckillOrder seckillOrder = this.searchOrderFromRedisByUserId(userId);
        if (seckillOrder == null) {
            throw new Exception("订单不存在!");
        }
        if (orderId.longValue() != seckillOrder.getId().longValue()) {
            throw new Exception("订单数据不匹配!");
        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        seckillOrderMapper.insert(seckillOrder);//保存到数据库
        //从redis中清除
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) throws Exception {
        System.out.println("订单超时未支付,删除订单并还原库存!");
        TbSeckillOrder seckillOrder = this.searchOrderFromRedisByUserId(userId);
        if (seckillOrder != null) {
            if (seckillOrder.getId().longValue() != orderId.longValue()) {
                throw new Exception("订单数据不匹配!");
            }
            //删除缓存中的秒杀订单数据
            redisTemplate.boundHashOps("seckillOrder").delete(orderId);
            TbSeckillGoods seckillGoods = seckillGoodsService.findByIdFromRedis(seckillOrder.getSeckillId());
            if (seckillGoods != null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
            } else {
                seckillGoods = seckillGoodsService.findById(seckillOrder.getSeckillId());
                if (seckillGoods != null) {
                    seckillGoods.setStockCount(1);
                    redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
                }
            }
        } else {
            throw new Exception("订单不存在!");
        }
    }
}