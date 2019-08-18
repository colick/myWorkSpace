package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void add(GoodsVo goodsVo) throws Exception {
        if (goodsVo != null && goodsVo.getGoods() != null && goodsVo.getGoodsDesc() != null) {
            goodsVo.getGoods().setAuditStatus("0");//默认状态未审核
            goodsVo.getGoods().setIsDelete("0");//默认未删除
            goodsVo.getGoods().setIsMarketable("0");//新增默认未上架
            goodsMapper.insertSelective(goodsVo.getGoods());
            goodsVo.getGoodsDesc().setGoodsId(goodsVo.getGoods().getId());
            goodsDescMapper.insertSelective(goodsVo.getGoodsDesc());
            //保存itemList
            saveItemList(goodsVo);
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    private void setItemProperties(GoodsVo goodsVo, TbItem item) {
        //组装图片地址
        List<Map> imageList = JSON.parseArray(goodsVo.getGoodsDesc().getItemImages(), Map.class);
        if (!CollectionUtils.isEmpty(imageList)) {
            item.setImage((String) imageList.get(0).get("url"));
        }
        //第三级分类ID
        item.setCategoryid(goodsVo.getGoods().getCategory3Id());
        //第三级分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        if (itemCat != null) {
            item.setCategory(itemCat.getName());
        }
        //创建时间
        item.setCreateTime(new Date());
        //修改时间
        item.setUpdateTime(new Date());
        //goodsID
        item.setGoodsId(goodsVo.getGoods().getId());
        //sellerID
        item.setSellerId(goodsVo.getGoods().getSellerId());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
        if (seller != null) {
            item.setSeller(seller.getNickName());
        }
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goodsVo.getGoods().getBrandId());
        if (brand != null) {
            item.setBrand(brand.getName());
        }
    }

    @Override
    public PageResult<TbGoods> findByPage(TbGoods goods, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        //定义查询条件对象
        TbGoodsExample example = new TbGoodsExample();

        //查询条件
        TbGoodsExample.Criteria criteria = example.createCriteria();
        //费删除状态
        criteria.andIsDeleteEqualTo("0");
        //页面查询条件进行拼接
        if (goods != null) {
            if (null != goods.getGoodsName() && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (null != goods.getSellerId() && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (null != goods.getAuditStatus() && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
        }
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public GoodsVo findByGoodsId(Long id) throws Exception {
        GoodsVo goodsVo = new GoodsVo();
        goodsVo.setGoods(goodsMapper.selectByPrimaryKey(id));
        goodsVo.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        goodsVo.setItemList(itemMapper.selectByExample(example));
        return goodsVo;
    }

    @Override
    public TbGoods findById(Long id) throws Exception {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(GoodsVo goodsVo) throws Exception {
        if (goodsVo != null && goodsVo.getGoods() != null && goodsVo.getGoodsDesc() != null) {
            goodsVo.getGoods().setAuditStatus("0");//默认状态未审核
            goodsVo.getGoods().setIsDelete("0");//默认未删除
            goodsVo.getGoods().setIsMarketable("0");//编辑后默认未上架
            goodsMapper.updateByPrimaryKeySelective(goodsVo.getGoods());
            goodsDescMapper.updateByPrimaryKeySelective(goodsVo.getGoodsDesc());
            //删除原有的itemList
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsVo.getGoods().getId());
            itemMapper.deleteByExample(example);
            //重新新增itemList
            saveItemList(goodsVo);
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    private void saveItemList(GoodsVo goodsVo) {
        if ("1".equals(goodsVo.getGoods().getIsEnableSpec())) {
            if (goodsVo.getItemList() != null && goodsVo.getItemList().size() > 0) {
                for (TbItem item : goodsVo.getItemList()) {
                    //组装标题
                    item.setTitle(goodsVo.getGoods().getGoodsName());
                    Map<String, Object> map = JSON.parseObject(item.getSpec());
                    for (String key : map.keySet()) {
                        item.setTitle(item.getTitle() + " " + map.get(key));
                    }
                    //设置其他属性
                    setItemProperties(goodsVo, item);
                    itemMapper.insertSelective(item);
                }
            }
        } else {//没有勾选启用规格时
            TbItem item = new TbItem();
            //组装标题
            item.setTitle(goodsVo.getGoods().getGoodsName());
            item.setPrice(goodsVo.getGoods().getPrice());
            item.setNum(99999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");
            //设置其他属性
            setItemProperties(goodsVo, item);
            itemMapper.insertSelective(item);
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) throws Exception {
        if (ids != null && ids.length > 0 && status != null && status.trim().length() > 0) {
            for (Long id : ids) {
                TbGoods goods = goodsMapper.selectByPrimaryKey(id);
                if (goods != null) {
                    goods.setAuditStatus(status);
                    goodsMapper.updateByPrimaryKeySelective(goods);
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void del(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                TbGoods goods = goodsMapper.selectByPrimaryKey(id);
                if (goods != null) {
                    goods.setIsDelete("1");
                    goodsMapper.updateByPrimaryKeySelective(goods);
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public void updateMarketable(Long[] ids, String isMarketable) throws Exception {
        if (ids != null && ids.length > 0 && isMarketable != null && isMarketable.trim().length() > 0) {
            for (Long id : ids) {
                TbGoods goods = goodsMapper.selectByPrimaryKey(id);
                if (goods != null) {
                    goods.setIsMarketable(isMarketable);
                    goodsMapper.updateByPrimaryKeySelective(goods);
                }
            }
        } else {
            throw new Exception("Data Is Empty!");
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) throws Exception {
        if (goodsIds != null && goodsIds.length > 0 && null != status && status.trim().length() > 0) {
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdIn(Arrays.asList(goodsIds));
            criteria.andStatusEqualTo(status);
            return itemMapper.selectByExample(example);
        } else {
            System.out.println("参数异常,根据goodIds查询itemList返回空!");
            return null;
        }
    }
}