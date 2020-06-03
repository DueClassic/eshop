package com.duoclassic.eshop.inventory.service.impl;

import com.duoclassic.eshop.inventory.request.Request;
import com.duoclassic.eshop.inventory.request.RequestQueues;
import com.duoclassic.eshop.inventory.service.RequestAsyncProcessService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的Service的实现
 */
@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {
    @Override
    public void handle(Request request) {
        try {
            //做请求的路由，根据每个请求的商品ID，路由到对应的内存队列中
            ArrayBlockingQueue<Request> queue=getRoutingQueue(request.getProductId());
            //将请求放入对应的队列中，完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取路由到的内存队列
     * @param productId 商品ID
     * @return 内存队列
     */
    private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId){
        RequestQueues requestQueues= RequestQueues.getInstance();
        //先获取productID的hash值
        String key=String.valueOf(productId);
        int h;
        int hash = (key==null) ? 0 : (h=key.hashCode())^(h>>>16);

        //对hash值取模，将hash值路由到指定的内存队列中
        int index=(requestQueues.queueSize()-1)&hash;

        System.out.println("===========日志===========: 路由内存队列，商品id=" + productId + ", 队列索引=" + index);

        return requestQueues.getQueue(index);
    }
}
