package com.lzhsite.scala.beifeng

/**
 * Created by ibf on 2016/12/24.
 */
object FunctionTest {

  def main(args: Array[String]) {
    //调用：
    val max_value = max(20,50)
    println("max is "+max_value)
  }

  def max(x : Int , y : Int): Int = {
    if(x > y)
      x
    else
      y
  }

  //无参无返回值定义函数：
  def add()={
    println("say hello...")
  }

  //由于没有参数那么括号可以不加
  //add()或add

  //嵌套函数（内部函数）
  def fun1(a: Int){
    def fun2(b: Int){
      println("fun1...fun2..." + (a + b))
    }
    fun2(100)
  }


  //匿名函数：没有函数名称
  (x: Int) => x + 1
  //将函数作为参数进行传递，这种定义的方式我们叫做：高阶函数
  def add2 = (x: Int,y: Int) => x + y
  def min = (x: Int , y: Int) => {
    if(x < y)
      x
    else
      y
  }
}
