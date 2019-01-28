package util;
import redis.clients.jedis.*;
import java.util.*;

public class RedisAccess {
    Jedis jedis;
    Set hash;
    public Set getkey(String key,Object connection){
        jedis = (Jedis) connection;
        hash = jedis.keys(key);
        System.out.println(hash);
        return hash;
    }

    public void addToSortedList(String id,Object connection)
    {
        jedis = (Jedis)connection;
        jedis.zadd("emptyslots", Double.parseDouble(id), id);

    }
    public Set getZRange(String dataset, int begining,int end,Object collection)
    {
        jedis = (Jedis)collection;
        hash = jedis.zrange(dataset,begining,end);
        return hash;
    }
    public void addHashSet(String prefix,String id,Map data,Object connection)
    {
        jedis = (Jedis)connection;
        jedis.hmset(prefix + id, data);

    }
    public void deleteHashField(String dataset,String id,Object connection)
    {
        jedis = (Jedis)connection;
        jedis.zrem(dataset,id);
    }


    public Map<String, String> getValue(String key, Object connection)
    {
        jedis = (Jedis)connection;
        Map<String, String> data = jedis.hgetAll(key);
        return data;
    }

    public String getValue(String key,String finder,Object connection)
    {
        jedis = (Jedis)connection;
        String data = jedis.hget(key,finder);
        System.out.println(data);
        return data;
    }
    public void deleteKey(String key,Object connection)
    {
        jedis = (Jedis)connection;
        jedis.del(key);
    }
}

