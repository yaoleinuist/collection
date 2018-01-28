package com.lzhsite.scala.beifeng

/**
 * Created by ibf on 2016/12/25.
 */
class Person2(val name : String, val age : Int){

  def this(name: String){
    this(name,0)
  }
  def this(age: Int){
    this("xx",age)
  }
  def sayHello = println("name = " + name + " , age = " + age)
}

object Person {

  def main(args: Array[String]) {

    val person2 = new Person2("zhangsan",20)
  }
}
