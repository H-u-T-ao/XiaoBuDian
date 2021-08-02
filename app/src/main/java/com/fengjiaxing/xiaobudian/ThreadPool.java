package com.fengjiaxing.xiaobudian;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * */
public class ThreadPool {

    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(10, 10, 60L,TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>());

}
