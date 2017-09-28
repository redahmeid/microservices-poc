package io.reda.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.reda.domain.Product;
import io.reda.exceptions.InternalException;
import io.reda.exceptions.NotFoundException;
import io.reda.pricing.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;

public class PriceRoute {


    public static Route getPrice = (Request req, Response res) -> {
        Price price = new MongoPrice();
        try{
            Product product = price.get(req.params("id"));
            res.status(200);
            return dataToJson(product);

        }catch(InternalException ie){
            res.status(500);
        }catch(NotFoundException nfe){
            res.status(404);
        }

        return null;
    };

    public static Route searchPrices = (Request req, Response res) -> {
       try {
           Price price = new MongoPrice();
           ObjectMapper mapper = new ObjectMapper();
           SearchRequest search = mapper.readValue(req.body(), SearchRequest.class);

           SearchResponse response = price.prices(search);

           //String response = dataToJson(price.prices(search));
           res.status(303);
           res.header("location","http://localhost:4567/prices/search/");

       }catch (Exception e){
           res.status(400);
       }

       return "";
    };


    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
}
