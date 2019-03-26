package spring.cloud.demo.study.cache;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheService {
	String getCacheOn();

	boolean set(String var1, Object var2, boolean var3);

	boolean set(String var1, Object var2);

	boolean replace(String var1, Object var2);

	boolean set(String var1, Object var2, int var3);

	boolean expire(String var1, int var2);

	void append(String var1, String var2);

	boolean delete(String var1);

	String type(String var1);

	String get(String var1);

	<T> T getObject(String var1, Class<T> var2);

	long ttl(String var1);

	<T> boolean setList(String var1, List<T> var2, int var3);

	<T> List<T> getList(String var1, Class<T> var2);

	<T> boolean setMapValue(String var1, String var2, T var3);

	<T> boolean setMapValue(String var1, String var2, String var3);

	<T> boolean setMap(String var1, Map<String, T> var2);

	<T> List<T> getValueListFromMap(String var1, Class<T> var2, String... var3);

	<T> T getObjectFromMap(String var1, String var2, Class<T> var3);

	String getStringFromMap(String var1, String var2);

	Map<String, String> getMap(String var1);

	<T> Map<String, T> getMap(String var1, Class<T> var2);

	long deleteFromMap(String var1, String... var2);

	Long incr(String var1);

	Long decr(String var1);

	Long incr(String var1, Long var2);

	Long decr(String var1, Long var2);

	boolean setByBytes(String var1, Object var2, int var3);

	Object getObjectByte(String var1);

	boolean deleteByte(String var1);

	boolean exist(String var1);

	Long lpush(String var1, Object var2);

	String ltrim(String var1, long var2, long var4);

	<T> List<T> getRedisList(String var1, long var2, long var4, Class<T> var6);

	Set<String> getRedisKeys(String var1);

	boolean deleteObjectByKeys(List<String> var1);
}
