/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.lzhsite.technology.shardingjdbc.example2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lzhsite.dao.OrderMapper;
import com.lzhsite.entity.Order;

public final class Main {
    
    private static ApplicationContext applicationContext;
    
    public static void main(final String[] args) {
        startContainer();
//        getSum();
        select();
//        System.out.println("--------------");
//        selectAll();
//        insert();
//        select();
//        selectLike();
//        selectByUserId();
//        batchInsert();
    }

    private static void getSum() {
        System.out.println("===start===");
        Integer sum = applicationContext.getBean(OrderMapper.class).getSum();
        System.out.println(sum);
        System.out.println("===end===");
    }

    private static void insert() {
        OrderMapper orderRepository = applicationContext.getBean(OrderMapper.class);
//        for (int i = 1; i < 10; i++) {
//            Order order = new Order();
//            order.setOrderId(i);
//            order.setUserId(51);
//            order.setStatus("INSERT_TEST");
//            System.out.println("=====插入=====");
//            orderRepository.insert(order);
//        }

//        for (int i = 1000; i < 1010; i++) {
//            Order order = new Order();
//            order.setOrderId(i);
//            order.setUserId(51);
//            order.setStatus("INSERT_TEST");
//            System.out.println("=====插入=====");
//            orderRepository.insert(order);
//        }

        for (int i = 100; i < 101; i++) {
            Order order = new Order();
            order.setOrderId(i);
            order.setUserId(51);
            order.setStatus("INSERT_TEST");
            System.out.println("=====插入=====");
            orderRepository.insert(order);
        }


    }

    private static void startContainer() {
        applicationContext = new ClassPathXmlApplicationContext("classpath:spring/spring-shardingContext.xml");
    }

    private static void selectByUserId() {
        System.out.println("===start===");
        List<Order> model = applicationContext.getBean(OrderMapper.class).selectByUserId(10);
        System.out.println(model.size());
        System.out.println("===end===");
    }

    private static void selectLike() {
        System.out.println("===start===");
        List<Order> model = applicationContext.getBean(OrderMapper.class).selectLike("INSERT");
        System.out.println(model.size());
        System.out.println("===end===");
    }

    private static void select() {
        Order criteria = new Order();
        criteria.setUserId(10);
        criteria.setOrderId(1005);
        Order model = applicationContext.getBean(OrderMapper.class).selectById(criteria);
        System.out.println(model);
    }
    
    private static void selectAll() {
        System.out.println(applicationContext.getBean(OrderMapper.class).selectAll());
    }

    private static void batchInsert() {
        List<Order> orderList = new ArrayList<>();
        for (int i = 995; i < 1005; i++) {
            Order order = new Order();
            order.setOrderId(i);
            order.setUserId(51);
            orderList.add(order);
        }
        applicationContext.getBean(OrderMapper.class).batchInsert(orderList);
    }

}
