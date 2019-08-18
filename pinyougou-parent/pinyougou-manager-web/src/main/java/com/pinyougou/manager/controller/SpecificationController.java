package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.vo.SpecificationVo;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findByPage")
    public PageResult<TbSpecification> findByPage(@RequestBody TbSpecification specification, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return specificationService.findByPage(specification, page, size);
    }

    @RequestMapping("/findById")
    public SpecificationVo findById(Long id) throws Exception {
        return specificationService.findBySpecificationId(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.save(specificationVo);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            specificationService.delBySpecificationIds(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/getOptionList")
    public List<Map<String, Object>> getOptionList() throws Exception {
        return specificationService.getOptionList();
    }
}
