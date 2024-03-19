package com.zht.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;  // 用于获取类加载器、加载类、扫描指定包下的类等操作
import com.zht.springframework.beans.factory.config.BeanDefinition;
import com.zht.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

// 类路径扫描候选组件提供者
public class ClassPathScanningCandidateComponentProvider {
    public Set<BeanDefinition> findCandidateComponents(String basePackage){
        Set<BeanDefinition> candidates = new LinkedHashSet<>();

        //  扫描指定包下标注了特定注解的类。该方法可以扫描指定包下的所有类，然后返回标注了指定注解的类列表  (就是扫描某个包含有指定注解的类)
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage , Component.class);
        for (Class<?> clazz:classes
             ) {
            candidates.add(new BeanDefinition(clazz));
        }
        return candidates;
    }
}
