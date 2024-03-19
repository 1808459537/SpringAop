package com.zht.springframework.aop.aspectj;

import com.zht.springframework.aop.ClassFilter;
import com.zht.springframework.aop.MethodMatcher;
import com.zht.springframework.aop.Pointcut;

/*
    在 Spring AOP 中，PointcutPrimitive 是一个枚举类型，用于表示 AspectJ 表达式中的切点原语（pointcut primitive）。AspectJ 表达式允许您使用各种切点原语来定义切点，以匹配特定的方法。
    PointcutPrimitive 枚举定义了各种切点原语，包括方法调用、方法执行、字段访问等。每个枚举常量代表一种切点原语，您可以在 AspectJ 表达式中使用这些枚举常量来匹配相应的切点。
    以下是 PointcutPrimitive 枚举的一些常见枚举常量：
    CALL：表示方法调用。
    EXECUTION：表示方法执行。// 常用这个
    GET：表示字段获取。
    SET：表示字段设置。
    INITIALIZATION：表示对象初始化。
    STATICINITIALIZATION：表示静态初始化。
    HANDLER：表示异常处理器。
*/
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AspectJExpressionPointcut implements ClassFilter , MethodMatcher, Pointcut {

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);// 猜测应该是正则表达式匹配的方式
    }

    /*
        通常使用 PointcutExpression 来构建和解析复杂的 AspectJ 表达式，而 PointcutPrimitive 则作为其中的基本构建块
     */

    private final PointcutExpression pointcutExpression;

    public AspectJExpressionPointcut(String expression){

        /*
            PointcutParser 通常与 PointcutExpression 结合使用，用于解析和评估
         */
        PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES ,this.getClass().getClassLoader()); //获取支持指定原语并使用指定类加载器进行解析的切点解析器
        pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }
    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
