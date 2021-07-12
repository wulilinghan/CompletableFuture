package top.b0x0.juc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.b0x0.juc.entry.SkuImagesEntity;
import top.b0x0.juc.util.PageUtils;

import java.util.Map;

/**
 * sku图片
 *
 * @author Ethan
 * @email hongshengmo@163.com
 * @date 2020-05-27 15:38:37
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

