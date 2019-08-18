package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContentCategory;
import entity.PageResult;

import java.util.List;

public interface ContentCategoryService {

    List<TbContentCategory> findAll() throws Exception;

    PageResult<TbContentCategory> findByPage(TbContentCategory contentCategory, int pageNum, int pageSize) throws Exception;

    TbContentCategory findById(Long id) throws Exception;

    void save(TbContentCategory contentCategory) throws Exception;

    void delete(Long[] ids) throws Exception;
}
