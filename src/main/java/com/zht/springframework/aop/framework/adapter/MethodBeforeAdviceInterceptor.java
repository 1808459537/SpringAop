package com.zht.springframework.aop.framework.adapter;

import com.zht.springframework.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor() {
    }

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    public MethodBeforeAdvice getAdvice(){
        return advice;
    }

    public void setAdvice(MethodBeforeAdvice advice){
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        /*
            这个地方已经是代理了，但是是框架里面的代码，所以又调用before方法，外包出去，让切面定义者自己实现
            重点就变成了调用before方法的advice是怎么来的，（bean配置XML中指定的，这样只要指定自己写的代码就可以完成AOP）
        */
       this.advice.before(methodInvocation.getMethod(),methodInvocation.getArguments(),methodInvocation.getThis());

       return methodInvocation.proceed();
    }
}
