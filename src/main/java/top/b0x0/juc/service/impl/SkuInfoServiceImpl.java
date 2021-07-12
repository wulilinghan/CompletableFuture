package top.b0x0.juc.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.b0x0.juc.dao.SkuInfoDao;
import top.b0x0.juc.entry.SkuImagesEntity;
import top.b0x0.juc.entry.SkuInfoEntity;
import top.b0x0.juc.entry.SpuInfoDescEntity;
import top.b0x0.juc.service.*;
import top.b0x0.juc.util.PageUtils;
import top.b0x0.juc.util.R;
import top.b0x0.juc.vo.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author TANG
 * @since 2021/07/12
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SecKillService secKillService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return null;
    }


    @Override
    public SkuItemVo item(Long skuId) {
        // TODO CompletableFuture 异步编排
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息的获取  pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(skuImagesEntities);
        }, executor);


        //3、获取spu的销售属性组合-> 依赖1 获取spuId
        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((info) -> {
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.listSaleAttrs(info.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);


        //4、获取spu的介绍-> 依赖1 获取spuId
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((info) -> {
            SpuInfoDescEntity byId = spuInfoDescService.getById(info.getSpuId());
            skuItemVo.setDesc(byId);
        }, executor);


        //5、获取spu的规格参数信息-> 依赖1 获取spuId catalogId
        CompletableFuture<Void> attrFuture = infoFuture.thenAcceptAsync((info) -> {
            List<SpuItemAttrGroupVo> spuItemAttrGroupVos = productAttrValueService.getProductGroupAttrsBySpuId(info.getSpuId(), info.getCatalogId());
            skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        }, executor);

        //6、秒杀商品的优惠信息
        CompletableFuture<Void> seckFuture = CompletableFuture.runAsync(() -> {
            SeckillSkuRedisTo seckillSkuInfo = secKillService.getSeckillSkuInfo(skuId);
            R r = R.ok().setData(seckillSkuInfo);
            if (r.getCode() == 0) {
                SeckillSkuVo seckillSkuVo = r.getData(new TypeReference<SeckillSkuVo>() {
                });
                long current = System.currentTimeMillis();
                //如果返回结果不为空且活动未过期，设置秒杀信息
                if (seckillSkuVo != null && current < seckillSkuVo.getEndTime()) {
                    skuItemVo.setSeckillSkuVo(seckillSkuVo);
                }
            }
        }, executor);

        //等待所有任务执行完成
        try {
            CompletableFuture.allOf(imageFuture, saleFuture, descFuture, attrFuture, seckFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return skuItemVo;
    }
}
