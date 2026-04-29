package com.jobmoa.app.CounselMain.biz.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Slf4j
@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 문자열 저장/조회
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    // 인증번호 만료시간 및 요청 횟수 설정
    /**
     * 사용자가 인증 번호를 요청하면 키값을 확인
     * 확인한 키값에 대한 인증 횟수, 시간 추가 지정
     *
     * @param key Primary Key
     * @param expireTime Second Time Data
     * @param maxAuthCount 최대 인증 횟수 (Default 10)
     */
    public void setStringWithExpire(String key, int maxAuthCount, int expireTime) throws TimeoutException {
        maxAuthCount = maxAuthCount < 1 ? 10 : maxAuthCount;
        log.info("RedisService setStringWithExpire key : [{}], maxAuthCount : [{}], expireTime : [{}]", key, maxAuthCount, expireTime);

        // 전달 받은 key 값에 인증 횟수를 증가
        Long count = redisTemplate.opsForValue().increment(key,1);
        log.info("RedisService setStringWithExpire key : [{}], count : [{}]", key, count);

        // 첫 요청에 인증키에 대한 시간을 설정
        if(count == 1) {
            //요청 인증키에 시간을 설정
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        }

        //count 가 null이 아니고 maxAuthCount 보다 클때 오류를 발생
        if(count != null && count > maxAuthCount) {
            throw new TimeoutException("요청 횟수 초과로 ["+(expireTime / 60)+"]분 이후 다시 진행해주세요.");
        }

    }

    // 만료시간 설정
    public void setStringWithExpire(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 객체 저장/조회
    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Hash 사용
    public void setHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    // 키 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 키 존재 확인
    public boolean exists(String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }
}
