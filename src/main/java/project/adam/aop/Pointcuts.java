package project.adam.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    @Pointcut("execution(* project.adam..*(..))")
    private void allObject() {}

    @Pointcut("execution(* project.adam.service..*(..))")
    private void serviceObject() {}

    @Pointcut("execution(* project.adam.aop..*(..))")
    private void aopObject() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestHeader)")
    private void sessionObject() {}

    @Pointcut("controllerObject() && !aopObject()")
    public void logTarget() {}

    @Pointcut("execution(* project.adam.controller..*(..))")
    public void controllerObject() {}
}
