package project.adam.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* project.adam.controller..*(..))")
    public void controllerObject() {}

    @Pointcut("execution(* project.adam.aop..*(..))")
    private void aopObject() {}

    @Pointcut("controllerObject() && !aopObject()")
    public void logTarget() {}
}
