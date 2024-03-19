package com.zht.springframework.aop;

import java.lang.reflect.Method;

public class UserServiceBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) {
        System.out.println("拦截方法：" + method.getName());
    }
}
