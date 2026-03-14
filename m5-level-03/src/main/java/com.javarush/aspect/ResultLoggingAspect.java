package com.javarush.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Aspect
@Component
public class ResultLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ResultLoggingAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.javarush.service.*.*(..))",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Метод {} успешно завершен, результат {}", methodName, result);
    }

}
