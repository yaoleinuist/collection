package com.lzhsite.technology.concurrent.atomic;

import java.util.concurrent.atomic.AtomicReference;

import com.lzhsite.dto.PersonDTO;

//AtomicReference的源码比较简单。它是通过"volatile"和"Unsafe提供的CAS函数实现"原子操作。
//(01) value是volatile类型。这保证了：当某线程修改value的值时，其他线程看到的value值都是最新的value值，即修改之后的volatile的值。
//(02) 通过CAS设置value。这保证了：当某线程池通过CAS函数(如compareAndSet函数)设置value时，它的操作是原子的，即线程在操作value时不会被中断

public class TestAtomicReference {
	public static void main(String[] args) {
        PersonDTO people1 =new PersonDTO("Bom", 0);
        PersonDTO people2 =new PersonDTO("Tom",10);

        //先初始化一个值，如果不初始化则默认值为null
        AtomicReference<PersonDTO> reference = new AtomicReference<>(people1);
        PersonDTO people3 = reference.get(); //获取的p3就是p1
        if (people3.equals(people1)) {
            System.out.println("people3:" + people3);
        } else {
            System.out.println("else:" + people3);
        }

        /**
         * compareAndSet的两个参数含义
         * 当前值：拿当前值和reference.get()获取到的值去比较，如果相等则true并更新值为期望值
         * 期望值：如果返回true则更新为期望值，如果返回false则不更新值
         */
        boolean b = reference.compareAndSet(null, people2); //返回false则不更新值
        System.out.println("myClass.main-"+b+"--"+reference.get()); //得到p1

        boolean b1 = reference.compareAndSet(people1, people2); //返回true更新为p2
        System.out.println("myClass.main-"+b1+"--"+reference.get()); //得到p2


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread1-----------");

                PersonDTO people = reference.get();
                people.setName("Tom1");
                people.setAge(people.getAge()+1);
                reference.getAndSet(people);
                System.out.println("Thread1:"+reference.get().toString());
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread2-----------");

                PersonDTO people = reference.get();
                people.setName("Tom2");
                people.setAge(people.getAge()+1);
                reference.getAndSet(people);
                System.out.println("Thread2:"+reference.get().toString());
            }
        }).start();

    }
 
}
//people3:People{name='Bom', age=0}
//myClass.main-false--People{name='Bom', age=0}
//myClass.main-true--People{name='Tom', age=10}
//Thread2-----------
//Thread2:People{name='Tom2', age=11}
//Thread1-----------
//Thread1:People{name='Tom1', age=12}
 

 
 