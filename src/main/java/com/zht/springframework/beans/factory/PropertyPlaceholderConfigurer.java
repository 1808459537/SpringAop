package com.zht.springframework.beans.factory;


import com.zht.springframework.beans.PropertyValue;
import com.zht.springframework.beans.PropertyValues;
import com.zht.springframework.beans.factory.config.BeanDefinition;
import com.zht.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.zht.springframework.core.io.DefaultResourceLoader;
import com.zht.springframework.core.io.Resource;

import java.util.Properties;

public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    // 默认占位符前缀
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    // 默认占位符后缀
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";


    String location ;
    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        try{
            DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
            Resource resource = defaultResourceLoader.getResource(location);

            // Properties 类通常用于处理配置文件
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            String []beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames
                 ) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue :propertyValues.getPropertyValues()
                        ) {
                    Object value = propertyValue.getValue();
                    if(!(value instanceof String )) continue;
                    String strVal =  (String)value;
                    StringBuilder buffer = new StringBuilder(strVal);

                    int startIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
                    int stopIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);

                    if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
                        String propKey = strVal.substring(startIdx + 2, stopIdx);
                        String propVal = properties.getProperty(propKey);
                        buffer.replace(startIdx, stopIdx + 1, propVal);
                        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), buffer.toString()));
                    }
                }
            }

        }catch (Exception e){
            System.out.println("不能加载properties配置文件");
            throw new RuntimeException();
        }
    }
}
