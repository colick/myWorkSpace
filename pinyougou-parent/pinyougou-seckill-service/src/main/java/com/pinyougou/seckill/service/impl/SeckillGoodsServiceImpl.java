package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult<TbSeckillGoods> findByPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        //拼接条件
        if (seckillGoods != null) {
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            if (null != seckillGoods.getTitle() && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (null != seckillGoods.getStatus() && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusEqualTo(seckillGoods.getStatus());
            }
            if (null != seckillGoods.getSellerId() && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(seckillGoods.getSellerId());
            }
        }
        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbSeckillGoods findById(Long id) throws Exception {
        if (null == id) {
            throw new Exception("Id Is Empty!");
        }
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbSeckillGoods seckillGoods) throws Exception {
        if (seckillGoods == null) {
            throw new Exception("Data Is Empty!");
        }
        if (seckillGoods.getId() != null) {
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
        } else {
            seckillGoods.setCreateTime(new Date());
            seckillGoods.setStatus("0");//默认未审核
            seckillGoods.setStockCount(seckillGoods.getNum());//新增时剩余库存默认与库存数量一致
            seckillGoodsMapper.insertSelective(seckillGoods);
        }
    }

    @Override
    public List<TbSeckillGoods> findList() throws Exception {
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        if (CollectionUtils.isEmpty(seckillGoodsList)) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//已审核
            criteria.andStockCountGreaterThan(0);//库存大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            criteria.andEndTimeGreaterThan(new Date());
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(seckillGoodsList)) {
                for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
                }
            }
        } else {
            System.out.println("从缓存中获取秒杀商品集合");
        }
        return seckillGoodsList;
    }

    @Override
    public TbSeckillGoods findByIdFromRedis(Long id) throws Exception {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }
}