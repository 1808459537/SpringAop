package com.zht.springframework.aop;


/*
    PointcutAdvisor 承担了 Pointcut 和 Advice 的组合，Pointcut 用于获取 JoinPoint，而 Advice 决定于 JoinPoint 执行什么操作。
 */
public interface PointcutAdvisor extends Advisor {


    // Pointcut 是集成了类过滤器与方法比较器  切点通常用于指定在程序执行过程中的某些特定点来执行额外的逻辑或行为。
    Pointcut getPointcut();

}
