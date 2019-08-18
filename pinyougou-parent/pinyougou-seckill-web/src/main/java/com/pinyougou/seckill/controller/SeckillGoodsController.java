package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀商品详情
     *
     * @return
     */
    @RequestMapping("/findList")
    public List<TbSeckillGoods> findList() throws Exception {
        return seckillGoodsService.findList();
    }

    /**
     * 根据主键查询秒杀商品主键
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/findByIdFromRedis")
    public TbSeckillGoods findByIdFromRedis(Long id) throws Exception {
        return seckillGoodsService.findByIdFromRedis(id);
    }

}