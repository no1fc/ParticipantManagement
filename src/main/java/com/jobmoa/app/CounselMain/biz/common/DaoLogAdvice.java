package com.jobmoa.app.CounselMain.biz.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

/**
 * DAO 계층 AOP 로그 어드바이스.
 *
 * <p>{@link PointCutConfig#daoLogPointcut()}에 정의된 포인트컷에 매칭되는
 * CounselMain 서비스 구현체 메서드의 실행 전/후/예외 시점에 로그를 출력한다.
 */
@Slf4j
@Service
@Aspect
public class DaoLogAdvice {

    /**
     * 대상 메서드 실행 전 호출되어 메서드명을 로그에 기록한다.
     *
     * @param jp 조인 포인트 정보
     */
    @Before("PointCutConfig.daoLogPointcut()")
    public void printLogBefore(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.info("로그 : [{}] 메서드 수행 전 호출",methodName);
    }
    /**
     * 대상 메서드가 정상 반환된 후 호출되어 성공 로그를 기록한다.
     *
     * @param jp 조인 포인트 정보
     */
    @AfterReturning("PointCutConfig.daoLogPointcut()")
    public void printLogAfterReturning(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.info("로그 : [{}] 메서드 수행 성공",methodName);
    }
    /**
     * 대상 메서드에서 예외가 발생했을 때 호출되어 실패 로그를 기록한다.
     *
     * @param jp 조인 포인트 정보
     */
    @AfterThrowing("PointCutConfig.daoLogPointcut()")
    public void printLogAfterThrowing(JoinPoint jp) {
        String methodName = jp.getSignature().getName(); // 메서드명
        log.error("로그 : [{}] 메서드 수행 실패",methodName);
    }
}
