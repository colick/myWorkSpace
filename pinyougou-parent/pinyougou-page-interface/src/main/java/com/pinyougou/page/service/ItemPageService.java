package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 根据SPUID生成商品详细页面
     *
     * @param goodsId
     * @return
     */
    boolean generateItemPage(Long goodsId);

    /**
     * 根据商品ids主键数组删除详细页面
     *
     * @param goodsIds
     * @return
     */
    boolean deleteItemPageByGoodIds(Long[] goodsIds);
}