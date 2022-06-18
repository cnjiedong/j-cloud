package cn.ljd.framework.javaskill.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HashMapTest {
    static void doit() throws Exception {
        final int count = 200;
        final AtomicInteger checkNum = new AtomicInteger(0);
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(100);
        //
        final Map<Long, String> map = new HashMap<>();
        map.put(0L, " www.imxylz.cn ");
        for (int j = 0; j < count; j++) {
            newFixedThreadPool.submit(new Runnable() {
                public void run() {
                    map.put(System.nanoTime() + new Random().nextLong(), " www.imxylz.cn ");
                    String obj = map.get(0L);
                    if (obj == null) {
                        checkNum.incrementAndGet();
                    }
                }
            });
        }
        newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS);
        newFixedThreadPool.shutdown();

        System.out.println(checkNum.get());
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            doit();
            Thread.sleep(500L);
        }
    }
}
