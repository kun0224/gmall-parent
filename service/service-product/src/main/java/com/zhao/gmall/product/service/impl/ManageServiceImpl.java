package com.zhao.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.gmall.common.cache.GmallCache;
import com.zhao.gmall.common.constant.RedisConst;
import com.zhao.gmall.list.product.*;
import com.zhao.gmall.product.mapper.*;
import com.zhao.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    /**
     * 查询所有的一级分类信息
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        //  select * from base_category2 where category1_id = category1Id;
        //  构建查询条件
        QueryWrapper<BaseCategory2> baseCategory2QueryWrapper = new QueryWrapper<>();
        baseCategory2QueryWrapper.eq("category1_id", category1Id);
        return baseCategory2Mapper.selectList(baseCategory2QueryWrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        //  select * from base_category3 where category2_id = category2Id;
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id", category2Id));
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    /**
     * 保存、修改
     *
     * @param baseAttrInfo
     */
    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() != null) {
            //修改
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            //添加
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        baseAttrValueMapper.delete(new QueryWrapper<BaseAttrValue>().eq("attr_id", baseAttrInfo.getId()));

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        if (!CollectionUtils.isEmpty(attrValueList)) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    /**
     * @param attrId
     * @return
     */
    @Override
    public BaseAttrInfo getBaseAttrInfo(Long attrId) {
        //  attrId = base_attr_value.attr_id = base_attr_info.id
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);

        //  判断平台属性不为空！
        if (baseAttrInfo != null) {
            //  baseAttrInfo 没有属性值集合字段
            //  给attrValueList 赋值！因为控制器要调用平台属性值集合！
            //  select * from base_attr_value where attr_id =  attrId;
            baseAttrInfo.setAttrValueList(this.getAttrValueList(attrId));
        }
        //  返回数据！
        return baseAttrInfo;
    }

    /**
     * 按照三级分类Id带分页的查询
     *
     * @param pageParam
     * @param spuInfo
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo) {

        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", spuInfo.getCategory3Id());
        queryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(pageParam, queryWrapper);

    }

    /**
     * 根据属性id获取属性值
     *
     * @param attrId
     * @return
     */
    private List<BaseAttrValue> getAttrValueList(Long attrId) {
        // select * from baseAttrValue where attrId = ?
        QueryWrapper queryWrapper = new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id", attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(queryWrapper);
        return baseAttrValueList;
    }

    /**
     * 添加spu
     *
     * @param spuInfo
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {

        spuInfoMapper.insert(spuInfo);

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }
        //销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {

            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {

                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {

                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {

                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);

                    }
                }
            }
        }


    }

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     */
    @Override
    public List<SpuImage> getspuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);
        return spuImages;
    }

    /**
     * 根据spuId 查询销售属性集合
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {

        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getspuSaleAttrList(spuId);
        return spuSaleAttrList;
    }

    /**
     * 添加sku
     *
     * @param skuInfo
     */
    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {

        skuInfoMapper.insert(skuInfo);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    /**
     * sku分页
     *
     * @param skuInfoPage
     * @return
     */
    @Override
    public IPage<SkuInfo> getPage(Page<SkuInfo> skuInfoPage) {

        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage, queryWrapper);
    }

    /**
     * 商品上架
     *
     * @param skuId
     */
    @Override
    @Transactional
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 商品下架
     *
     * @param skuId
     */
    @Override
    @Transactional
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }


    /**
     * 根据skuid获取sku信息
     *
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SkuIngfo")
    public SkuInfo getSkuIngfo(Long skuId) {
        return getSkuInfoDB(skuId);
    }



    //     根据skuid获取sku信息
    //     利用redis-set 命令来实现分布式锁！
    private SkuInfo getSkuInfoByRedis(Long skuId) {
        //从缓存中获取数据
        SkuInfo skuInfo = null;
        try {
            String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //判断缓存是否存在
            if (skuInfo == null) {
                //缓存没有数据，要到数据库中查询数据，（防止缓存击穿）要考虑加锁
                String lockskuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                //给锁添加随机数
                String uuid = UUID.randomUUID().toString();
                //准备上锁
                Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockskuKey, uuid, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                //表示上锁成功
                if (flag) {
                    System.out.println("获取到分布式锁！");
                    //查询数据，并将数据放入到缓存
                    skuInfo = getSkuInfoDB(skuId);
                    //防止缓存穿透
                    if (skuInfo == null) {
                        SkuInfo skuInfo1 = new SkuInfo();
                        //放入缓存
                        redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        //返回数据
                        return skuInfo;
                    }

                    //缓存中存储商品详情的时候，我们需要有个过期时间
                    redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);

                    // 解锁：使用lua 脚本解锁
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    //执行lua脚本
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    redisScript.setScriptText(script);
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(lockskuKey), uuid);
                    //返回数据
                    return skuInfo;
                } else {
                    //等待睡觉
                    try {
                        Thread.sleep(1000);
                        //自旋
                        return getSkuIngfo(skuId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //返回数据
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 为了防止缓存宕机：从数据库中获取数据
        return getSkuInfoDB(skuId);
    }

    //根据skuid获取sku信息
    private SkuInfo getSkuInfoDB(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        if (skuInfo != null) {
            QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sku_id", skuId);
            List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);
            skuInfo.setSkuImageList(skuImages);
        }
        return skuInfo;
    }


    /**
     * 通过三级分类id查询分类信息
     *
     * @param category3Id
     * @return
     */
    @Override
    @GmallCache(prefix = "CategoryViewByCategory3Id:")
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 获取sku最新价格
     *
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SkuPrice:")
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (null != skuId) {
            return skuInfo.getPrice();
        }
        return new BigDecimal("0");
    }

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SpuSaleAttrListCheckBySku:")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
        return spuSaleAttrList;
    }

    /**
     * 根据spuId 查询map 集合数据
     *
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SkuValueIdsMap:")
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object, Object> map = new HashMap<>();
        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (!CollectionUtils.isEmpty(mapList)) {
            for (Map skumap : mapList) {
                map.put(skumap.get("value_ids"), skumap.get("sku_id"));
            }
        }
        return map;
    }

    /**
     *
     * @return
     */
    @Override
    @GmallCache(prefix = "index:")
    //分布式锁
    public List<JSONObject> getBaseCategoryList() {
        //声明json集合
        ArrayList<JSONObject> list = new ArrayList<>();
        //声明获取所有分类数据集合
        List<BaseCategoryView> categoryViewList = baseCategoryViewMapper.selectList(null);
        //对集合按照一级进行分组
        Map<Long, List<BaseCategoryView>> category1Map = categoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        int index = 1;
        //获取一级的信息
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : category1Map.entrySet()) {
            //获取一级分类Id
            Long category1Id = entry1.getKey();
            //获取一级分类下面的所有集合
            List<BaseCategoryView> category2List1 = entry1.getValue();
            //
            JSONObject category1 = new JSONObject();
            category1.put("index", index);
            category1.put("categoryId", category1Id);
            category1.put("categoryName", category2List1.get(0).getCategory1Name());

            index++;
            //循环获取二级分类数据
            Map<Long, List<BaseCategoryView>> category2Map = category2List1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //声明存放二级分类对象的集合
            List<JSONObject> category2Child = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : category2Map.entrySet()) {
                //获取二级分类Id
                Long category2Id = entry2.getKey();
                //获取二级分类下面的所有集合
                List<BaseCategoryView> category3List1 = entry2.getValue();
                //
                JSONObject category2 = new JSONObject();
                category2.put("categoryId", category2Id);
                category2.put("categoryName", category3List1.get(0).getCategory2Name());

                List<JSONObject> category3Child = new ArrayList<>();
                //循环三级分类数据
                category3List1.stream().forEach(category3View -> {
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId", category3View.getCategory3Id());
                    category3.put("categoryName", category3View.getCategory3Name());
                    category3Child.add(category3);
                });
                // 将三级分类数据集合添加到二级分类
                category2.put("categoryChild", category3Child);

                category2Child.add(category2);
            }
            //将二级放入一级
            category1.put("categoryChild", category2Child);
            //将一级放入json集合
            list.add(category1);
        }
        return list;
    }


    @Override
    public BaseTrademark getTrademarkByTmId(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {

        // 多表关联查询
        return baseAttrInfoMapper.selectAttrList(skuId);
    }


}
