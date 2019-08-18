package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper addressMapper;

    @Override
    public List<TbAddress> findListByUserId(String userId) throws Exception {
        System.out.println("根据userId=" + userId + "查询地址列表");
        TbAddressExample example = new TbAddressExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("is_default desc");
        return addressMapper.selectByExample(example);
    }

    @Override
    public TbAddress findById(Long id) throws Exception {
        return addressMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbAddress address) throws Exception {
        if (null == address) {
            throw new Exception("Data Is Empty!");
        }
        if (null != address.getId()) {//修改
            addressMapper.updateByPrimaryKeySelective(address);
        } else {//新增
            address.setCreateDate(new Date());
            address.setIsDefault("0");
            addressMapper.insertSelective(address);
        }
    }

    @Override
    public void deleteById(Long id) throws Exception {
        if (null == id) {
            throw new Exception("Data Is Empty!");
        }
        addressMapper.deleteByPrimaryKey(id);
    }
}