package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult<TbContent> findByPage(TbContent content, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public TbContent findById(Long id) throws Exception {
        return contentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbContent content) throws Exception {
        if (content == null) {
            throw new Exception("Data Is Empty!");
        }
        if (content.getId() != null) {
            Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
            redisTemplate.boundHashOps("content").delete(oldCategoryId);
            contentMapper.updateByPrimaryKeySelective(content);
            if (oldCategoryId.longValue() != content.getCategoryId().longValue()) {
                redisTemplate.boundHashOps("content").delete(content.getCategoryId());
            }
        } else {
            if (null == content.getStatus() || "".equals(content.getStatus().trim())) {
                content.setStatus("0");
            }
            contentMapper.insertSelective(content);
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
                redisTemplate.boundHashOps("content").delete(categoryId);
                contentMapper.deleteByPrimaryKey(id);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) throws Exception {
        if (ids != null && ids.length > 0 && status != null && status.trim().length() > 0) {
            for (Long id : ids) {
                TbContent content = contentMapper.selectByPrimaryKey(id);
                if (content != null) {
                    content.setStatus(status);
                    contentMapper.updateByPrimaryKeySelective(content);
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public List<TbContent> findByCategoryId(Long categoryId) throws Exception {
        if (categoryId == null) {
            throw new Exception("Data Is Empty!");
        }
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        if (CollectionUtils.isEmpty(contentList)) {
            TbContentExample example = new TbContentExample();
            TbContentExample.Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("sort_order");
            contentList = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("content").put(categoryId, contentList);
            System.out.println("content从数据库中获取!");
            return contentList;
        } else {
            System.out.println("content从缓存中获取!");
            return contentList;
        }
    }
}