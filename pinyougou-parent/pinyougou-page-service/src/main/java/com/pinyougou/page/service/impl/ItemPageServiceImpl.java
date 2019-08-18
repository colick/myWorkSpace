package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean generateItemPage(Long goodsId) {
        if (null == goodsId) {
            System.out.println("商品主键为空,生成详细页失败!");
            return false;
        }
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map<String, Object> paramMap = new HashMap<>();
            //查询商品SPU数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            paramMap.put("goods", goods);
            //查询商品SPU详细表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            paramMap.put("goodsDesc", goodsDesc);

            //商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            paramMap.put("itemCat1", itemCat1);
            paramMap.put("itemCat2", itemCat2);
            paramMap.put("itemCat3", itemCat3);

            //查询商品SKU数据集合
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");//有效的
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            paramMap.put("itemList", itemList);

            Writer out = new FileWriter(pageDir + goods.getId() + ".html");
            template.process(paramMap, out);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO异常,生成详细页失败!");
            return false;
        } catch (TemplateException e) {
            e.printStackTrace();
            System.out.println("模板生成异常,生成详细页失败!");
            return false;
        }
    }

    @Override
    public boolean deleteItemPageByGoodIds(Long[] goodsIds) {
        if (null == goodsIds || goodsIds.length == 0) {
            System.out.println("商品主键数组为空,删除详细页失败!");
            return false;
        }
        try {
            for (Long goodsId : goodsIds) {
                File file = new File(pageDir + goodsId + ".html");
                file.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO异常,删除详细页失败!");
            return false;
        }
    }
}