package com.example.bbs.util;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchedulerUtil {
    // 全局唯一的 ScheduledExecutorService 实例
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(3);

    // 提供一个公共的 getter 方法供外部使用
    public static ScheduledExecutorService getScheduler() {
        return SCHEDULER;
    }

    // 应用关闭时需要手动调用此方法来释放线程池资源
    public static void shutdownScheduler() {
        SCHEDULER.shutdown();
    }
}