package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;

import java.util.List;

public interface AddressService {

    List<TbAddress> findListByUserId(String userId) throws Exception;

    TbAddress findById(Long id) throws Exception;

    void save(TbAddress address) throws Exception;

    void deleteById(Long id) throws Exception;
}