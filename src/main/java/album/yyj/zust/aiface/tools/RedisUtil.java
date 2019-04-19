package album.yyj.zust.aiface.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: 杨玉杰
 * @Date: 2019/4/17 09:46
 * @Description:
 * Redis工具类
 */
@Component
public class RedisUtil {

    public static final String PREFIX_POS_FACE_KEY = "photo_face_pos_";
    public static final Integer POS_STATS_UNDO = 0;
    public static final Integer POS_STATS_SUCCESS = 1;
    public static final Integer POS_STATS_POSING = 2;
    public static final Integer POS_STATS_FAIL = -1;
    public static final Long KEY_EXPIRE_TIME = (long)10*60; //key的过期时间为10分钟

    //图片检索相关的定义
    public static final String PREFIX_FACE_MACHE_KEY = "face_mache_";
    public static final Integer MACHE_STATS_UNDO = 0;
    public static final Integer MACHE_STATS_SUCCESS = 1;
    public static final Integer MACHE_STATS_MACHING = 2;
    public static final Integer MACHE_STATS_FAIL = -1;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 指定缓存失效时间
     * time 时间（秒）
     */
    public boolean expire(String key,long time){
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * 返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
        /**
         * 普通缓存放入
         * @param key 键
         * @param value 值
         * @return true成功 false失败
         */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
        /**
         * 普通缓存放入并设置时间
         * @param key 键
         * @param value 值
         * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
         * @return true成功 false 失败
         */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
