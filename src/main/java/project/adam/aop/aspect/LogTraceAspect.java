package project.adam.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.UUID;

@Slf4j
@Aspect
public class LogTraceAspect {

    private final ThreadLocal<String> uuidPrefix = new ThreadLocal<>();

    @Around("project.adam.aop.Pointcuts.logTarget()")
    public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isNew = false;
        if (uuidPrefix.get() == null) {
            isNew = true;
            uuidPrefix.set(UUID.randomUUID().toString().substring(0, 8));
        }
        log.info("[{}] {}", uuidPrefix.get(), joinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} Success in {}ms", uuidPrefix.get(), joinPoint.getSignature(), endTime - startTime);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} Fail in {}ms", uuidPrefix.get(), joinPoint.getSignature(), endTime - startTime);
            throw e;
        } finally {
            if (isNew) {
                uuidPrefix.remove();
            }
        }
    }
}
