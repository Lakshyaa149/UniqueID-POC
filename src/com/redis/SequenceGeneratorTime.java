package com.redis;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class SequenceGeneratorTime {

    public RedissonClient getRedisson() {
        return redisson;
    }

    public RAtomicLong getAtomicLong() {
        return atomicLong;
    }

    private RedissonClient redisson;
    private RAtomicLong atomicLong;
    //This will go to property file
    private final Long expiryTime = 1800000L;
    //This will go to property file
    private final Long adHocExpiryTime = 1L;

    private final Long oldTime = 1609439400000L;

    public SequenceGeneratorTime() {
        redisson = getClient();
        //This will go to property file
        atomicLong = redisson.getAtomicLong("myAtomicLong");
        atomicLong.expire(expiryTime, TimeUnit.MILLISECONDS);
    }

    private long timestamp() {
        return (Instant.now().toEpochMilli()-oldTime);
    }


    private RedissonClient getClient() {
        Config config = new Config();
        // use single Redis server
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    public long generateId() throws InterruptedException {
        if(atomicLong.remainTimeToLive() < 0) {
            setExpiry(expiryTime);
        }
        long currentTimestamp = timestamp();
        Long redisId = atomicLong.incrementAndGet();
        String compositeId = currentTimestamp +""+ redisId;
        Long id = getValidId(compositeId);
        if(id < 0){
            System.out.println("Id : "+id);
            System.out.println(compositeId);
        }
        return id;
    }

    private void setExpiry(Long expiryTime){
        atomicLong.expire(expiryTime, TimeUnit.MILLISECONDS);
    }

    private Long getValidId(String compositeId) throws InterruptedException {
        Long id = 0L;
        if(compositeId.length() > 19){
            System.out.println("Length crossed");
            id = regenerateId();
        }else{
            try{
                id = Long.valueOf(compositeId);
            }catch (Exception ex){
                id = regenerateId();
            }
        }
        return id;

    }

    private synchronized Long regenerateId() throws InterruptedException {
        setExpiry(adHocExpiryTime);
        Thread.sleep(10L);
        return generateId();
    }

}
