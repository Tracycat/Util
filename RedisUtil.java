package Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 引入依赖
 * jedis-2.2.0.jar
 * commons-pool.jar
 *
 */
public class RedisUtil {
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024*1024;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static long MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;   
    private final static int time = 60 * 60 * 24 * 10;      //默认过期时间
    
    /**
     * 初始化Redis连接池
     */
    static {
        try {
//        	String addr = PropertiesService.getInstance().getValue("redisAddress");
//       	int port = Integer.parseInt(PropertiesService.getInstance().getValue("redisPort"));
//       	String auth = PropertiesService.getInstance().getValue("redisAuth");
//        	int dataSource = Integer.parseInt(PropertiesService.getInstance().getValue("redisDataSource"));
//          System.out.println("addr:"+addr+",port:"+port+",auth:"+auth+",dataSource:"+dataSource);

			String addr="";
			Integer port=null;
        	JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWait(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
//            jedisPool = new JedisPool(config, "127.0.0.1", port, TIMEOUT);
//            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
            //jedisPool = new JedisPool(config, addr, port, TIMEOUT,auth,dataSource);     //98
            
            System.out.println("addr:"+addr+",port:"+port+"");
            jedisPool = new JedisPool(config, addr, port);
        } catch (Exception e) {
            e.printStackTrace();          
        }
    }
    
    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
    
    /**
	 * 判断key值是否存在
	 * @param key 键
	 * @return 存在true
	 */
	public static boolean keyExists(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			boolean code = jedis.exists(key);
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 通过key获取相应的值
	 * @param key
	 * @return
	 */
	public static String getValueByKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			String value = jedis.get(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 将key,value存入Redis
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setObjectToRedis(String key, String value){
		try{			
			return setObjectToRedis(key,value,time);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 将key,value存入Redis
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setObjectToRedis(String key, Object value){
		try{			
			return setObjectToRedis(key,value,time);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 将key,value存入Redis
	 * @param key
	 * @param value
	 * @param seconds 存放过期时间
	 * @return
	 */
	public static boolean setObjectToRedis(String key, String value, int seconds){
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			String code = jedis.set(key, value);
			Long c = jedis.expire(key, seconds);
			boolean flag = (StringUtils.isNotBlank(code) && "OK".equals(code.trim()))?true:false;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	public static boolean setObjectToRedis(String key,String field, String value){
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			long c = jedis.hset(key, field, value);
			///boolean flag = true;//(StringUtils.isNotBlank(code) && "OK".equals(code.trim()))?true:false;
			//return flag;
			if(c==1){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	public static String getStringFromRedis(String key,String field){
		Jedis jedis = null;
		try {
			jedis = getJedis();		
			//jedis.lpush(key, field);
			String value = jedis.hget(key, field);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 将key,value存入Redis
	 * @param key
	 * @param obj
	 * @param seconds
	 * @return
	 */
	public static boolean setObjectToRedis(String key, Object obj, int seconds){
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			byte[] value = getByte(obj);
			String code = jedis.set(key.getBytes(), value);
			Long c = jedis.expire(key, seconds);
			boolean flag = (StringUtils.isNotBlank(code) && "OK".equals(code.trim())) ? true : false;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 按键获得当前的值
	 * @param key 键
	 * @return String 值
	 */
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			String code = jedis.get(key);
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 按键获得当前的值
	 * @param key 键
	 * @return Long 值
	 */
	public Long getLong(String key){		
		String code = this.get(key);
		return StringUtils.isNotBlank(code) ? Long.parseLong(code) : null;
	}

	/**
	 * 取缓存数据
	 * 
	 * @param key 键
	 * @return 取得缓存数据值 或 null
	 */
	public String getStringFromRedis(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			String code = jedis.get(key);
			return code;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	public static Object getObjectFromRedis(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();			
			byte[] code = jedis.get(key.getBytes());
			if(code==null) return null;
			Object obj = getObject(code);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 取出key，可使用   前缀+*，或者*
	 * 其中*包含全部（类似正则表达式） 
	 * @param containKey
	 * @return
	 */
	public static Set getKeys(String containKey){
		Jedis jedis = null;
		try {			
			jedis = getJedis();
			Set set = jedis.keys(containKey);
			return set;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 删除当前选择数据库中的所有key
	 */
	public static void flushdb(){
		Jedis jedis = null;
		try {			
			jedis = getJedis();
			getJedis().flushDB();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	/**
	 * 删除所有数据库中的数据库
	 */
	public static void flushall(){
		Jedis jedis = null;
		try {			
			jedis = getJedis();
			jedis.flushAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
		
	}
	
	/**
	 * 删除key值
	 * @param key
	 */
	public static void delKey(String key){
		Jedis jedis = null;
		try {			
			jedis = getJedis();
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
	}
	
	public static long getKeyTime(String key){
		Jedis jedis = null;
		try {
			//ProjectException.printTextToInfoLog("---删除删除key值:"+key);
			jedis = getJedis();
			long time = jedis.ttl(key);
			return time;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			returnResource(jedis);
		}
		return 0;
	}
	
	public static byte[] getByte(Object obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream( bos);
            oos.writeObject(obj);
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] bs = bos.toByteArray();
        
        return bs;
    }
    
    public static Object getObject(byte[] bi){
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(bi,0, bi.length));
            obj = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return obj;
    }
}

