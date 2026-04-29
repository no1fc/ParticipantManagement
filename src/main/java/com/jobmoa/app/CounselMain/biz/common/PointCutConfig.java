package com.jobmoa.app.CounselMain.biz.common;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointCutConfig {

    @Pointcut("execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))")
    public void daoLogPointcut() {}
}
