package top.b0x0.juc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.b0x0.juc.service.SecKillService;
import top.b0x0.juc.vo.SeckillSkuRedisTo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("SecKillService")
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //K: SESSION_CACHE_PREFIX + startTime + "_" + endTime
    //V: sessionId+"-"+skuId的List
    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    //K: 固定值SECKILL_CHARE_PREFIX
    //V: hash，k为sessionId+"-"+skuId，v为对应的商品信息SeckillSkuRedisTo
    private final String SECKILL_CHARE_PREFIX = "seckill:skus";

    //K: SKU_STOCK_SEMAPHORE+商品随机码
    //V: 秒杀的库存件数
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public SeckillSkuRedisTo getSeckillSkuInfo(Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        Set<String> keys = ops.keys();
        for (String key : keys) {
            if (Pattern.matches("\\d-" + skuId,key)) {
                String v = ops.get(key);
                SeckillSkuRedisTo redisTo = JSON.parseObject(v, SeckillSkuRedisTo.class);
                //当前商品参与秒杀活动
                if (redisTo!=null){
                    long current = System.currentTimeMillis();
                    //当前活动在有效期，暴露商品随机码返回
                    if (redisTo.getStartTime() < current && redisTo.getEndTime() > current) {
                        return redisTo;
                    }
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }
        }
        return null;
    }

}
