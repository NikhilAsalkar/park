package db;

import redis.clients.jedis.Jedis;

public class RedisAdapter {
    public Jedis getConnection()
    {
        Jedis jedis = new Jedis("localhost");
            return jedis;
    }



}
