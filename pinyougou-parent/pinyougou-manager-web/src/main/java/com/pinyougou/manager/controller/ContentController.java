package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByPage")
    public PageResult<TbContent> findByPage(@RequestBody TbContent content, int page, int size) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        return contentService.findByPage(content, page, size);
    }

    @RequestMapping("/findById")
    public TbContent findById(Long id) throws Exception {
        return contentService.findById(id);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TbContent content) {
        try {
            contentService.save(content);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            contentService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@PathVariable String status, Long[] ids) {
        try {
            contentService.updateStatus(ids, status);
            return new Result(true, "操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败!");
        }
    }
}