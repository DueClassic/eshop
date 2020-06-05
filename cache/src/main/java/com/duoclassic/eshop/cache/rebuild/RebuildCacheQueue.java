package com.duoclassic.eshop.cache.queue;

import java.util.concurrent.ArrayBlockingQueue;

public class RebuildCacheQueue {

    private ArrayBlockingQueue<String> queue=new ArrayBlockingQueue<String>(1000);


    /**
     * 内部单例类
     */
    public static class Singleton{

        private static RebuildCacheQueue instance;

        static {
            instance=new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance(){
            return instance;
        }
    }

    public static RebuildCacheQueue getInstance(){
        return getInstance();
    }

    public static void init(){
        getInstance();
    }
}
