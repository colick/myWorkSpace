package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.PageResult;
import entity.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 分页查询
     *
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping("/findByPage")
    public PageResult<TbSeckillGoods> findByPage(@RequestBody TbSeckillGoods seckillGoods, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        return seckillGoodsService.findByPage(seckillGoods, page, size);
    }

    /**
     * 保存
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/save")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            //获取商家ID
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            seckillGoods.setSellerId(sellerId);//设置商家ID
            //暂时写死商品开始与截至时间
            seckillGoods.setStartTime(new Date());
            seckillGoods.setEndTime(DateUtils.addDays(new Date(), 3));
            seckillGoodsService.save(seckillGoods);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    /**
     * id查询
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/findById")
    public TbSeckillGoods findById(Long id) throws Exception {
        return seckillGoodsService.findById(id);
    }

    /*@RequestMapping("/updateMarketable/{isMarketable}")
    public Result updateMarketable(@PathVariable String isMarketable, Long[] ids) {
        try {
            goodsService.updateMarketable(ids, isMarketable);
            return new Result(true, "操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败!");
        }
    }*/
}