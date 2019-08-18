package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findByParentId")
    public List<TbItemCat> findByParentId(Long parentId) throws Exception {
        return itemCatService.findByParentId(parentId);
    }

    @RequestMapping("/findById")
    public TbItemCat findById(Long id) throws Exception {
        return itemCatService.findById(id);
    }

    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() throws Exception {
        return itemCatService.findAll();
    }
}