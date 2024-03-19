package com.zht.springframework.aop.framework.autoproxy;

import com.zht.springframework.aop.*;
import com.zht.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.zht.springframework.aop.framework.ProxyFactory;
import com.zht.springframework.beans.factory.BeanFactory;
import com.zht.springframework.beans.factory.BeanFactoryAware;
import com.zht.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.zht.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;

public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory)beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        if(isInfrastructureClass(beanClass))return null;

        // 拿到Map里面的value值
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors
             ) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(beanClass)) continue;

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = null;
            try{
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            }catch (Exception e){
                System.out.println("com.zht.springframework.aop.framework.atuoproxy.DefaultAdvisorAutoProxyCreator" + "异常");
            }

            advisedSupport.setTargetSource(targetSource);

            advisedSupport.setMethodInterceptor((MethodInterceptor)advisor.getAdvice());

            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            // 代理方式的选择 advisedSupport.setProxyTargetClass(false);
            return new ProxyFactory(advisedSupport).getProxy();
        }
        return null;
    }


    // 是否是基础设施类
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
