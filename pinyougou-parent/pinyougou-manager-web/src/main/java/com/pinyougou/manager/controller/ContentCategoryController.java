package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.pojo.TbContentCategory;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/findAll")
    public List<TbContentCategory> findAll() throws Exception {
        return contentCategoryService.findAll();
    }

    @RequestMapping("/findByPage")
    public PageResult<TbContentCategory> findByPage(@RequestBody TbContentCategory contentCategory, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return contentCategoryService.findByPage(contentCategory, page, size);
    }

    @RequestMapping("/findById")
    public TbContentCategory findById(Long id) throws Exception {
        return contentCategoryService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbContentCategory contentCategory) {
        try {
            contentCategoryService.save(contentCategory);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            contentCategoryService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
}