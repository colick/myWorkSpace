package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<TbBrand> findAll() throws Exception;

    PageResult<TbBrand> findByPage(TbBrand brand, int pageNum, int pageSize) throws Exception;

    TbBrand findById(Long id) throws Exception;

    void save(TbBrand brand) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<Map<String, Object>> getOptionList() throws Exception;
}
