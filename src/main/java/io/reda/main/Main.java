package io.reda.main;

import static spark.Spark.*;
public class Main {


    public static void main(String args[]){

        get("/hello", (req, res) -> "Hello World");
        get("/prices/:id", PriceRoute.getPrice  );
        post("/prices/search",PriceRoute.searchPrices);
        get("/prices/search/:id",PriceRoute.getPriceList);
    }


}
