package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult<TbTypeTemplate> findByPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbTypeTemplateExample example = new TbTypeTemplateExample();
        //拼接条件
        if (typeTemplate != null) {
            TbTypeTemplateExample.Criteria criteria = example.createCriteria();
            if (null != typeTemplate.getName() && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
        }
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        saveToRedis();
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    private void saveToRedis() throws Exception {
        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(null);
        System.out.println("缓存商品品牌和规格[模板ID--品牌List/规格List]开始");
        for (TbTypeTemplate typeTemplate : list) {
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
            List<Map> specList = findSpecListByTypeId(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
        }
        System.out.println("缓存商品品牌和规格[模板ID--品牌List/规格List]结束");
    }

    @Override
    public TbTypeTemplate findById(Long id) throws Exception {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbTypeTemplate typeTemplate) throws Exception {
        if (typeTemplate.getId() != null) {
            typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
        } else {
            typeTemplateMapper.insertSelective(typeTemplate);
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                typeTemplateMapper.deleteByPrimaryKey(id);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public List<Map<String, Object>> getOptionList() throws Exception {
        return typeTemplateMapper.getOptionList();
    }

    @Override
    public List<Map> findSpecListByTypeId(Long id) throws Exception {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> list = JSONObject.parseArray(typeTemplate.getSpecIds(), Map.class);
        if (!CollectionUtils.isEmpty(list)) {
            TbSpecificationOptionExample example;
            for (Map map : list) {

                example = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
                List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
                map.put("options", optionList);
            }
        }
        return list;
    }
}