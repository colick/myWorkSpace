package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findByPage")
    public PageResult<TbItemCat> findByPage(@RequestBody TbItemCat brand, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return itemCatService.findByPage(brand, page, size);
    }

    @RequestMapping("/findById")
    public TbItemCat findById(Long id) throws Exception {
        return itemCatService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbItemCat brand) {
        try {
            itemCatService.save(brand);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            itemCatService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/findByParentId")
    public List<TbItemCat> findByParentId(Long parentId) throws Exception {
        return itemCatService.findByParentId(parentId);
    }

    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() throws Exception {
        return itemCatService.findAll();
    }
}