package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() throws Exception {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult<TbBrand> findByPage(TbBrand brand, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbBrandExample example = new TbBrandExample();
        //拼接条件
        if (brand != null) {
            Criteria criteria = example.createCriteria();
            if (null != brand.getName() && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if (null != brand.getFirstChar() && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbBrand findById(Long id) throws Exception {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbBrand brand) throws Exception {
        if (brand == null) {
            throw new Exception("Data Is Empty!");
        }
        checkName(brand.getName(), brand.getId());
        if (brand.getId() != null) {
            brandMapper.updateByPrimaryKeySelective(brand);
        } else {
            brandMapper.insertSelective(brand);
        }
    }

    private void checkName(String name, Long id) throws Exception {
        int count = brandMapper.selectCountByName(name, id);
        if (count > 0) {
            throw new Exception("Brand Name Is Duplicate!");
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                brandMapper.deleteByPrimaryKey(id);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public List<Map<String, Object>> getOptionList() throws Exception {
        return brandMapper.getOptionList();
    }
}