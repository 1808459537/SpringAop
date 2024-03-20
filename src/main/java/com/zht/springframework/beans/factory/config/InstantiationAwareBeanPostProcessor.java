package com.zht.springframework.beans.factory.config;

import com.zht.springframework.beans.PropertyValues;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{

    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName);

    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName);

}
