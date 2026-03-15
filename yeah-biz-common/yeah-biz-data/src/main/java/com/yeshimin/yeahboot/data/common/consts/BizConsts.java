package com.yeshimin.yeahboot.data.common.consts;

public class BizConsts {

    // 登录短信验证码队列名称
    public static final String LOGIN_SMS_CODE_TOPIC = "LOGIN_SMS_CODE";

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
                    "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return -2 end;" +
                    "redis.call('decr', KEYS[1]);" +
                    "redis.call('sadd', KEYS[2], ARGV[1]);" +
                    "return stock - 1;";

    // 秒杀中的活动集合缓存key
    public static final String SECKILL_ACTIVITY_ING_KEY = "seckill:activity:ing";
    // 秒杀活动信息缓存key    seckill:activity:<activityId>
    public static final String SECKILL_ACTIVITY_KEY = "seckill:activity:%s";
    // 秒杀活动下的sku集合    seckill:activity:skus:<activityId>
    public static final String SECKILL_ACTIVITY_SKUS_KEY = "seckill:activity:skus:%s";

    // 秒杀库存缓存key    seckill:stock:sku:<skuId>
    public static final String SECKILL_STOCK_KEY = "seckill:stock:sku:%s";
    // 秒杀sku用户名额缓存key    seckill:quota:sku:<skuId>
    public static final String SECKILL_QUOTA_KEY = "seckill:quota:sku:%s";
    // 秒杀sku下单记录缓存key    seckill:order:sku:<skuId>
    public static final String SECKILL_ORDER_KEY = "seckill:order:sku:%s";
    // 秒杀sku用户的各个事件节点时间缓存key    seckill:event:sku:<skuId>:user:<userId>
    public static final String SECKILL_EVENT_KEY = "seckill:event:sku:%s:user:%s";
    // 秒杀业务处理结果缓存key    seckill:result:sku:<skuId>:user:<userId>
    public static final String SECKILL_RESULT_KEY = "seckill:result:sku:%s:user:%s";
    // 秒杀订单支付超时阻止缓存key    seckill:block:sku:<skuId>
    // 此key同时可以用来做“每个用户只能参与一次”的逻辑
    public static final String SECKILL_BLOCK_KEY = "seckill:block:sku:%s";

    // 秒杀相关
    // --------------------------------------------------------------------------------
}
