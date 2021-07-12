package top.b0x0.juc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.b0x0.juc.entry.SkuSaleAttrValueEntity;
import top.b0x0.juc.util.PageUtils;
import top.b0x0.juc.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author Ethan
 * @email hongshengmo@163.com
 * @date 2020-05-27 15:38:36
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> listSaleAttrs(Long spuId);

    List<String> getSkuSaleAttrValuesAsString(Long skuId);
}

