package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.vo.SpecificationVo;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    PageResult<TbSpecification> findByPage(TbSpecification specification, int pageNum, int pageSize) throws Exception;

    SpecificationVo findBySpecificationId(Long id) throws Exception;

    void save(SpecificationVo specificationVo) throws Exception;

    void delBySpecificationIds(Long[] ids) throws Exception;

    List<Map<String, Object>> getOptionList() throws Exception;
}
