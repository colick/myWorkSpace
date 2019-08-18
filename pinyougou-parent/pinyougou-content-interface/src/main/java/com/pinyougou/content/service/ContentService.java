package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import entity.PageResult;

import java.util.List;

public interface ContentService {

    PageResult<TbContent> findByPage(TbContent content, int pageNum, int pageSize) throws Exception;

    TbContent findById(Long id) throws Exception;

    void save(TbContent content) throws Exception;

    void delete(Long[] ids) throws Exception;

    void updateStatus(Long[] ids, String status) throws Exception;

    List<TbContent> findByCategoryId(Long categoryId) throws Exception;
}
