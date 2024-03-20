package com.zht.springframework;
import com.zht.springframework.beans.factory.annotation.Value;
import com.zht.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDao {
    @Value("${name}")
    public String name;
    @Value("${age}")
    public int age;

    public String queryUserName(String uId) {
        return "name is :"+" " + name+","+"age is :" + " " + age;
    }}