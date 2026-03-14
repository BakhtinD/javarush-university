package com.javarush.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("execution(* com.javarush.service.*.*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
       long start = System.nanoTime();
       try {
           // Выполняем целевой метод
           Object result = joinPoint.proceed();
           return  result;
       } finally {
           long duration = System.nanoTime() - start;
           log.info("Метод {} выполнен за {} нс", joinPoint.getSignature().toShortString(), duration);
       }
    }

}
