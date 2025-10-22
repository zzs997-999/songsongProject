package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.CACHE_NULL_TTL;
import static com.hmdp.utils.RedisConstants.LOCK_SHOP_KEY;

@Slf4j
@Component
public class CacheClient {
//
//    private final StringRedisTemplate stringRedisTemplate;
//
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
//
//    public CacheClient(StringRedisTemplate stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//    }
//
//    public void set(String key, Object value, Long time, TimeUnit unit) {
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
//    }
//
//    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
//        // 设置逻辑过期
//        RedisData redisData = new RedisData();
//        redisData.setData(value);
//        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
//        // 写入Redis
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
//    }
//
//    public <R,ID> R queryWithPassThrough(
//            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit){
//        String key = keyPrefix + id;
//        // 1.从redis查询商铺缓存
//        String json = stringRedisTemplate.opsForValue().get(key);
//        // 2.判断是否存在
//        if (StrUtil.isNotBlank(json)) {
//            // 3.存在，直接返回
//            return JSONUtil.toBean(json, type);
//        }
//        // 判断命中的是否是空值
//        if (json != null) {
//            // 返回一个错误信息
//            return null;
//        }
//
//        // 4.不存在，根据id查询数据库
//        R r = dbFallback.apply(id);
//        // 5.不存在，返回错误
//        if (r == null) {
//            // 将空值写入redis
//            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//            // 返回错误信息
//            return null;
//        }
//        // 6.存在，写入redis
//        this.set(key, r, time, unit);
//        return r;
//    }
//
//    public <R, ID> R queryWithLogicalExpire(
//            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
//        String key = keyPrefix + id;
//        // 1.从redis查询商铺缓存
//        String json = stringRedisTemplate.opsForValue().get(key);
//        // 2.判断是否存在
//        if (StrUtil.isBlank(json)) {
//            // 3.存在，直接返回
//            return null;
//        }
//        // 4.命中，需要先把json反序列化为对象
//        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
//        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
//        LocalDateTime expireTime = redisData.getExpireTime();
//        // 5.判断是否过期
//        if(expireTime.isAfter(LocalDateTime.now())) {
//            // 5.1.未过期，直接返回店铺信息
//            return r;
//        }
//        // 5.2.已过期，需要缓存重建
//        // 6.缓存重建
//        // 6.1.获取互斥锁
//        String lockKey = LOCK_SHOP_KEY + id;
//        boolean isLock = tryLock(lockKey);
//        // 6.2.判断是否获取锁成功
//        if (isLock){
//            // 6.3.成功，开启独立线程，实现缓存重建
//            CACHE_REBUILD_EXECUTOR.submit(() -> {
//                try {
//                    // 查询数据库
//                    R newR = dbFallback.apply(id);
//                    // 重建缓存
//                    this.setWithLogicalExpire(key, newR, time, unit);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }finally {
//                    // 释放锁
//                    unlock(lockKey);
//                }
//            });
//        }
//        // 6.4.返回过期的商铺信息
//        return r;
//    }
//
//    public <R, ID> R queryWithMutex(
//            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
//        String key = keyPrefix + id;
//        // 1.从redis查询商铺缓存
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//        // 2.判断是否存在
//        if (StrUtil.isNotBlank(shopJson)) {
//            // 3.存在，直接返回
//            return JSONUtil.toBean(shopJson, type);
//        }
//        // 判断命中的是否是空值
//        if (shopJson != null) {
//            // 返回一个错误信息
//            return null;
//        }
//
//        // 4.实现缓存重建
//        // 4.1.获取互斥锁
//        String lockKey = LOCK_SHOP_KEY + id;
//        R r = null;
//        try {
//            boolean isLock = tryLock(lockKey);
//            // 4.2.判断是否获取成功
//            if (!isLock) {
//                // 4.3.获取锁失败，休眠并重试
//                Thread.sleep(50);
//                return queryWithMutex(keyPrefix, id, type, dbFallback, time, unit);
//            }
//            // 4.4.获取锁成功，根据id查询数据库
//            r = dbFallback.apply(id);
//            // 5.不存在，返回错误
//            if (r == null) {
//                // 将空值写入redis
//                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//                // 返回错误信息
//                return null;
//            }
//            // 6.存在，写入redis
//            this.set(key, r, time, unit);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }finally {
//            // 7.释放锁
//            unlock(lockKey);
//        }
//        // 8.返回
//        return r;
//    }
//
//    private boolean tryLock(String key) {
//        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
//        return BooleanUtil.isTrue(flag);
//    }
//
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }
    //set：把数据假如redis，并设置有效期
    public void set(String key,Object value,Long timeout,TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(value),timeout,unit);
    }

    //把数据加入redis，设置逻辑有效时间
    public void setWithLogicalExpire(String key,Object value,Long timeout,TimeUnit unit){
        RedisData redisData=new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(timeout)));
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(value));
    }

    /*
    根据id查询数据，处理缓存穿透:数据直接反复打到数据库。解决方法：缓存空值
     */
    public <T,ID>T queryWithPassThrough(String keyPrefix,ID id,Class<T>type,
                                            Function<ID,T>dbFallback,Long timeout,TimeUnit unit){
        String key=keyPrefix+id;
        String RedisJSON=stringRedisTemplate.opsForValue().get(key);
        T result=null;
        if (StrUtil.isNotBlank(RedisJSON)) {
            result=JSONUtil.toBean(RedisJSON,type);
            return result;
        }
        //先看字符串是不是null,
        if(RedisJSON!=null){
            return null;
        }
        //去数据库里找
        result=dbFallback.apply(id);
        if(Objects.isNull(result)){
            //数据库不存在，缓存空对象，返回失败信息
            this.set(key,"",timeout,unit);
            return null;
        }
        set(key,result,timeout,unit);
        return result;
    }

    //处理缓存击穿
    public <T,ID>T handleCacheBreakdown(String keyPrefix,ID id,Class<T>type,
                                          Function<ID,T> dbFallback,Long timeout,TimeUnit unit){
        String key=keyPrefix+id;
        T result;
        String RedisJSON=stringRedisTemplate.opsForValue().get(key);
        //看逻辑过期时间
        if(JSONUtil.isNull(RedisJSON)){
            return null;
        }
        //反序列化为Redisdata
        RedisData redisData=JSONUtil.toBean(RedisJSON,RedisData.class);
        LocalDateTime expireTime=redisData.getExpireTime();
        LocalDateTime TimeNow=LocalDateTime.now();
        result=JSONUtil.toBean((JSONObject) redisData.getData(), type);
        //未过期，直接返回
        if(expireTime.isAfter(TimeNow)){
            return result;
        }
        //过期了，尝试拿锁(setNX简单分布式锁)
        String lockKey=LOCK_SHOP_KEY+id;
        boolean isLock=tryLock(lockKey);
        //拿锁成功
        if(isLock){
            //这里可以双检
            //开启一个子线程去重建缓存
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try{
                    T t1=dbFallback.apply(id);
                    //查询到的数据存到redis
                    setWithLogicalExpire(key,t1,timeout,unit);
                }
                finally {
                    unlock(lockKey);
                }
            });
        }
        //获取锁失败，再次查询缓存，判断缓存是否重建
        return result;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
}
