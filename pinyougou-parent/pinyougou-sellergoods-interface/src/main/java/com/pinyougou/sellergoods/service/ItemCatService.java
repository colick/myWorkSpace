package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbItemCat;
import entity.PageResult;

import java.util.List;

public interface ItemCatService {

    PageResult<TbItemCat> findByPage(TbItemCat itemCat, int pageNum, int pageSize) throws Exception;

    TbItemCat findById(Long id) throws Exception;

    void save(TbItemCat itemCat) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<TbItemCat> findByParentId(Long parentId) throws Exception;

    List<TbItemCat> findAll() throws Exception;
}