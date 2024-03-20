package com.zht.springframework;

import com.zht.springframework.beans.factory.*;
import com.zht.springframework.beans.factory.annotation.Autowired;
import com.zht.springframework.beans.factory.annotation.Value;
import com.zht.springframework.context.ApplicationContext;
import com.zht.springframework.context.ApplicationContextAware;
import com.zht.springframework.stereotype.Component;

import java.util.Random;

@Component("userService")
public class    UserService implements IUserService{
    @Value("${token}")
    private String token;

    @Autowired
    private UserDao userDao;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userDao.queryUserName("10001") + "，" + token;
    }

    @Override
    public String register(String userName) {
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //   private ApplicationContext applicationContext;
//    private BeanFactory beanFactory;
//
//
//    private String uId;
//    private String company;
//    private String location;
//    private IUserDao userDao;
//
//    public String queryUserInfo() {
//        return userDao.queryUserName(uId) + "," + company + "," + location;
//    }
//
//    public String getuId() {
//        return uId;
//    }
//
//    public void setuId(String uId) {
//        this.uId = uId;
//    }
//
//    public String getCompany() {
//        return company;
//    }
//
//    public void setCompany(String company) {
//        this.company = company;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public IUserDao getUserDao() {
//        return userDao;
//    }
//
//    public void setUserDao(IUserDao userDao) {
//        this.userDao = userDao;
//    }
//
//    @Override
//    public void setBeanClassLoader(ClassLoader classLoader) {
//        System.out.println("ClassLoader：" + classLoader);
//    }
//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) {
//        this.beanFactory = beanFactory;
//    }
//
//    @Override
//    public void setBeanName(String name) {
//        System.out.println("Bean Name is：" + name);
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    public ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }
//
//    public BeanFactory getBeanFactory() {
//        return beanFactory;
//    }

//    @Override
//    public void destroy() throws Exception {
//        System.out.println("执行：UserService.destroy");
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        System.out.println("执行：UserService.afterPropertiesSet");
//    }


}
