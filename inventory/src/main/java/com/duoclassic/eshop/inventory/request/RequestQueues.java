package com.duoclassic.eshop.inventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 */
public class RequestQueues {

    /**
     * 内存队列
     */
    private List<ArrayBlockingQueue<Request>> queues=new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 标识位
     */
    private Map<Integer,Boolean> flagMap=new ConcurrentHashMap<Integer, Boolean>();

    /**
     * 使用静态内部类的方式，去初始化单例
     */
    private static class Singleton{

        private static RequestQueues instance;

        static {
            instance=new RequestQueues();
        }

        public static RequestQueues getInstance(){
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
    public static RequestQueues getInstance(){
        return RequestQueues.Singleton.getInstance();
    }

    /**
     * 添加一个内存队列
     * @param queue
     */
    public void addQueue(ArrayBlockingQueue<Request> queue){
        this.queues.add(queue);
    }

    /**
     * 获取内存队列的数量
     * @return
     */
    public int queueSize(){
        return queues.size();
    }

    /**
     * 获取内存队列
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index){
        return queues.get(index);
    }

    /**
     * 获取标识
     * @return
     */
    public Map<Integer,Boolean> getFlagMap(){
        return flagMap;
    }
}
