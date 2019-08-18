package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.mapper.TbContentCategoryMapper;
import com.pinyougou.pojo.TbContentCategory;
import com.pinyougou.pojo.TbContentCategoryExample;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<TbContentCategory> findAll() throws Exception {
        return contentCategoryMapper.selectByExample(null);
    }

    @Override
    public PageResult<TbContentCategory> findByPage(TbContentCategory contentCategory, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbContentCategoryExample example = new TbContentCategoryExample();
        //拼接条件
        if (contentCategory != null) {
            TbContentCategoryExample.Criteria criteria = example.createCriteria();
            if (null != contentCategory.getName() && contentCategory.getName().length() > 0) {
                criteria.andNameLike("%" + contentCategory.getName() + "%");
            }
        }
        Page<TbContentCategory> page = (Page<TbContentCategory>) contentCategoryMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbContentCategory findById(Long id) throws Exception {
        return contentCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbContentCategory contentCategory) throws Exception {
        if (contentCategory == null) {
            throw new Exception("Data Is Empty!");
        }
        if (contentCategory.getId() != null) {
            contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        } else {
            contentCategoryMapper.insertSelective(contentCategory);
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                contentCategoryMapper.deleteByPrimaryKey(id);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }
}