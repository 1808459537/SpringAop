package com.zht.springframework.beans.factory;

public interface BeanFactoryAware extends Aware{
    void setBeanFactory(BeanFactory beanFactory);

}
