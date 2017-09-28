package io.reda.pricing;

import io.reda.domain.Product;

public interface Price {

    public Product get(String productId);
    public void set(String productId,double price);
    public SearchResponse prices(SearchRequest request);

}
