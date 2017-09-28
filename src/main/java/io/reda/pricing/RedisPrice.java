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
    private static DB db;
    static{
        System.out.println("Set up Redis");
        //Connecting to Redis server on localhost
        //jedis = new Jedis("localhost");
        try{
            MongoClient mongo = new MongoClient( "172.17.0.3" , 27017 );
            db = mongo.getDB("officedepot");
            DBCollection prices = db.getCollection("prices");
            ArrayList<DBObject> list = new ArrayList<>();
            for (int i = 0; i < 50; i++) {

                BasicDBObject document = new BasicDBObject();
                document.put("productid", String.valueOf(i));
                document.put("price", (2.67*i));
                document.put("createdDate", new Date());
                list.add(document);
            }
            System.out.println(prices.insert(list));
        }catch(Exception e){

        }


            System.out.println("Connection to server sucessfully");

    }
    @Override
    public Product get(String productId) throws NotFoundException, InternalException{

        try {

            DBCollection prices = db.getCollection("prices");
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("productid", productId);

            DBObject object = prices.findOne(searchQuery);
            if(object == null) throw new NotFoundException();
            System.out.println(object);
            Product product = new Product();
            product.price = object.get("price").toString();
            product.productId = productId;
            return product;



        }catch(Exception e){
            e.printStackTrace();
            throw new InternalException();
        }


    }



    @Override
    public void set(String productId, double price) {

        jedis.set(productId,""+price);
    }

    @Override
    public SearchResponse prices(SearchRequest request) {

        DBCollection prices = db.getCollection("prices");


        BasicDBList or = new BasicDBList();
        for(String productId : request.producIds){
            DBObject or_part1 = new BasicDBObject("productid", productId );
            or.add(or_part1);
        }

        DBObject query = new BasicDBObject("$or", or);
        prices.find(query);
        return null;
    }


}
