package top.b0x0.juc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.b0x0.juc.dao.ProductAttrValueDao;
import top.b0x0.juc.entry.ProductAttrValueEntity;
import top.b0x0.juc.service.ProductAttrValueService;
import top.b0x0.juc.util.PageUtils;
import top.b0x0.juc.util.Query;
import top.b0x0.juc.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId) {

        return baseMapper.getProductGroupAttrsBySpuId(spuId, catalogId);
    }


}