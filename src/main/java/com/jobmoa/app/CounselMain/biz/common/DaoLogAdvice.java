package com.jobmoa.app.CounselMain.biz.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Aspect
public class DaoLogAdvice {
    @Before("PointCutConfig.daoLogPointcut()") // 전 실행
    public void printLogBefore(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.info("로그 : [{}] 메서드 수행 전 호출",methodName);
    }
    @AfterReturning("PointCutConfig.daoLogPointcut()") // 성공 실행
    public void printLogAfterReturning(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.info("로그 : [{}] 메서드 수행 성공",methodName);
    }
    @AfterThrowing("PointCutConfig.daoLogPointcut()") // 오류 시 실행
    public void printLogAfterThrowing(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.error("로그 : [{}] 메서드 수행 실패",methodName);
    }
}
