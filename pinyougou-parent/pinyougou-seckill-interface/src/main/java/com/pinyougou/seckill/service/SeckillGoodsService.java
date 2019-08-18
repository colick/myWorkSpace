package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;
import entity.PageResult;

import java.util.List;

public interface SeckillGoodsService {

    PageResult<TbSeckillGoods> findByPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) throws Exception;

    TbSeckillGoods findById(Long id) throws Exception;

    void save(TbSeckillGoods seckillGoods) throws Exception;

    List<TbSeckillGoods> findList() throws Exception;

    TbSeckillGoods findByIdFromRedis(Long id) throws Exception;
}