package com.zht.springframework.context.annotation;

import java.lang.annotation.*;


// ElementType.TYPE: 注解可以应用于类、接口、枚举等类型。
//ElementType.METHOD: 注解可以应用于方法
@Target({ElementType.TYPE ,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented //标记自定义注解是否包含在 Java 文档中 以便开发人员能够在文档中看到该注解的使用说明。
public @interface Scope {
    String value()default "singleton";
}
