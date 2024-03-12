package com.zht.springframework.context.support;

import com.zht.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.zht.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.zht.springframework.beans.factory.config.BeanPostProcessor;
import com.zht.springframework.context.ConfigurableApplicationContext;
import com.zht.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    @Override
    public void registerShutdownHook() {

        //  JVM最后执行的步骤
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));// lambda表达式 ，()->(),前面是参数，后面是方法，如果参数是个引用(或者无参)并且方法里面使用了该引用，->可以替换为::
    }

    @Override
    public void close() {

        getBeanFactory().destroySingletons();}

    @Override
    public void refresh() {
        //创建 BeanFactory，并加载 BeanDefinition
        refreshBeanFactory();


        //获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        //添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的 ApplicationContext
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));


        //在 Bean 实例化之前，执行 BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);


        //BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
        registerBeanPostProcessors(beanFactory);

        beanFactory.preInstantiateSingletons();
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String , BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor b :
                beanFactoryPostProcessorMap.values()) {
            b.postProcessBeanFactory(beanFactory);
        }
    }

    protected abstract void refreshBeanFactory() ;

    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return null;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public Object getBean(String name) {
        return null;
    }

    @Override
    public Object getBean(String name, Object... args) {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name, requiredType);
    }
}
