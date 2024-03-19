package com.zht.springframework.aop.framework;

import com.zht.springframework.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//代理模式怎么写，这个就怎么写，只是换成了JDK动态代理罢了
public class JdkDynamicAopProxy implements AopProxy , InvocationHandler {
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }
    @Override
    public Object getProxy() {

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),advised.getTargetSource().getTargetClass(),this);

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
            代理中的代理 ，JDK的动态代理是检查，如果满足要求就再代理出去执行相应的操作
         */
        if(advised.getMethodMatcher().matches(method , advised.getTargetSource().getTarget().getClass())){

            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();

            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        }
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }
}
