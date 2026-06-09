package com.jobmoa.app.CounselMain.biz.common;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * AOP 포인트컷 정의 클래스.
 *
 * <p>CounselMain 서비스 구현체({@code *Impl})를 대상으로 하는
 * 공통 포인트컷 표현식을 한 곳에서 관리한다.
 * {@link DaoLogAdvice} 등의 어드바이스에서 참조하여 사용한다.
 */
@Aspect
public class PointCutConfig {

    @Pointcut("execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))")
    public void daoLogPointcut() {}
}
