package com.elim.learn.redis.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

/**
 * 基础测试
 * @author elim
 *
 */
public class BasicTest {
	
	private static final RedisClient CLIENT = RedisClient.create("redis://localhost:6379");
	private RedisConnection<String, String> connect = null;
	
	@Before
	public void before() {
		connect = CLIENT.connect();
	}
	

	/**
	 * 简单的测试建立Redis链接和获取对应的String类型的Key对应的值
	 */
	@Test
	public void test1() {
		String key = "abc";
		connect.set(key, "Hello World");
		String value = connect.get(key);
		System.out.println(value);
	}
	
	/**
	 * 简单的测试哈希类型的Key的操作
	 */
	@Test
	public void test2() {
		Map<String, String> map = new HashMap<>();
		map.put("id", "1");
		map.put("username", "zhangsan");
		map.put("password", "123");
		map.put("realName", "张三");
		String result = connect.hmset("user:1", map);
		System.out.println(result);
		
		Map<String, String> user1 = connect.hgetall("user:1");
		System.out.println(user1);
	}
	
	/**
	 * 简单的测试列表类型的Key的操作
	 */
	@Test
	public void test3() {
		String key = "list";
		connect.lpush(key, "A", "B", "C", "D", "E", "F", "G");
		Long listSize = connect.llen(key);
		String ele3 = connect.lindex(key, 3);//获取索引为3的元素，从0开始
		assert listSize == 7;
		assert "D".equals(ele3);
		//获取索引为0到1的元素，都是包含，这里将返回G和F。因为新元素默认是添加到列头，索引为0，如果需要默认添加到列尾，则可以使用rpush
		List<String> list = connect.lrange(key, 0, 1);
		System.out.println(list);
		//默认添加到列尾
		connect.rpush(key, "H", "I", "J", "K", "L", "M", "N");
		list = connect.lrange(key, 0, 100);
		System.out.println(list);
	}
	
	/**
	 * 简单的测试无序集合的操作
	 */
	@Test
	public void test4() {
		String key = "set";
		connect.sadd(key, "A", "B", "C", "D", "E", "F", "G");
		Set<String> members = connect.smembers(key);
		System.out.println(members);
	}
	
	/**
	 * 简单的测试有序集合的操作
	 */
	@Test
	public void test5() {
		String key = "sortedSet";
		connect.zadd(key, 0d, "A", 1d, "B", 2d, "C", 3d, "D");
		connect.zadd(key, 4, "E");
		//通过字典顺序来取，[表示包括，(表示不包括
		List<String> members = connect.zrangebylex(key, "[B", "(D");//B，C
		System.out.println(members);
		//通过分数范围来获取
		members = connect.zrangebyscore(key, 0, 10.5);
		System.out.println(members);
	}
	
	/**
	 * 对HyperLogLog这种类型的Key的简单测试。HyperLogLog主要用来统计其中的不重复的元素的个数，其不进行元素的存储，只做统计
	 */
	@Test
	public void test6() {
		String key = "HyperLogLog";
		connect.pfadd(key, "A", "B", "C", "D", "F", "D", "F", "G", "E");
		//统计指定key中不重复元素的数量，这里应为7
		Long pfcount = connect.pfcount(key);
		System.out.println(pfcount);//7
		
		String key2 = "HyperLoglog2";
		connect.pfadd(key2, "1", "2", "A");
		//统计多个Key中不重复元素的数量，这里应为9
		Long pfcount2 = connect.pfcount(key, key2);
		System.out.println(pfcount2);//9
	}
	
	/**
	 * 简单的测试对key的操作
	 */
	@Test
	public void delAll() {
		//获取所有的Key
		List<String> keys = connect.keys("*");
		if (!keys.isEmpty()) {
			connect.del(keys.toArray(new String[keys.size()]));
		}
	}
}
