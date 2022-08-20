package project.adam.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.UUID;

@Slf4j
@Aspect
public class LogTraceAspect {

    private ThreadLocal<String> uuidPrifix = new ThreadLocal<>();

    @Around("project.adam.aop.Pointcuts.logTarget()")
    public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isNew = false;
        if (uuidPrifix.get() == null) {
            isNew = true;
            uuidPrifix.set(UUID.randomUUID().toString().substring(0, 8));
        }
        log.info("[{}] {}", uuidPrifix.get(), joinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} Success in {}ms", uuidPrifix.get(), joinPoint.getSignature(), endTime - startTime);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} Fail in {}ms", uuidPrifix.get(), joinPoint.getSignature(), endTime - startTime);
            throw e;
        } finally {
            if (isNew) {
                uuidPrifix.remove();
            }
        }
    }
}
