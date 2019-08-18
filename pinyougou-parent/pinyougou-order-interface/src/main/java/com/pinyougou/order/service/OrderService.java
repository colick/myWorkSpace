package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entity.PageResult;

public interface OrderService {

    void saveOrder(TbOrder order) throws Exception;

    TbPayLog searchPayLogFromRedis(String userId) throws Exception;

    void updateOrderStatus(String orderPayNo,String transactionId) throws Exception;

    PageResult<TbPayLog> findByPage(TbPayLog payLog, int pageNum, int pageSize) throws Exception;
}