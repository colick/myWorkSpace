package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSeller;
import entity.PageResult;

public interface SellerService {

    PageResult<TbSeller> findByPage(TbSeller seller, int pageNum, int pageSize) throws Exception;

    TbSeller findById(String id) throws Exception;

    void add(TbSeller seller) throws Exception;

    void updateStatus(String id, String status) throws Exception;
}