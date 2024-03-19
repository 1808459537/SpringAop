package com.zht.springframework.beans.factory.support;
import com.zht.springframework.beans.factory.FactoryBean;
import com.zht.springframework.beans.factory.config.BeanDefinition;
import com.zht.springframework.beans.factory.config.BeanPostProcessor;
import com.zht.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.zht.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanFactory extends  FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();


    public ClassLoader getBeanClassLoader(){
        return classLoader;
    }
    @Override
    public Object getBean(String name) {
        return doGetBean(name ,null);
    }

    @Override
    public Object getBean(String name , Object ...args) {
      return doGetBean(name ,args);
    }



    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return (T) getBean(name);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }


    //获得bean的雏形 ，先从缓存中拿，没有就创建
    protected  <T>T  doGetBean(String name, Object[] args){
        Object sharedInstance = getSingleton(name);
        if(sharedInstance != null){
            return (T) getObjectForBeanInstance(sharedInstance, name); // 让bean过一下该方法，判断是不是该接口的实例
        }
        BeanDefinition beanDefinition = getBeanDefinition(name);

        Object bean = createBean(name, beanDefinition, args);
        return (T) getObjectForBeanInstance(bean, name);
    }

    protected  Object getObjectForBeanInstance(Object bean, String name){
        if(!(bean instanceof FactoryBean)){
            return bean;
        }
        Object object = getCachedObjectForFactoryBean(name);
        if(object == null){
            FactoryBean<?> factoryBean = (FactoryBean)bean;
            object = getObjectFromFactoryBean(factoryBean ,name);
        }
        return object;

    }

    ;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition ,Object[] args);

    protected abstract BeanDefinition getBeanDefinition(String beanName);

}
