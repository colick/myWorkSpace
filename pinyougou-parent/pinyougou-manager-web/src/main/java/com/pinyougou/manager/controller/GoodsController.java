package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /*@Reference
    private ItemPageService itemPageService;*/

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueSolrDestination;

    @Autowired
    private Destination topicPageDestination;

    @RequestMapping("/findByPage")
    public PageResult<TbGoods> findByPage(@RequestBody TbGoods goods, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return goodsService.findByPage(goods, page, size);
    }

    @RequestMapping("/findByGoodsId")
    public GoodsVo findByGoodsId(Long goodsId) throws Exception {
        return goodsService.findByGoodsId(goodsId);
    }

    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@PathVariable String status, Long[] ids) {
        try {
            goodsService.updateStatus(ids, status);
            //审核通过时导入solr库并生成页面
            if (status.equals("1")) {//审核通过
                final List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
                if (!CollectionUtils.isEmpty(itemList)) {
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(JSONObject.toJSONString(itemList));
                        }
                    });
                }
            }
            //审核通过生成静态页面
            for (final Long goodsId : ids) {
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(goodsId + "");
                    }
                });
            }
            return new Result(true, "操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            goodsService.del(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/generateHtml")
    public void genHtml(final Long goodsId) {
        jmsTemplate.send(topicPageDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(goodsId + "");
            }
        });
//        itemPageService.generateItemPage(goodsId);
    }
}