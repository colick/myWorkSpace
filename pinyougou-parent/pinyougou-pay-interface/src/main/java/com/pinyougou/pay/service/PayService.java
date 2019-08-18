package com.pinyougou.pay.service;

import java.util.Map;

public interface PayService {

    Map<String, String> createNative(String orderPayNo, String money) throws Exception;

    Map<String, String> queryPayStatus(String orderPayNo) throws Exception;

    Map<String, String> closePay(String orderPayNo) throws Exception;
}