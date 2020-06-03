package com.duoclassic.eshop.inventory.thread;

import com.duoclassic.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.duoclassic.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.duoclassic.eshop.inventory.request.Request;
import com.duoclassic.eshop.inventory.request.RequestQueues;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 */
public class RequestProcessorThread implements Callable {

    /**
     * 自己监控的内存队列
     */
    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue){
        this.queue=queue;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            while (true){
                //如果队列是满的或者是空，那么都会在执行操作的时候，阻塞住
                Request request=queue.take();
                boolean forceRefresh=request.isForceRefresh();

                if (!forceRefresh){
                    //先做读请求的去重
                    RequestQueues requestQueues=RequestQueues.getInstance();
                    Map<Integer, Boolean> flagMap=requestQueues.getFlagMap();
                    if (request instanceof ProductInventoryDBUpdateRequest){
                        //如果是更新数据库的请求，就将productId对应的标识设置为true
                        flagMap.put(request.getProductId(),true);
                    }else if (request instanceof ProductInventoryCacheRefreshRequest){
                        Boolean flag=flagMap.get(request.getProductId());

                        if (null==flag){
                            flagMap.put(request.getProductId(),false);
                        }

                        //如果是缓存刷新的请求，如果标识不为空，而且是true，
                        // 就说明之前有一个这个商品的数据库更新请求
                        if (flag!=null && flag){
                            flagMap.put(request.getProductId(),false);
                        }

                        //如果是缓存刷新的请求，而且标识不为空，但是标识是false，
                        // 就说明已经有一个数据库更新请求+一个缓存刷新请求
                        if (flag!=null && !flag){
                            //如果是这种重复的读请求，不要放入内存队列中了
                            return true;
                        }
                    }
                }
                System.out.println("===========日志===========: 工作线程处理请求，商品id=" + request.getProductId());
                //执行process操作
                request.process();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
