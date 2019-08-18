package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    PageResult<TbTypeTemplate> findByPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) throws Exception;

    TbTypeTemplate findById(Long id) throws Exception;

    void save(TbTypeTemplate typeTemplate) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<Map<String,Object>> getOptionList() throws Exception;

    List<Map> findSpecListByTypeId(Long id) throws Exception;
}