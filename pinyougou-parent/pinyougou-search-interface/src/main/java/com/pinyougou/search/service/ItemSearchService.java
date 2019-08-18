package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map<String, Object> search(Map<String, Object> searchMap) throws Exception;

    boolean importItemListToSolr(List<TbItem> list);

    boolean deleteByGoodsIds(List goodsIdList);
}