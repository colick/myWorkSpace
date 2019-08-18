package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() throws Exception {
        return brandService.findAll();
    }

    @RequestMapping("/findByPage")
    public PageResult<TbBrand> findByPage(@RequestBody TbBrand brand, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return brandService.findByPage(brand, page, size);
    }

    @RequestMapping("/findById")
    public TbBrand findById(Long id) throws Exception {
        return brandService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbBrand brand) {
        try {
            brandService.save(brand);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/getOptionList")
    public List<Map<String, Object>> getOptionList() throws Exception {
        return brandService.getOptionList();
    }
}