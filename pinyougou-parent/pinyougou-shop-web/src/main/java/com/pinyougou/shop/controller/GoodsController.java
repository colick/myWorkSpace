package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 增加
     *
     * @param goodsVo
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsVo goodsVo) {
        try {
            //获取商家ID
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);//设置商家ID
            goodsService.add(goodsVo);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goodsVo
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsVo goodsVo) {
        try {
            //获取原对象的商家ID
            String oldeSellerId = goodsService.findById(goodsVo.getGoods().getId()).getSellerId();
            //获取商家ID
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!oldeSellerId.equals(sellerId) || !sellerId.equals(goodsVo.getGoods().getSellerId())) {
                return new Result(false, "非法操作");
            }
            goodsVo.getGoods().setSellerId(sellerId);//设置商家ID
            goodsService.update(goodsVo);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    @RequestMapping("/findByPage")
    public PageResult<TbGoods> findByPage(@RequestBody TbGoods goods, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findByPage(goods, page, size);
    }

    @RequestMapping("/findByGoodsId")
    public GoodsVo findByGoodsId(Long goodsId) throws Exception {
        return goodsService.findByGoodsId(goodsId);
    }

    @RequestMapping("/updateMarketable/{isMarketable}")
    public Result updateMarketable(@PathVariable String isMarketable, Long[] ids) {
        try {
            goodsService.updateMarketable(ids, isMarketable);
            return new Result(true, "操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败!");
        }
    }
}