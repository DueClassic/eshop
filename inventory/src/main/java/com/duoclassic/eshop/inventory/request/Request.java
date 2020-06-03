package com.duoclassic.eshop.inventory.request;

public interface Request {

    void process();

    Integer getProductId();

    boolean isForceRefresh();
}
