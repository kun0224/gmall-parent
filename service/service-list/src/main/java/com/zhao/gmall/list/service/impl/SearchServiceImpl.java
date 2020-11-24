package com.zhao.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.repository.GoodsRepository;
import com.zhao.gmall.list.service.SearchService;
import com.zhao.gmall.list.list.*;
import com.zhao.gmall.list.product.BaseAttrInfo;
import com.zhao.gmall.list.product.BaseCategoryView;
import com.zhao.gmall.list.product.BaseTrademark;
import com.zhao.gmall.list.product.SkuInfo;
import com.zhao.gmall.product.client.ProductFeignClient;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    //  ElasticsearchRepository 接口 最终CrudRepository 中有对当前es 有增删改查方法
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    //  商品的上架
    @Override
    public void upperGoods(Long skuId) {
        Goods goods = new Goods();
        //  获取skuInfo 数据
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo!=null){
            goods.setId(skuId);
            goods.setTitle(skuInfo.getSkuName());
            goods.setCreateTime(new Date());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());

            //  获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            if (categoryView!=null){
                goods.setCategory1Id(categoryView.getCategory1Id());
                goods.setCategory1Name(categoryView.getCategory1Name());
                goods.setCategory2Id(categoryView.getCategory2Id());
                goods.setCategory2Name(categoryView.getCategory2Name());
                goods.setCategory3Id(categoryView.getCategory3Id());
                goods.setCategory3Name(categoryView.getCategory3Name());

            }

            //  品牌数据
            BaseTrademark trademark = productFeignClient.getTrademark(skuInfo.getTmId());
            if (trademark!=null){
                goods.setTmId(trademark.getId());
                goods.setTmName(trademark.getTmName());
                goods.setTmLogoUrl(trademark.getLogoUrl());
            }

            //  平台属性 List<SearchAttr> attrs;
            //  声明一个集合
            //List<SearchAttr> attrs = new ArrayList<>();
            List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
//            if (!CollectionUtils.isEmpty(attrList)){
//                for (BaseAttrInfo baseAttrInfo : attrList) {
//                    //  声明一个平台属性对象
//                    SearchAttr searchAttr = new SearchAttr();
//
//                    searchAttr.setAttrId(baseAttrInfo.getId());
//                    searchAttr.setAttrName(baseAttrInfo.getAttrName());
//                    searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
//                    //  将每个属性对象存入集合
//                    attrs.add(searchAttr);
//                }
//            }
//            goods.setAttrs(attrs);
            //  Function 有参数，有返回值

            List<SearchAttr> attrs = attrList.stream().map(baseAttrInfo -> {
                //  声明一个平台属性对象
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(baseAttrInfo.getId());
                searchAttr.setAttrName(baseAttrInfo.getAttrName());
                searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());

                return searchAttr;
            }).collect(Collectors.toList());

            goods.setAttrs(attrs);
        }
        // 保存方法
        this.goodsRepository.save(goods);
    }

    //  商品的下架
    @Override
    public void lowerGoods(Long skuId) {
        //  删除方法
        this.goodsRepository.deleteById(skuId);
    }

    /**
     * 检索数据接口
     * @param searchParam
     * @return
     * @throws Exception
     */
    @Override
    public SearchResponseVo search(SearchParam searchParam) throws Exception {
        /*
        1.  生成一个dsl 语句
        2.  利用当前生成的dsl 语句进行查询数据
        3.  将查询到的结果集封装到 SearchResponseVo
         */
        SearchRequest searchRequest = this.buildQueryDsl(searchParam);

        //  查询之后的结果集
        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //  将数据结果集进行转换
        SearchResponseVo searchResponseVo = parseSearchResult(searchResponse);

        //  当前页显示多少条数据
        searchResponseVo.setPageSize(searchParam.getPageSize());
        searchResponseVo.setPageNo(searchParam.getPageNo());
        //  总页数！ 10 ,3 ,4  9,3,3
        //  long totalPages = searchResponseVo.getTotal()%searchParam.getPageSize()==0?searchResponseVo.getTotal()/searchParam.getPageSize():searchResponseVo.getTotal()/searchParam.getPageSize()+1;
        //  总的页数！
        long totalPages = (searchResponseVo.getTotal()+searchParam.getPageSize()-1)/searchParam.getPageSize();
        searchResponseVo.setTotalPages(totalPages);
        return searchResponseVo;
    }

    //  转换结果集
    private SearchResponseVo parseSearchResult(SearchResponse searchResponse) {
        //  声明对象
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        /*
         private List<SearchResponseTmVo> trademarkList;
         private List<SearchResponseAttrVo> attrsList = new ArrayList<>();
         private List<Goods> goodsList = new ArrayList<>();
         private Long total;//总记录数
         */
        //  获取品牌：从聚合中获取
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        //  根据key 获取 tmIdAgg
        //  获取buckets 应该做个数据类型转换 Aggregation --> ParsedLongTerms
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregationMap.get("tmIdAgg");
        //  Function T R
        List<SearchResponseTmVo> trademarkList = tmIdAgg.getBuckets().stream().map((bucket) -> {
            //  创建一个对象
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            String tmId = ((Terms.Bucket) bucket).getKeyAsString();
            //  赋值品牌Id
            searchResponseTmVo.setTmId(Long.parseLong(tmId));
            //  赋值品牌的名称 Aggregation ---> ParsedStringTerms
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("tmNameAgg");
            //  获取品的名称
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmName(tmName);

            //  赋值品牌的LogoUrl
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);

            return searchResponseTmVo;
        }).collect(Collectors.toList());

        //  赋值
        searchResponseVo.setTrademarkList(trademarkList);

        //  获取平台属性：从聚合中获取 数据类型 nested Aggregation -->
        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        //  平台属性Id 聚合
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().asMap().get("attrIdAgg");
        //  获取平台属性集合
        List<SearchResponseAttrVo> attrsList = attrIdAgg.getBuckets().stream().map((bucket) -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //  赋值属性Id
            Number keyAsNumber = ((Terms.Bucket) bucket).getKeyAsNumber();
            searchResponseAttrVo.setAttrId(keyAsNumber.longValue());
            //  赋值属性名称
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);

            //  赋值属性值名称
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("attrValueAgg");
            //  平台属性值集合数据

            List<? extends Terms.Bucket> buckets = attrValueAgg.getBuckets();
            //   Terms.Bucket::getKeyAsString 相当于  bucket1.getKeyAsString()
            List<String> strList = buckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
            //  声明一个集合
            //            List<String> strList = new ArrayList<>();
            //            //  循环获取数据
            //            for (Terms.Bucket bucket1 : buckets) {
            //                //  获取到属性值名称
            //                String attrValueName = bucket1.getKeyAsString();
            //                strList.add(attrValueName);
            //            }
            //  将平台属性值集合放入
            searchResponseAttrVo.setAttrValueList(strList);
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        //  赋值
        searchResponseVo.setAttrsList(attrsList);

        SearchHits hits = searchResponse.getHits();
        //  获取数据
        SearchHit[] subHits = hits.getHits();
        List<Goods> goodsList = new ArrayList<>();
        //  判断是否有数据
        if (subHits!=null && subHits.length>0){
            for (SearchHit subHit : subHits) {
                //  获取source
                String sourceJson = subHit.getSourceAsString();
                //  转换为Goods
                Goods goods = JSON.parseObject(sourceJson, Goods.class);
                //  获取高亮字段 根据三级分类Id 获取的时候，根本没有高亮！
                if (subHit.getHighlightFields().get("title")!=null){
                    Text title = subHit.getHighlightFields().get("title").getFragments()[0];
                    //  获取高亮之后的字段
                    goods.setTitle(title.toString());
                }
                //  添加到集合
                goodsList.add(goods);
            }
        }
        //  获取商品sku集合
        searchResponseVo.setGoodsList(goodsList);

        // 赋值总条数
        searchResponseVo.setTotal(hits.totalHits);
        return searchResponseVo;
    }

    //  生成dsl 语句 SearchRequest
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        //  构建查询器{}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 创建一个 QueryBuilder 接口 的实体类
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //  判断用户是否根据关键字检索
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            //  Operator.AND 分词之后的数据，必须同时存在才会查询！
            // {must ---match ---title }
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",searchParam.getKeyword()).operator(Operator.AND));
        }

        //  判断分类Id
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category3Id",searchParam.getCategory3Id()));
        }

        if (!StringUtils.isEmpty(searchParam.getCategory2Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2Id",searchParam.getCategory2Id()));
        }

        if (!StringUtils.isEmpty(searchParam.getCategory1Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1Id",searchParam.getCategory1Id()));
        }

        //  有关于品牌 前台传递的数据 trademark=2:华为
        String trademark = searchParam.getTrademark();
        if(!StringUtils.isEmpty(trademark)){
            // trademark=2:华为
            // 获取到品牌的Id 才能进行过滤
            String[] split = trademark.split(":");
            if (split!=null && split.length==2){
                //  品牌的Id split[0]
                boolQueryBuilder.filter(QueryBuilders.termQuery("tmId",split[0]));
            }
        }
        //  有关于平台属性 props=23:8G:运行内存&props=24:256G:机身内存
        //  平台属性Id：平台属性值：平台属性名
        String[] props = searchParam.getProps();
        if (props!=null && props.length>0){
            // 循环遍历
            for (String prop : props) {
                //  每个prop 是什么样的结构  23:8G:运行内存 台属性Id 0：平台属性值 1：平台属性名 2
                //  org.apache.commons.lang3.StringUtils.split(":")
                String[] split = prop.split(":");
                if (split!=null && split.length==3){
                    // 平台属性值过滤属于nested！ 外层
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    // 嵌套查询子查询 内层
                    BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();

                    //  拼接查询数据
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));

                    //  将内存的bool 放入外层 must -- nested ---
                    boolQuery.must(QueryBuilders.nestedQuery("attrs",subBoolQuery, ScoreMode.None));

                    //  将外层的bool 放入filter
                    boolQueryBuilder.filter(boolQuery);
                }
            }
        }
        //  有关于排序
        //  1:hotScore 2:price
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            //  按照排序 前端传递过来的数据 ：  1:asc 1:desc  2:asc  2:desc
            String[] split = order.split(":");
            if (split!=null && split.length==2){
                String field = null;
                // 判断
                switch (split[0]){
                    case "1":
                        // 按照 hotScore
                        field = "hotScore";
                        break;
                    case "2":
                        field = "price";
                        break;
                }
                // 开始排序 如果前端传递过来的是asc ,则升序，否则降序
                searchSourceBuilder.sort(field,"asc".equals(split[1])? SortOrder.ASC:SortOrder.DESC);
            }else {
                //  给默认排序规则
                searchSourceBuilder.sort("hotScore",SortOrder.DESC);
            }
        }

        //  分页
        //  (总条数 + pageSize - 1)/ pageSize;
        //   (pageNo - 1)*pageSize;
        //   4  2 |  pageNo = 1  0,2 pageNo = 2  2,2
        int from = (searchParam.getPageNo()-1)*searchParam.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(searchParam.getPageSize());
        //  高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style=color:red>");
        highlightBuilder.postTags("</span>");
        //  将设置好的高亮对象放入查询器！
        searchSourceBuilder.highlighter(highlightBuilder);

        //  聚合： 品牌 + 平台属性
        //   tmIdAgg --  terms  -- field
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        //  品牌聚合
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        // 平台属性聚合 属于nested 聚合
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","attrs")
            .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
            .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                    .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

        // {query -- bool}
        searchSourceBuilder.query(boolQueryBuilder);

        //  设置查询的字段 设置哪些字段查询的时候，需要显示值！
        searchSourceBuilder.fetchSource(new String[]{"id","defaultImg","title","price"},null);
        //  GET /goods/info/_search
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");

        String dsl = searchSourceBuilder.toString();
        System.out.println("DSL:\t"+dsl);
        //  将查询的dsl 语句放入searchRequest;
        searchRequest.source(searchSourceBuilder);
        //  返回
        return searchRequest;
    }

    @Override
    public Result incrHotcore(Long skuId) {
        //  更新的业务逻辑：
        //  需要借助redis , 必须知道key ，使用的数据类型 Zset ZINCRBY
        String hotScoreKey = "hotScore";
        //  存储数据
        //  第一个 key ，第二个参数成员，第三个自增数
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotScoreKey, "skuId:" + skuId, 1);
        //  根据规则设置es
        if (hotScore%10==0){
            //  更改谁 根据skuId 找到的
            Optional<Goods> optional = this.goodsRepository.findById(skuId);
            Goods goods = optional.get();
            //  将最新的数据进行覆盖
            goods.setHotScore(hotScore.longValue());
            this.goodsRepository.save(goods);
        }

        return Result.ok();
    }
}
