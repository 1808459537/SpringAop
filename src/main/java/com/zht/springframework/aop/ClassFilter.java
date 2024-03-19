package com.zht.springframework.aop;

public interface ClassFilter {

    boolean matches(Class<?> clazz);

}
