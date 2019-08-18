package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String jsonText = textMessage.getText();
//            System.out.println("接收到的消息：" + jsonText);
            List<TbItem> itemList = JSONObject.parseArray(jsonText, TbItem.class);
            itemSearchService.importItemListToSolr(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}