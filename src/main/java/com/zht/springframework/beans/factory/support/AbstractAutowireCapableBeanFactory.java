package com.zht.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zht.springframework.beans.PropertyValue;
import com.zht.springframework.beans.PropertyValues;
import com.zht.springframework.beans.factory.*;
import com.zht.springframework.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {


    //自定义对象，功能为封装java反射机制并创建对象
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    //模版模式的实现，实现父类中的抽象方法
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition ,Object args[]) {
        Object bean = null;

        try{
            // 提前判断是否代理
            bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (null != bean) {
                return bean;
            }
            // 进入bean的生命周期
            //创建分两步：1类信息new对象（分配地址） 2对象填充（地址填值）
            //step1:
            bean = createBeanInstance(beanDefinition, beanName, args);


            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);

            //step2:
            applyPropertyValues(beanName, bean, beanDefinition);

            // step3:后续增加第三步，填充bean
            bean = initializeBean(beanName, bean, beanDefinition);

        }catch (Exception e){}

        // 注册实现了 DisposableBean 接口的 Bean 对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        //bean创建完毕，由于该工厂分支为单例路线，所以把bean填充到单例容器中

        if (beanDefinition.isSingleton()) {
            addSingleton(beanName, bean);
        }
        return bean;
    }


    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor){
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (null != pvs) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (!beanDefinition.isSingleton()) return;
        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestoryMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }

    // Aop第一步
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (null != bean) {
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    //Aop第二步
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                Object result = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
                if (null != result) return result;
            }
        }
        return null;
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 感知
        // invokeAwareMethods

        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware){
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }


        // 1. 执行 BeanPostProcessor Before 处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        //2. 执行 Bean 对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            System.out.println("invokeInitMethods +  失败" );
        }

        // 3. 执行 BeanPostProcessor After 处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }



    // step1的外包工作
    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try{
            //拿到bean的属性集合以及属性值
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue p:propertyValues.getPropertyValues()
                 ) {
                String name = p.getName();
                Object value = p.getValue();
                if(value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                //填充属性，完整的bean诞生
                BeanUtil.setFieldValue(bean, name, value);
            }
        }catch (Exception e){

        }
    }
    //step2的外包工作
    private Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        //拿到构造器后外包出去
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor ctor:declaredConstructors
             ) {
            if(args != null && ctor.getParameterTypes().length == args.length){
                constructorToUse = ctor;
                break;
            }
        }

        // 反射机制的使用根据类信息创建对象，其中需要的参数只有构造器未知，上面的代码就是根据长度确定唯一的构造器
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    private InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }






    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
            if(bean instanceof InitializingBean){
                ((InitializingBean) bean).afterPropertiesSet();
            }

            String InitMethodName = beanDefinition.getInitMethodName();
            if(StrUtil.isNotEmpty(InitMethodName)){
                //从bean定义中拿到bean的class就拿到了一切
                Method initMethod = beanDefinition.getBeanClass().getMethod(InitMethodName);
                //反射调用bean的对应方法
                initMethod.invoke(bean);
            }
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }


}
