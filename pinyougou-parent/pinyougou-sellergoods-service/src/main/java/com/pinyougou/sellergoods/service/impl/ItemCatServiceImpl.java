package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.sellergoods.service.ItemCatService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult<TbItemCat> findByPage(TbItemCat itemCat, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbItemCatExample example = new TbItemCatExample();
        //拼接条件
        if (itemCat != null) {
            TbItemCatExample.Criteria criteria = example.createCriteria();
            if (null != itemCat.getName() && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }
            if (null != itemCat.getParentId()) {
                criteria.andParentIdEqualTo(itemCat.getParentId());
            }
        }
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        saveToRedis();
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbItemCat findById(Long id) throws Exception {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbItemCat itemCat) throws Exception {
        if (itemCat != null) {
            if (itemCat.getId() != null) {//修改
                itemCatMapper.updateByPrimaryKeySelective(itemCat);
            } else {//新增
                itemCatMapper.insertSelective(itemCat);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                if (hasChild(id)) {
                    continue;
                } else {
                    itemCatMapper.deleteByPrimaryKey(id);
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    private boolean hasChild(Long id) {
        return itemCatMapper.selectCountByParentId(id) > 0L;
    }

    @Override
    public List<TbItemCat> findByParentId(Long parentId) throws Exception {
        if (null == parentId) {
            throw new Exception("ParentId Is Empty!");
        }
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        saveToRedis();
        return itemCatMapper.selectByExample(example);
    }

    private void saveToRedis() throws Exception {
        List<TbItemCat> list = this.findAll();
        System.out.println("缓存商品品牌和规格[商品分类名称--模板ID]开始");
        for (TbItemCat itemCat : list) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
        }
        System.out.println("缓存商品品牌和规格[商品分类名称--模板ID]结束");
    }

    @Override
    public List<TbItemCat> findAll() throws Exception {
        return itemCatMapper.selectByExample(null);
    }
}