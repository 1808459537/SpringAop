package com.zht.springframework.aop;

import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;

@Getter
@Setter
public class AdvisedSupport {
    // ProxyConfig
    private boolean proxyTargetClass = false;

    //被代理的目标，真正干活的
    private TargetSource targetSource;

    // 方法拦截器
    private MethodInterceptor methodInterceptor;

    //方法匹配器
    private MethodMatcher methodMatcher;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }
}
