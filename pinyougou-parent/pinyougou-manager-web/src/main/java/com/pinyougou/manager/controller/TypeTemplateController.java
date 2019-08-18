package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/findByPage")
    public PageResult<TbTypeTemplate> findByPage(@RequestBody TbTypeTemplate typeTemplate, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return typeTemplateService.findByPage(typeTemplate, page, size);
    }

    @RequestMapping("/findById")
    public TbTypeTemplate findById(Long id) throws Exception {
        return typeTemplateService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbTypeTemplate typeTemplate) {
        try {
            typeTemplateService.save(typeTemplate);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            typeTemplateService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/getOptionList")
    public List<Map<String, Object>> getOptionList() throws Exception {
        return typeTemplateService.getOptionList();
    }

    @RequestMapping("/findSpecListByTypeId")
    public List<Map> findSpecListByTypeId(Long id) throws Exception {
        return typeTemplateService.findSpecListByTypeId(id);
    }
}