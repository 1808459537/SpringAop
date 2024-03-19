package com.zht.springframework.aop.framework;

import com.zht.springframework.aop.AdvisedSupport;

public class ProxyFactory {

    private AdvisedSupport advisedSupport;

    public ProxyFactory(AdvisedSupport advisedSupport){
        this.advisedSupport = advisedSupport;
    }

    private AopProxy createAopPorxy(){
        if(advisedSupport.isProxyTargetClass())
            return new Cglib2AopProxy(advisedSupport);

        return new JdkDynamicAopProxy(advisedSupport);
    }

    public Object getProxy(){
        return createAopPorxy().getProxy();
    }
}
