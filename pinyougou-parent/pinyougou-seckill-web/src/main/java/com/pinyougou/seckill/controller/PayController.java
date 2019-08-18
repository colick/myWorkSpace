package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);
        if (seckillOrder == null) {
            return new HashMap<>();
        }
        long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);//金额（分）
        return payService.createNative(seckillOrder.getId() + "", fen + "");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String orderPayNo) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result;
        Map<String, String> resultMap;
        int count = 0;
        while (true) {
            resultMap = payService.queryPayStatus(orderPayNo);
            if (resultMap == null) {
                result = new Result(false, "支付异常!");
                break;
            }
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(orderPayNo), resultMap.get("transaction_id"));
                result = new Result(true, "支付成功!");
                break;
            }
            //间隔2.5秒
            Thread.sleep(2500);
            count++;
            if (count >= 100) {
                result = new Result(false, "二维码超时");
                //关闭订单并从缓存中删除订单
                // 关闭支付
                Map<String, String> payResult = payService.closePay(orderPayNo);
                if (payResult != null && "FAIL".equals(payResult.get("return_code"))) {
                    if ("ORDERPAID".equals(payResult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        //保存订单
                        seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(orderPayNo), resultMap.get("transaction_id"));
                    }
                }
                //删除订单
                if (!result.getSuccess()) {
                    seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(orderPayNo));
                }
                break;
            }
        }
        return result;
    }
}