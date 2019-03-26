package spring.cloud.demo.study.cache;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class CacheServiceFactory {
    //初始化信息
    private static CacheServiceFactory instance;

    private JedisCluster jc;
    private Jedis jedis;
    private String cacheOn;
    private Logger  LOG = LoggerFactory.getLogger(getClass());

    public static synchronized CacheServiceFactory getInstance() {
        if (instance == null) {
            instance = getInstance((Environment) null);
        }

        return instance;
    }

    public static CacheServiceFactory getInstance(Environment config) {
        return new CacheServiceFactory(config);
    }

    private CacheServiceFactory(Environment config) {
        this.init(config);
    }

    private void init(Environment config) {
        String servers = config.getProperty("redis.servers");
        if (!StringUtils.isEmpty(servers)) {
            String[] serverArr = servers.split(";");
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();

            String ip;
            String port;
            for (int i = 0; i < serverArr.length; ++i) {
                if (!StringUtils.isEmpty(serverArr[i]) && serverArr[i].indexOf(":") != -1) {
                    String[] ipAndPort = serverArr[i].split(":");
                    if (ipAndPort != null && ipAndPort.length > 1) {
                        ip = ipAndPort[0];
                        port = ipAndPort[1];
                        jedisClusterNodes.add(new HostAndPort(ip, Integer.parseInt(port)));
                        if (i == 0) {
                            this.jedis = new Jedis(ip, Integer.parseInt(port));
                        }
                    }
                }
            }

            String maxWaitMillis = config.getProperty("redis.maxWaitMillis");
            String maxTotal = config.getProperty("redis.maxTotal");
            ip = config.getProperty("redis.minIdle");
            port = config.getProperty("redis.maxIdle");
            String testOnBorrow = config.getProperty("redis.testOnBorrow");
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxWaitMillis(Long.parseLong(maxWaitMillis));
            poolConfig.setMaxTotal(Integer.parseInt(maxTotal));
            poolConfig.setMaxIdle(Integer.parseInt(port));
            poolConfig.setMinIdle(Integer.parseInt(ip));
            if ("true".equals(testOnBorrow)) {
                poolConfig.setTestOnBorrow(true);
            } else {
                poolConfig.setTestOnBorrow(false);
            }

            String connectionTimeout = config.getProperty("redis.connectionTimeout");
            String soTimeout = config.getProperty("redis.cluster.soTimeout");
            String maxRedirections = config.getProperty("redis.cluster.maxRedirections");
            this.cacheOn = config.getProperty("redis.cache.on");
            this.jc = new JedisCluster(jedisClusterNodes, Integer.parseInt(connectionTimeout),
                    Integer.parseInt(soTimeout), Integer.parseInt(maxRedirections), poolConfig);
        }
    }

    public CacheService createCacheService() {
        try {
            CacheServiceImpl cacheService = new CacheServiceImpl();
            cacheService.setJc(this.jc);
            cacheService.setCacheOn(this.cacheOn);
            cacheService.setJedis(this.jedis);
            return cacheService;
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }
}