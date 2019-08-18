package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);
        if (payLog == null) {
            return new HashMap<>();
        }
        return payService.createNative(payLog.getOutTradeNo() + "", payLog.getTotalFee() + "");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String orderPayNo) throws Exception {
        Result result = null;
        Map<String, String> resultMap;
        int count = 0;
        while (true) {
            resultMap = payService.queryPayStatus(orderPayNo);
            if (resultMap == null) {
                result = new Result(false, "支付异常!");
                break;
            }
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                orderService.updateOrderStatus(orderPayNo, resultMap.get("transaction_id"));
                result = new Result(true, "支付成功!");
                break;
            }
            //间隔2.5秒
            Thread.sleep(2500);
            count++;
            if (count >= 100) {
                result = new Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }
}