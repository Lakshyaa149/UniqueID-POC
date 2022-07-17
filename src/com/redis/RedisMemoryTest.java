package com.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisMemoryTest {

    public static void main( String[] args )
    {
        Long time = System.currentTimeMillis();
        SequenceGeneratorTime sequenceGenerator = new SequenceGeneratorTime();
        ExecutorService executor = Executors.newFixedThreadPool(300);
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < 50000000; i++) {
            Runnable worker = new WorkerThread(sequenceGenerator,set);
            executor.execute(worker);//calling execute method of ExecutorService
        }
        executor.shutdown();
        while (!executor.isTerminated()) {   }

        System.out.println(set.size());

        System.out.println("Finished all threads---"+sequenceGenerator.getAtomicLong().get());
        sequenceGenerator.getRedisson().shutdown();
        System.out.println("Time  "+(System.currentTimeMillis() - time));
    }


}

