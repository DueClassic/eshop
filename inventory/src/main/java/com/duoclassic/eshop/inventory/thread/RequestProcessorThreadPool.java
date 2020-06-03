package com.duoclassic.eshop.inventory.thread;

import com.duoclassic.eshop.inventory.request.Request;
import com.duoclassic.eshop.inventory.request.RequestQueues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求处理线程池：单例
 */
public class RequestProcessorThreadPool {

    /**
     * 线程池:10
     */
    private ExecutorService threadPool= Executors.newFixedThreadPool(10);

    public RequestProcessorThreadPool(){
        RequestQueues requestQueues=RequestQueues.getInstance();
        for (int i=0;i<10;i++){
            ArrayBlockingQueue<Request> queue=new ArrayBlockingQueue<Request>(100);
            requestQueues.addQueue(queue);
            threadPool.submit(new RequestProcessorThread(queue));
        }
    }

    /**
     * 使用静态内部类的方式，去初始化单例
     */
    private static class Singleton{

        private static RequestProcessorThreadPool instance;

        static {
            instance=new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance(){
            return instance;
        }
    }

    /**
     * jvm的机制去保证多线程并发安全
     *
     * 内部类的初始化，一定只会发生一次
     *
     * @return  instance
     */
    public static RequestProcessorThreadPool getInstance(){
        return Singleton.getInstance();
    }

    /**
     * 初始化的便捷方法
     */
    public static void init(){
        getInstance();
    }
}
