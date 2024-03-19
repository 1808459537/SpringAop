package com.zht.springframework.aop.aspectj;
import com.zht.springframework.aop.Pointcut;
import com.zht.springframework.aop.PointcutAdvisor;
import org.aopalliance.aop.Advice;




/*
    可以看成是上下文，做缝合工作的
 */

public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    // 切面
    private AspectJExpressionPointcut pointcut;


    //具体的拦截方法
    private Advice advice;



    //表达式
    private String expression;

    //拿到expression，提供给能解析expression的实例用
    public void setExpression(String expression){
        this.expression = expression;
    }


    @Override
    public Pointcut getPointcut() {
        if(pointcut == null){
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice){
        this.advice = advice;
    }
}
