package com.javarush.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditingAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditingAspect.class);

    @Before("execution(* com.javarush.service.*.*(..))")
    public void auditBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Аудит: вызван метод {} с аргументами: {}", methodName, args);
    }

}
