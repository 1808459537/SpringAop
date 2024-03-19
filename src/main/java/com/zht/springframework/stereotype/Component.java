package com.zht.springframework.stereotype;


/*
    Stereotype  可以翻译为 "原型" 或 "陈规"。在计算机编程中，它通常指代一种标记或注解，用于为类或方法添加额外的信息，以便在框架或工具中进行特定处理。
 */
import java.lang.annotation.*;

@Target({ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
