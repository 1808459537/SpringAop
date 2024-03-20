package com.zht.springframework.beans.factory;

public interface BeanFactory {
    public Object getBean(String name);
    public Object getBean(String name, Object... args);
    <T> T getBean(String name, Class<T> requiredType);
    <T> T getBean(Class<T> requiredType);
}
