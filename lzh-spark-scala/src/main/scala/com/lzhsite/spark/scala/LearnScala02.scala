package com.lzhsite.spark.scala

/**
  * Created by lzhcode on 2018/1/28.
  */
object LearnScala02 {

  //1.如果一个数字为正数，则它的signum为1；如果是负数，则signum为-1；如果是0，则signum是0
  //编写一个函数来计算这个值
  def execise1(num:Int):Int = {
    if (num > 0) 1 else if(num == 0) 0 else -1
  }

  //2.一个空的块表达式的值是什么？它的类型是什么
  //值是(),类型是Unit

  //3.指出Scala何种情况下赋值语句x=y=1是合法的
  //在x的类型为Unit的情况下是合法的

  //4.针对下列java循环编写一个scala版
  //for(int i=10;i>=0;i--)System.out.println(i)
  def execise4():Unit={
   var i = 10
    while(i >= 0){
      println(i)
      i = i -1
    }
  }

  //5.编写一个过程countdown(n:Int)，打印从n到0的数字
  def countdown(n:Int):Unit={
    var i = n
    while(i >= 0){
      println(i)
      i = i -1
    }
  }

  //6.编写一个for循环，计算字符串中所有字母的unicode代码的乘积
  //举例来说，"Hello"中所有字母的字符成为9415087488L
  def execise6(input:String):BigInt = {
    var sum:BigInt = 1
    val length = input.length
    for(i <- 0 to length-1){
      sum = sum * input(i).toLong
    }
    sum
  }

  //7.同样是解决6的问题，但不能够用循环（提示，StringOps）
  def execise7(input:String):BigInt = {
    var sum:BigInt = 0
    sum = input.map(_.toLong).product
    sum
  }

  //第8题感觉和6,7重复了，故不做

  //9.把前一个练习的函数改为递归函数
  def execise9(input:String,curIndex:Int):BigInt = {
    if (curIndex == input.length - 1){
      input(curIndex).toLong
    } else{
      execise9(input,curIndex+1)*input(curIndex).toLong
    }
  }

  //第10题，题目太长，主要是根据定义编写函数
  def execise10(x:Double,n:Int): BigDecimal ={
    if(n == 0){
      1
    }else if (n < 0){
      1/execise10(x,-1*n)
    }else if (n % 2 == 1){
      x*execise10(x,n-1)
    }else{
      execise10(x,n/2)*execise10(x,n/2)
    }
  }

  def main(args:Array[String]):Unit = {
    println("================execise1==============")
    println(LearnScala02.execise1(333))
    println(LearnScala02.execise1(-23))
    println(LearnScala02.execise1(0))

    println("================execise4==============")
    LearnScala02.execise4()

    println("================execise5==============")
    LearnScala02.countdown(3)

    println("================execise6==============")
    println(LearnScala02.execise6("Hello"))

    println("================execise7==============")
    println(LearnScala02.execise7("Hello"))

    println("================execise9==============")
    println(LearnScala02.execise9("Hello",0))

    println("================execise10==============")
    println(LearnScala02.execise10(2,-4))
  }
}
