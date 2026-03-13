package com.yeshimin.yeahboot.data.common.consts;

public class BizConsts {

    // 登录短信验证码队列名称
    public static final String LOGIN_SMS_CODE_TOPIC = "LOGIN_SMS_CODE";

    // 秒杀下单队列名称
    public static final String SECKILL_ORDER_TOPIC = "SECKILL_ORDER";

    // 秒杀扣减库存的lua脚本
    public static final String SECKILL_STOCK_SCRIPT =
            "local stock = tonumber(redis.call('get', KEYS[1]));" +
                    "if not stock or stock <= 0 then return -1 end;" +
                    "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return -2 end;" +
                    "redis.call('decr', KEYS[1]);" +
                    "redis.call('sadd', KEYS[2], ARGV[1]);" +
                    "return stock - 1;";

    // 秒杀库存缓存key    seckill:stock:sku:<skuId>
    public static final String SECKILL_STOCK_KEY = "seckill:stock:sku:%s";
    // 秒杀sku用户购买记录缓存key    seckill:user:sku:<skuId>
    public static final String SECKILL_USER_KEY = "seckill:user:sku:%s";
    // 秒杀业务处理结果缓存key    seckill:result:sku:<skuId>:user:<userId>
    public static final String SECKILL_RESULT_KEY = "seckill:result:sku:%s:user:%s";
}
