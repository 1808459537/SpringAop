package com.zht.springframework.aop;


public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    public Class<?>[] getTargetClass(){
        return target.getClass().getInterfaces();
    }
    public Object getTarget(){
        return target;
    }
}
