package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.TbSellerExample;
import com.pinyougou.sellergoods.service.SellerService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private TbSellerMapper sellerMapper;

    public PageResult<TbSeller> findByPage(TbSeller seller, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        TbSellerExample example = new TbSellerExample();
        TbSellerExample.Criteria criteria = example.createCriteria();
        if (seller != null) {
            if (seller.getName() != null && seller.getName().length() > 0) {
                criteria.andNameLike("%" + seller.getName() + "%");
            }
            if (seller.getNickName() != null && seller.getNickName().length() > 0) {
                criteria.andNickNameLike("%" + seller.getNickName() + "%");
            }
            if (seller.getEmail() != null && seller.getEmail().length() > 0) {
                criteria.andEmailLike("%" + seller.getEmail() + "%");
            }
            if (seller.getMobile() != null && seller.getMobile().length() > 0) {
                criteria.andMobileEqualTo(seller.getMobile());
            }
            if (seller.getTelephone() != null && seller.getTelephone().length() > 0) {
                criteria.andTelephoneEqualTo(seller.getTelephone());
            }
            if (seller.getStatus() != null && seller.getStatus().length() > 0) {
                criteria.andStatusEqualTo(seller.getStatus());
            }
            if (seller.getLinkmanName() != null && seller.getLinkmanName().length() > 0) {
                criteria.andLinkmanNameLike("%" + seller.getLinkmanName() + "%");
            }
            if (seller.getLinkmanQq() != null && seller.getLinkmanQq().length() > 0) {
                criteria.andLinkmanQqEqualTo(seller.getLinkmanQq());
            }
            if (seller.getLinkmanMobile() != null && seller.getLinkmanMobile().length() > 0) {
                criteria.andLinkmanMobileEqualTo(seller.getLinkmanMobile());
            }
            if (seller.getLinkmanEmail() != null && seller.getLinkmanEmail().length() > 0) {
                criteria.andLinkmanEmailLike("%" + seller.getLinkmanEmail() + "%");
            }
            if (seller.getLicenseNumber() != null && seller.getLicenseNumber().length() > 0) {
                criteria.andLicenseNumberLike("%" + seller.getLicenseNumber() + "%");
            }
//            if (seller.getTaxNumber() != null && seller.getTaxNumber().length() > 0) {
//                criteria.andTaxNumberLike("%" + seller.getTaxNumber() + "%");
//            }
//            if (seller.getOrgNumber() != null && seller.getOrgNumber().length() > 0) {
//                criteria.andOrgNumberLike("%" + seller.getOrgNumber() + "%");
//            }
//            if (seller.getLogoPic() != null && seller.getLogoPic().length() > 0) {
//                criteria.andLogoPicLike("%" + seller.getLogoPic() + "%");
//            }
//            if (seller.getBrief() != null && seller.getBrief().length() > 0) {
//                criteria.andBriefLike("%" + seller.getBrief() + "%");
//            }
//            if (seller.getLegalPerson() != null && seller.getLegalPerson().length() > 0) {
//                criteria.andLegalPersonLike("%" + seller.getLegalPerson() + "%");
//            }
//            if (seller.getLegalPersonCardId() != null && seller.getLegalPersonCardId().length() > 0) {
//                criteria.andLegalPersonCardIdLike("%" + seller.getLegalPersonCardId() + "%");
//            }
//            if (seller.getBankUser() != null && seller.getBankUser().length() > 0) {
//                criteria.andBankUserLike("%" + seller.getBankUser() + "%");
//            }
//            if (seller.getBankName() != null && seller.getBankName().length() > 0) {
//                criteria.andBankNameLike("%" + seller.getBankName() + "%");
//            }
        }

        Page<TbSeller> page = (Page<TbSeller>) sellerMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeller findById(String id) throws Exception {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(TbSeller seller) throws Exception {
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        sellerMapper.insertSelective(seller);
    }

    @Override
    public void updateStatus(String id, String status) throws Exception {
        if (null == status || (!"0".equals(status) && !"1".equals(status) && !"2".equals(status) && !"3".equals(status))) {
            throw new Exception("状态值不合法!");
        }
        TbSeller seller = sellerMapper.selectByPrimaryKey(id);
        if (null != seller) {
            seller.setStatus(status);
            sellerMapper.updateByPrimaryKey(seller);
        } else {
            throw new Exception("用户信息不存在!");
        }
    }
}