package spring.cloud.demo.study.cache;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class CacheServiceImpl implements CacheService {
    public static final String CACHE_ON = "1";
    public static final String CACHE_OFF = "0";
    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
    private JedisCluster jc;
    private Jedis jedis;
    private String cacheOn;
    private static final String NX = "NX";
    private static final String EX = "EX";


    public CacheServiceImpl() {
        super();
    }

    public CacheServiceImpl(JedisCluster jc, Jedis jedis, String cacheOn) {
        super();
        this.jc = jc;
        this.jedis = jedis;
        this.cacheOn = cacheOn;
    }

    public String getCacheOn() {
        return this.cacheOn;
    }

    public void setCacheOn(String cacheOn) {
        this.cacheOn = cacheOn;
    }

    public JedisCluster getJc() {
        return this.jc;
    }

    public void setJc(JedisCluster jc) {
        this.jc = jc;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean set(String key, Object value, boolean coverOnExist) {
        logger.debug("key :" + key + " | value:" + value + " | coverOnExist:" + coverOnExist);
        if (StringUtils.isEmpty(key)) {
            return false;
        } else {
            String result = "";
            if (coverOnExist) {
                if ("java.lang.String".equals(value.getClass().getName())) {
                    result = this.jc.set(key, value.toString());
                } else {
                    result = this.jc.set(key, JSON.toJSONString(value));
                }
            } else if ("java.lang.String".equals(value.getClass().getName())) {
                result = String.valueOf(this.jc.setnx(key, value.toString()));
            } else {
                result = String.valueOf(this.jc.setnx(key, JSON.toJSONString(value)));
            }

            if (StringUtils.isEmpty(result)) {
                return false;
            } else {
                if ("1".equals(result)) {
                    result = "OK";
                }

                return "OK".equals(result.toUpperCase());
            }
        }
    }

    public boolean set(String key, Object value) {
        return this.set(key, value, false);
    }

    public boolean replace(String key, Object value) {
        return this.set(key, value, true);
    }

    public boolean set(String key, Object value, int second) {
        if (StringUtils.isEmpty(key)) {
            return false;
        } else {
            logger.debug("opt:setwithtime | key :" + key + " | value:" + value + " | second:" + second);
            String result = "";
            if (this.jc.exists(key)) {
                if ("java.lang.String".equals(value.getClass().getName())) {
                    if (second <= 0) {
                        result = this.jc.set(key, value.toString());
                    } else {
                        result = this.jc.setex(key, second, value.toString());
                    }
                } else if (second <= 0) {
                    result = this.jc.set(key, JSON.toJSONString(value));
                } else {
                    result = this.jc.setex(key, second, JSON.toJSONString(value));
                }
            } else if ("java.lang.String".equals(value.getClass().getName())) {
                if (second <= 0) {
                    result = this.jc.set(key, value.toString());
                } else {
                    result = this.jc.set(key, value.toString(), "NX", "EX", new Long((long) second));
                }
            } else if (second <= 0) {
                result = this.jc.set(key, JSON.toJSONString(value));
            } else {
                result = this.jc.set(key, JSON.toJSONString(value), "NX", "EX", new Long((long) second));
            }

            if (StringUtils.isEmpty(result)) {
                return false;
            } else {
                return "OK".equals(result.toUpperCase());
            }
        }
    }

    public boolean expire(String key, int seconds) {
        return this.jc.expire(key, seconds) == 1L;
    }

    public void append(String key, String append) {
        this.jc.append(key, append);
    }

    public boolean delete(String key) {
        return this.jc.del(key) > 0L;
    }

    public String type(String key) {
        return this.jc.type(key);
    }

    public String get(String key) {
        try {
            if (!"1".equals(this.getCacheOn())) {
                return null;
            } else {
                return StringUtils.isEmpty(key) ? null : this.jc.get(key);
            }
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            return null;
        }
    }

    public <T> T getObject(String key, Class<T> objectType) {
        try {
            if (!"1".equals(this.getCacheOn())) {
                return null;
            } else if (StringUtils.isEmpty(key)) {
                return null;
            } else {
                String result = this.jc.get(key);
                return JSON.parseObject(result, objectType);
            }
        } catch (Exception var4) {
            logger.error(var4.getMessage(), var4);
            return null;
        }
    }

    public long ttl(String key) {
        return this.jc.ttl(key);
    }

    public <T> boolean setList(String key, List<T> list, int seconds) {
        if (list != null) {
            boolean ret = false;
            if (seconds > 0) {
                this.set(key, list, seconds);
            } else {
                this.replace(key, list);
            }

            logger.debug("setList key:" + key);
            return ret;
        } else {
            logger.error("缓存list出错，list为null");
            logger.debug("Cache List: [" + key + "]");
            return false;
        }
    }

    public <T> List<T> getList(String key, Class<T> objectType) {
        List list = null;

        try {
            if (!"1".equals(this.getCacheOn())) {
                return null;
            }

            if (!StringUtils.isEmpty(key)) {
                String result = this.get(key);
                System.out.println(result);
                list = JSON.parseArray(result, objectType);
            } else {
                logger.error("获取list出错，key为空");
            }
        } catch (Exception var5) {
            logger.error(var5.getMessage(), var5);
        }

        logger.info("Load List: [" + key + "]");
        return list;
    }

    public <T> boolean setMapValue(String tableName, String key, T value) {
        if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(key)) {
            long result = this.jc.hset(tableName, key, JSON.toJSONString(value));
            return result == 1L;
        } else {
            return false;
        }
    }

    public <T> boolean setMapValue(String tableName, String key, String value) {
        if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(key)) {
            long result = this.jc.hset(tableName, key, value);
            return result == 1L;
        } else {
            return false;
        }
    }

    public <T> boolean setMap(String tableName, Map<String, T> dataMap) {
        if (tableName != null && dataMap != null && !dataMap.isEmpty()) {
            Map<String, String> sMap = new HashMap();
            Iterator i$ = dataMap.keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                Object value = dataMap.get(key);

                try {
                    sMap.put(key, JSON.toJSONString(value));
                } catch (Exception var8) {
                    logger.error("class not cast ", var8);
                }
            }

            String result = this.jc.hmset(tableName, sMap);
            if (StringUtils.isEmpty(result)) {
                return false;
            } else {
                return "OK".equals(result.toUpperCase());
            }
        } else {
            return false;
        }
    }

    public <T> List<T> getValueListFromMap(String tableName, Class<T> objectType, String... keys) {
        if (!"1".equals(this.getCacheOn())) {
            return null;
        } else if (tableName == null) {
            return null;
        } else {
            List<String> list = this.jc.hmget(tableName, keys);
            List<T> resultList = new ArrayList();
            Iterator i$ = list.iterator();

            while (i$.hasNext()) {
                String value = (String) i$.next();
                resultList.add(JSON.parseObject(value, objectType));
            }

            return resultList;
        }
    }

    public <T> T getObjectFromMap(String tableName, String key, Class<T> objectType) {
        if (!"1".equals(this.getCacheOn())) {
            return null;
        } else if (tableName == null) {
            return null;
        } else {
            String result = this.jc.hget(tableName, key);
            return JSON.parseObject(result, objectType);
        }
    }

    public String getStringFromMap(String tableName, String key) {
        if (!"1".equals(this.getCacheOn())) {
            return null;
        } else if (tableName == null) {
            return null;
        } else {
            String result = this.jc.hget(tableName, key);
            return result;
        }
    }

    public Map<String, String> getMap(String tableName) {
        if (!"1".equals(this.getCacheOn())) {
            return null;
        } else {
            return StringUtils.isEmpty(tableName) ? null : this.jc.hgetAll(tableName);
        }
    }

    public <T> Map<String, T> getMap(String tableName, Class<T> mapValueType) {
        if (!"1".equals(this.getCacheOn())) {
            return null;
        } else if (tableName == null) {
            return null;
        } else {
            Map<String, String> tmp = this.jc.hgetAll(tableName);
            Map<String, T> resultMap = new HashMap();
            Iterator i$ = tmp.keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                String value = (String) tmp.get(key);
                resultMap.put(key, JSON.parseObject(value, mapValueType));
            }

            return resultMap;
        }
    }

    public long deleteFromMap(String tableName, String... keys) {
        return !StringUtils.isEmpty(tableName) && keys != null && keys.length >= 1
                ? this.jc.hdel(tableName, keys)
                : -1L;
    }

    public Long incr(String key) {
        return this.jc.incrBy(key, 1L);
    }

    public Long decr(String key) {
        return this.jc.decrBy(key, 1L);
    }

    public Long incr(String key, Long value) {
        return value != null ? this.jc.incrBy(key, value) : this.jc.incrBy(key, 0L);
    }

    public Long decr(String key, Long value) {
        return value != null ? this.jc.decrBy(key, value) : this.jc.decrBy(key, 0L);
    }

    public boolean setByBytes(String key, Object value, int second) {
        boolean flag = false;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            if (StringUtils.isEmpty(key)) {
                flag = false;
            } else {
                logger.debug("opt:setex | key :" + key + " | value:" + value + " | second:" + second);
                oos = new ObjectOutputStream(bos);
                oos.writeObject(value);
                String result = "";
                result = this.jc.setex(key.getBytes(), second, bos.toByteArray());
                if (StringUtils.isEmpty(result)) {
                    flag = false;
                } else if ("OK".equals(result.toUpperCase())) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        } catch (Exception var16) {
            logger.error(var16.getMessage());
            flag = false;
        } finally {
            try {
                if (null != oos) {
                    oos.close();
                }
            } catch (Exception var15) {
                logger.error(var15.getMessage());
                flag = false;
            }

        }

        return flag;
    }

    public Object getObjectByte(String key) {
        try {
            if (!"1".equals(this.getCacheOn())) {
                return null;
            } else if (StringUtils.isEmpty(key)) {
                return null;
            } else {
                byte[] obj_byte = this.jc.get(key.getBytes());
                if (null != obj_byte && obj_byte.length > 0) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(obj_byte);
                    ObjectInputStream ois = null;
                    Object obj = null;

                    try {
                        ois = new ObjectInputStream(bais);
                        obj = ois.readObject();
                    } catch (Exception var16) {
                        logger.error(var16.getMessage());
                    } finally {
                        try {
                            if (null != ois) {
                                ois.close();
                            }
                        } catch (Exception var15) {
                            logger.error(var15.getMessage());
                        }

                    }

                    return obj;
                } else {
                    return null;
                }
            }
        } catch (Exception var18) {
            logger.error(var18.getMessage(), var18);
            return null;
        }
    }

    public boolean deleteByte(String key) {
        boolean flag = false;
        if (StringUtils.isEmpty(key)) {
            flag = false;
        } else {
            logger.debug("deleteByte by key" + key);
            flag = this.jc.del(key.getBytes()) > 0L;
        }

        return flag;
    }

    public boolean exist(String key) {
        boolean flag = false;
        if (StringUtils.isEmpty(key)) {
            flag = false;
        } else {
            flag = this.jc.exists(key);
        }

        return flag;
    }

    public Long lpush(String key, Object object) {
        return this.jc.lpush(key, new String[]{JSON.toJSONString(object)});
    }

    public String ltrim(String key, long start, long end) {
        return this.jc.ltrim(key, start, end);
    }

    public <T> List<T> getRedisList(String key, long start, long end, Class<T> objectClass) {
        List<T> rtnList = new ArrayList();
        List<String> list = this.jc.lrange(key, start, end);
        Iterator i$ = list.iterator();

        while (i$.hasNext()) {
            String value = (String) i$.next();
            T t = JSON.parseObject(value, objectClass);
            rtnList.add(t);
        }

        return rtnList;
    }

    public Set<String> getRedisKeys(String key) {
        return key != null && !key.equals("") ? this.jedis.keys("*" + key + "*") : this.jedis.keys("*");
    }

    public boolean deleteObjectByKeys(List<String> keys) {
        int i = 0;

        for (Iterator i$ = keys.iterator(); i$.hasNext(); ++i) {
            String key = (String) i$.next();
            if (this.jc.del(key) != 1L) {
                return false;
            }
        }

        return keys.size() == i;
    }
}