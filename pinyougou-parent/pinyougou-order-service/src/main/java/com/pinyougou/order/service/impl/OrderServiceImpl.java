package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbPayLogExample;
import com.pinyougou.vo.CartVo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Override
    public void saveOrder(TbOrder order) throws Exception {
        //从缓存中得到购物车列表
        List<CartVo> cartList = (List<CartVo>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        if (CollectionUtils.isEmpty(cartList)) {
            throw new Exception("Cart Is Empty!");
        }
        List<String> orderIdList = new ArrayList();//订单ID列表
        double totalMoney = 0;
        TbOrder addOrder;
        for (CartVo cart : cartList) {
            addOrder = new TbOrder();
            addOrder.setOrderId(idWorker.nextId());//生成雪花主键
            addOrder.setPaymentType(order.getPaymentType());
            addOrder.setStatus("1");//未付款
            addOrder.setCreateTime(new Date());
            addOrder.setUpdateTime(addOrder.getCreateTime());
            addOrder.setUserId(order.getUserId());
            addOrder.setReceiverAreaName(order.getReceiverAreaName());
            addOrder.setReceiverMobile(order.getReceiverMobile());
            addOrder.setReceiver(order.getReceiver());
            addOrder.setInvoiceType("1");//默认个人发票
            addOrder.setSourceType(order.getSourceType());
            addOrder.setSellerId(cart.getSellerId());
            //计算单一商家订单总额
            double money = 0;
            for (TbOrderItem orderItem : cart.getItemList()) {
                orderItem.setId(idWorker.nextId());//生成雪花主键
                orderItem.setOrderId(addOrder.getOrderId());
                orderItem.setSellerId(addOrder.getSellerId());
                orderItemMapper.insertSelective(orderItem);
                money += orderItem.getTotalFee().doubleValue();
            }
            addOrder.setPayment(new BigDecimal(money));
            orderMapper.insertSelective(addOrder);
            orderIdList.add(addOrder.getOrderId() + "");//添加到订单列表
            totalMoney += money;
        }
        //若微信支付则生成订单支付对象至数据库并存入缓存
        if ("1".equals(order.getPaymentType())) {
            TbPayLog payLog = new TbPayLog();
            payLog.setOutTradeNo(idWorker.nextId() + "");
            payLog.setCreateTime(new Date());
            payLog.setTotalFee((long) (totalMoney * 100));//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLogMapper.insert(payLog);//插入到支付日志表
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }
        //清除redis中的购物车列表
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) throws Exception {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String orderPayNo, String transactionId) throws Exception {
        if (orderPayNo == null || orderPayNo.trim().length() == 0) {
            throw new Exception("PayNo Is Empty!");
        }
        if (transactionId == null || transactionId.trim().length() == 0) {
            throw new Exception("TransactionId Is Empty!");
        }
        //修改对应支付日志信息
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(orderPayNo);
        if (null == payLog) {
            throw new Exception("PayLog Is Empty!");
        }
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transactionId);
        payLog.setTradeState("1");//已支付
        payLogMapper.updateByPrimaryKeySelective(payLog);
        //修改对应订单列表信息
        String orderList = payLog.getOrderList();//获取订单号列表
        if(orderList != null && orderList.trim().length() > 0) {
            TbOrder order;
            for (String orderId : orderList.split(",")) {
                order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
                if (order != null) {
                    order.setStatus("2");//已付款
                    orderMapper.updateByPrimaryKey(order);
                }
            }
        }
        //清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    @Override
    public PageResult<TbPayLog> findByPage(TbPayLog payLog, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbPayLogExample example = new TbPayLogExample();
        //拼接条件
        if (payLog != null) {
            TbPayLogExample.Criteria criteria = example.createCriteria();
            if (null != payLog.getUserId() && payLog.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + payLog.getUserId() + "%");
            }
            if (null != payLog.getTradeState() && payLog.getTradeState().length() > 0) {
                criteria.andTradeStateEqualTo(payLog.getTradeState());
            }

        }
        example.setOrderByClause("create_time desc");
        Page<TbPayLog> page = (Page<TbPayLog>) payLogMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}