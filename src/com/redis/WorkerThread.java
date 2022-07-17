package com.redis;

import java.util.Set;

public class WorkerThread implements Runnable {
    private SequenceGeneratorTime sequenceGenerator;
    private Set<Long> set;
    public WorkerThread(SequenceGeneratorTime sequenceGenerator, Set<Long> set){
        this.sequenceGenerator = sequenceGenerator;
        this.set = set;
    }
    public void run() {
        Long l = null;
        try {
            l = sequenceGenerator.generateId();
            if(set.contains(l)){
                System.out.println("exits"+ l);
                System.out.println("Size of set:  "+ set.size());
            }else{
                set.add(l);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
