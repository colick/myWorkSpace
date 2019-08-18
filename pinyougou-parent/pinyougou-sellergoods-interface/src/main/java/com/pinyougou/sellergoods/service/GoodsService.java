package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;

import java.util.List;

public interface GoodsService {

    void add(GoodsVo goodsVo) throws Exception;

    PageResult<TbGoods> findByPage(TbGoods goods, int pageNum, int pageSize) throws Exception;

    GoodsVo findByGoodsId(Long id) throws Exception;

    TbGoods findById(Long id) throws Exception;

    void update(GoodsVo goodsVo) throws Exception;

    void updateStatus(Long[] ids, String status) throws Exception;

    void del(Long[] ids) throws Exception;

    void updateMarketable(Long[] ids, String isMarketable) throws Exception;

    /**
     * 根据商品ID和状态查询Item表信息
     *
     * @param goodsIds
     * @param status
     * @return
     */
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) throws Exception;
}