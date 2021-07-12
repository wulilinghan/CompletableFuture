package top.b0x0.juc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.b0x0.juc.entry.SkuInfoEntity;
import top.b0x0.juc.util.PageUtils;
import top.b0x0.juc.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author Ethan
 * @email hongshengmo@163.com
 * @date 2020-05-27 15:38:36
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo item(Long skuId);
}

