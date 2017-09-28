package io.reda.pricing;

import com.mongodb.*;
import io.reda.domain.Product;
import io.reda.exceptions.InternalException;
import io.reda.exceptions.NotFoundException;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class RedisPrice implements Price {

    private static Jedis jedis;


    static{
        //Connecting to Redis server on localhost
        jedis = new Jedis(System.getenv("REDIS_HOST"));
    }
    @Override
    public Product get(String productId) {
        return null;
    }

    @Override
    public void set(String productId, double price) {

    }

    @Override
    public SearchResponse prices(SearchRequest request) {
        return null;
    }
}
