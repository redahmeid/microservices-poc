package io.reda.pricing;

import com.mongodb.*;
import io.reda.domain.Product;
import io.reda.exceptions.InternalException;
import io.reda.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Date;

public class MongoPrice implements Price{
    private static DB db;
    static{
        System.out.println("Set up Mongo");
        //Connecting to Redis server on localhost
        //jedis = new Jedis("localhost");
        try{
            // host = "172.17.0.3"
            // port = 27017
            MongoClient mongo = new MongoClient( System.getenv("MONGO_HOST") , new Integer(System.getenv("MONGO_PORT")) );
            db = mongo.getDB("officedepot");
            DBCollection prices = db.getCollection("prices");
            ArrayList<DBObject> list = new ArrayList<>();
            for (int i = 0; i < new Integer(System.getenv("STUB_NUMBER")); i++) {

                BasicDBObject document = new BasicDBObject();
                document.put("_id", String.valueOf(i));
                document.put("productid", String.valueOf(i));
                document.put("price", (new Float(System.getenv("STUB_PRICE"))*i));
                document.put("createdDate", new Date());
                list.add(document);
            }
            System.out.println(prices.insert(list));
        }catch(Exception e){
            e.printStackTrace();
        }


        System.out.println("Connection to server sucessfully");

    }
    @Override
    public Product get(String productId) throws NotFoundException, InternalException {

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

        //jedis.set(productId,""+price);
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
        DBCursor cursor = prices.find(query);
        SearchResponse response = new SearchResponse();
        while(cursor.hasNext()){
            DBObject object = cursor.next();
            Product product = new Product();
            product.price = object.get("price").toString();
            product.productId = object.get("productid").toString();

            response.products.add(product);
        }

        return response;
    }
}
