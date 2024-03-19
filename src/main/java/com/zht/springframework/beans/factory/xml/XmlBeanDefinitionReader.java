package com.zht.springframework.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import com.zht.springframework.beans.PropertyValue;
import com.zht.springframework.beans.factory.config.BeanDefinition;
import com.zht.springframework.beans.factory.config.BeanReference;
import com.zht.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.zht.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.zht.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.zht.springframework.core.io.Resource;
import com.zht.springframework.core.io.ResourceLoader;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.InputStream;
import java.util.List;

// 真实的落地类
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    // 入参是一个容器，真正干活的工厂又继承了这个容器，就可以把注册这个过程迁移到这个类里
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            doLoadBeanDefinitions(inputStream);
        }catch (Exception e){}
    }

    @Override
    public void loadBeanDefinitions(String... locations)  {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }


    @Override
    public void loadBeanDedinitions(Resource... resources) {
        for (Resource re:resources
             ) {
            loadBeanDefinitions(re);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }


    private void doLoadBeanDefinitions(InputStream inputStream)throws Exception {
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(inputStream);
        org.dom4j.Element root = document.getRootElement();


        org.dom4j.Element componentScan = root.element("component-scan");
        if(componentScan != null){
            String scanPath = componentScan.attributeValue("base-package");
            if(StrUtil.isEmpty(scanPath)){
                System.out.println("scanPath 为空");
                throw new RuntimeException();
            }
            scanPackage(scanPath);
        }
        List<Element> beanList = root.elements("bean");
        for (Element bean : beanList) {

            String id = bean.attributeValue("id");
            String name = bean.attributeValue("name");
            String className = bean.attributeValue("class");
            String initMethod = bean.attributeValue("init-method");
            String destroyMethodName = bean.attributeValue("destroy-method");
            String beanScope = bean.attributeValue("scope");

            // 获取 Class，方便获取类中的名称
            Class<?> clazz = Class.forName(className);
            // 优先级 id > name
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 定义Bean
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethod);
            beanDefinition.setDestoryMethodName(destroyMethodName);

            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            List<Element> propertyList = bean.elements("property");
            // 读取属性并填充
            for (Element property : propertyList) {
                // 解析标签：property
                String attrName = property.attributeValue("name");
                String attrValue = property.attributeValue("value");
                String attrRef = property.attributeValue("ref");
                // 获取属性值：引入对象、值对象
                Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;
                // 创建属性信息
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new RuntimeException();
            }
            // 注册 BeanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }

    }

    private void scanPackage(String scanPath) {
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(basePackages);
    }
}
