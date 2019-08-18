package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //处理关键字空格问题
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        //查询结果集
        returnMap.putAll(this.searchBySolr(searchMap));
        //查询商品分类
        List<String> categoryList = this.getCategoryList(searchMap);
        returnMap.put("categoryList", categoryList);
        //查询品牌和规格列表
        if (!CollectionUtils.isEmpty(categoryList)) {
            String categoryName = (String) searchMap.get("category");
            if (!"".equals(categoryName)) {
                returnMap.putAll(this.getBrandAndSpecList(categoryName));
            } else {
                //初始化使用第一个商品分类名称作为查询依据
                returnMap.putAll(this.getBrandAndSpecList(categoryList.get(0)));
            }
        }
        return returnMap;
    }

    private Map<String, Object> getBrandAndSpecList(String category) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Long typeTemplateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (null != typeTemplateId) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
            map.put("brandList", brandList);//返回值添加品牌列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeTemplateId);
            map.put("specList", specList);//返回值添加规格列表
        }
        return map;
    }

    private List<String> getCategoryList(Map<String, Object> searchMap) throws Exception {
        //构建返回值
        List<String> categoryList = new ArrayList<>();
        //构建查询条件对象
        Query query = new SimpleQuery();
        //构建查询条件筛选对象
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //构建分组设置对象
        GroupOptions groupOptions = new GroupOptions();
        //设置分组字段,对应sechma.xml中的字段
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> result = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> entries = result.getGroupEntries();
        List<GroupEntry<TbItem>> contentList = entries.getContent();
        if (!CollectionUtils.isEmpty(contentList)) {
            for (GroupEntry<TbItem> groupEntry : contentList) {
                categoryList.add(groupEntry.getGroupValue());
            }
        }
        return categoryList;
    }

    private Map<String, Object> searchBySolr(Map<String, Object> searchMap) throws Exception {
        //构建返回值
        Map<String, Object> map = new HashMap<>();
        //构建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮选项参数对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮的列名,对应sechma.xml中的字段
        highlightOptions.addField("item_title");
        //设置高亮字段添加前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮字段添加后缀
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        //加载页面查询参数----[关键字]
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //构建过滤条件
        //商品分类条件过滤
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category")
                    .is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌条件过滤
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand")
                    .is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //规格条件过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key)
                        .is(specMap.get(key));
                FilterQuery filterQuery = new SimpleQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //价格区间条件过滤
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");
            String[] priceArray = priceStr.split("-");
            if (!"*".equals(priceArray[1])) {
                Criteria filterCriteria = new Criteria("item_price")
                        .between(priceArray[0], priceArray[1]);
                FilterQuery filterQuery = new SimpleQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            } else {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(priceArray[0]);
                FilterQuery filterQuery = new SimpleQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //排序条件
        if (!"".equals(searchMap.get("sortField"))) {
            String sortField = (String) searchMap.get("sortField");
            String sortValue = (String) searchMap.get("sort");
            if ("ASC".equals(sortValue)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            } else {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //分页参数条件
        Integer pageNo = (Integer) searchMap.get("pageNo");//页码
        if (pageNo == null) {
            pageNo = 1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if (pageSize == null) {
            pageSize = 20;//默认20
        }
        //设值分页参数
        query.setRows(pageSize);
        query.setOffset((pageNo - 1) * query.getRows());

        //分页查询
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        if (!CollectionUtils.isEmpty(entryList)) {
            for (HighlightEntry<TbItem> entry : entryList) {
                if (!CollectionUtils.isEmpty(entry.getHighlights())
                        && !CollectionUtils.isEmpty(entry.getHighlights().get(0).getSnipplets())) {
                    entry.getEntity().setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());//总页数
        map.put("totalCount", page.getTotalElements());//总记录数
        return map;
    }

    @Override
    public boolean importItemListToSolr(List<TbItem> list) {
        if (!CollectionUtils.isEmpty(list)) {
            try {
                Map specMap;
                for (TbItem item : list) {
                    specMap = JSON.parseObject(item.getSpec(), Map.class);
                    item.setSpecMap(specMap);
                }
                solrTemplate.saveBeans(list);
                solrTemplate.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("导入itemList至solr库异常,导入失败：" + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteByGoodsIds(List goodsIdList) {
        if (!CollectionUtils.isEmpty(goodsIdList)) {
            try {
                Query query = new SimpleQuery();
                Criteria criteria = new Criteria("item_goodsid");
                criteria.in(goodsIdList);
                query.addCriteria(criteria);
                solrTemplate.delete(query);
                solrTemplate.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("从solr库中删除指定goodId数据异常,删除失败：" + e.getMessage());
                return false;
            }
        }
        return false;
    }
}