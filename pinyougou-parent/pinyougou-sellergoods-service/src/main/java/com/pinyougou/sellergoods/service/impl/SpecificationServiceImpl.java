package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.vo.SpecificationVo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult<TbSpecification> findByPage(TbSpecification specification, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbSpecificationExample example = new TbSpecificationExample();
        //拼接条件
        if (specification != null) {
            TbSpecificationExample.Criteria criteria = example.createCriteria();
            if (null != specification.getSpecName() && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public SpecificationVo findBySpecificationId(Long id) throws Exception {
        //查询商品规格
        TbSpecification specification = specificationMapper.selectByPrimaryKey(id);

        //查询商品规格项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);

        return new SpecificationVo(specification, specificationOptionList);
    }

    @Override
    public void save(SpecificationVo specificationVo) throws Exception {
        if (specificationVo != null && specificationVo.getSpecification() != null) {
            TbSpecification tbSpecification = specificationVo.getSpecification();
            if (tbSpecification.getId() != null) {//修改逻辑
                specificationMapper.updateByPrimaryKeySelective(tbSpecification);
                //删除所有子项
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(tbSpecification.getId());
                specificationOptionMapper.deleteByExample(example);
                //重新新增
                if (!CollectionUtils.isEmpty(specificationVo.getSpecificationOptionList())) {
                    for (TbSpecificationOption tbSpecificationOption : specificationVo.getSpecificationOptionList()) {
                        tbSpecificationOption.setId(null);
                        tbSpecificationOption.setSpecId(tbSpecification.getId());
                        specificationOptionMapper.insertSelective(tbSpecificationOption);
                    }
                }
            } else {//新增逻辑
                specificationMapper.insertSelective(tbSpecification);
                if (!CollectionUtils.isEmpty(specificationVo.getSpecificationOptionList())) {
                    for (TbSpecificationOption tbSpecificationOption : specificationVo.getSpecificationOptionList()) {
                        tbSpecificationOption.setSpecId(tbSpecification.getId());
                        specificationOptionMapper.insertSelective(tbSpecificationOption);
                    }
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void delBySpecificationIds(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                //删除规格主表数据
                specificationMapper.deleteByPrimaryKey(id);
                //删除所有子项
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(id);
                specificationOptionMapper.deleteByExample(example);
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public List<Map<String, Object>> getOptionList() throws Exception {
        return specificationMapper.getOptionList();
    }
}
