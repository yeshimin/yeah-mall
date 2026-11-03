package com.yeshimin.yeahboot.data.common.consts;

public class BizConsts {

    // 登录短信验证码队列名称
    public static final String LOGIN_SMS_CODE_TOPIC = "LOGIN_SMS_CODE";
    // 商品浏览足迹队列名称
    public static final String PRODUCT_BROWSE_TOPIC = "PRODUCT_BROWSE";

    // --------------------------------------------------------------------------------
    // 秒杀相关

    // 秒杀下单队列名称
    public static final String SECKILL_ORDER_TOPIC = "SECKILL_ORDER";
    // 秒杀订单取消队列名称
    public static final String SECKILL_ORDER_CANCEL_TOPIC = "SECKILL_ORDER_CANCEL";

    // 秒杀扣减库存的lua脚本
    public static final String SECKILL_STOCK_SCRIPT =
            "local stock = tonumber(redis.call('get', KEYS[1]));" +
                    "if not stock or stock <= 0 then return -1 end;" +
                    "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 " +
                    "or redis.call('sismember', KEYS[3], ARGV[1]) == 1 then return -2 end;" +
                    "redis.call('decr', KEYS[1]);" +
                    "redis.call('sadd', KEYS[2], ARGV[1]);" +
                    "redis.call('sadd', KEYS[3], ARGV[1]);" +
                    "return stock - 1;";

    // 秒杀中的活动集合缓存key
    public static final String SECKILL_ACTIVITY_ING_KEY = "seckill:activity:ing";
    // 秒杀活动信息缓存key    seckill:activity:<activityId>
    public static final String SECKILL_ACTIVITY_KEY = "seckill:activity:%s";
    // 秒杀活动下的sku集合    seckill:activity:skus:<activityId>
    public static final String SECKILL_ACTIVITY_SKUS_KEY = "seckill:activity:skus:%s";
    // 秒杀SKU活动与场次上下文    seckill:sku:context:<skuId>
    public static final String SECKILL_SKU_CONTEXT_KEY = "seckill:sku:context:%s";

    // 秒杀库存缓存key    seckill:stock:sku:<skuId>
    public static final String SECKILL_STOCK_KEY = "seckill:stock:sku:%s";
    // 秒杀sku用户名额缓存key    seckill:quota:sku:<skuId>
    public static final String SECKILL_QUOTA_KEY = "seckill:quota:sku:%s";
    // 秒杀商品用户名额缓存key    seckill:quota:spu:<seckillSpuId>
    public static final String SECKILL_SPU_QUOTA_KEY = "seckill:quota:spu:%s";
    // 秒杀sku抢购尝试下单记录缓存key，集合成员为attemptId    seckill:order:sku:<skuId>
    public static final String SECKILL_ORDER_KEY = "seckill:order:sku:%s";
    // 秒杀sku用户的各个事件节点时间缓存key    seckill:event:sku:<skuId>:user:<userId>
    public static final String SECKILL_EVENT_KEY = "seckill:event:sku:%s:user:%s";
    // 秒杀业务处理结果缓存key    seckill:result:sku:<skuId>:user:<userId>
    public static final String SECKILL_RESULT_KEY = "seckill:result:sku:%s:user:%s";
    // 秒杀sku失效抢购尝试缓存key，集合成员为attemptId    seckill:block:sku:<skuId>
    public static final String SECKILL_BLOCK_KEY = "seckill:block:sku:%s";

    // 秒杀相关
    // --------------------------------------------------------------------------------
}
