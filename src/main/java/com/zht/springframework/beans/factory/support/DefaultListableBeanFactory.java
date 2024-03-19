package com.zht.springframework.beans.factory.support;

import com.zht.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.zht.springframework.beans.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;


 // 底层实现类，最终落地的 ==>启动顺序：最终落地的bean工厂 new一个自身A ==> 向A中注册类信息==> 在A中取bean对象（getBean由父类实现） ### 其实完全可以写在一个类里，是设计模式让他们分开
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements  BeanDefinitionRegistry , ConfigurableListableBeanFactory {

    // BeanDefinition定义的容器， 其实本类还有单例容器，通过继承获得
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // BeanDefinition的注册，最底层的东西，上层的玩意全部依赖这个，可以手动注册，也可写个类来外包注册。。。
    // 外包就是把这个方法抽象到接口里面
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            System.out.println("没有 beanDefinition");
            return null;
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            /*
                isAssignableFrom  这个方法用于检查一个类是否是另一个类的子类或者接口的实现类。
             */
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    @Override
    public void preInstantiateSingletons() {
        beanDefinitionMap.keySet().forEach(this::getBean);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }
}
