package com.duoclassic.eshop.inventory.service;

import com.duoclassic.eshop.inventory.request.Request;

/**
 * 请求异步执行的service
 */
public interface RequestAsyncProcessService {

    void handle(Request request);
}
