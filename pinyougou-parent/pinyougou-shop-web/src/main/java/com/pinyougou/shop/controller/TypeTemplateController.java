package com.pinyougou.shop.controller;

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

    @RequestMapping("/findById")
    public TbTypeTemplate findById(Long id) throws Exception {
        return typeTemplateService.findById(id);
    }

    @RequestMapping("/findSpecListByTypeId")
    public List<Map> findSpecListByTypeId(Long id) throws Exception {
        return typeTemplateService.findSpecListByTypeId(id);
    }
}