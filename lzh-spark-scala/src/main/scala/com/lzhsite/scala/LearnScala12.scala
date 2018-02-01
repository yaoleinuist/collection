package com.lzhsite.scala

/**
  * Created by lzhcode on 2018/1/30.
  */


//12.1  编写函数values(fun:(Int)=>Int,low:Int,high:Int),该函数输出一个集合，
// 对应给定区间内给定函数的输入 和输出。比如，values(x=>x*x,-5,5)
// 应该产出一个对偶的集合(-5,25),(-4,16),(-3,9),…,(5,25)
object Test extends App {


  def values(fun: (Int) => Int, low: Int, high: Int) = {
    var arr = List[(Int, Int)]()
    low to high foreach {
      num =>
        //意为构造，向数组的头部追加数据，创造新的列表
        arr = (num, fun(num)) :: arr
    }
    arr
  }

  println(values(x => x * x, -5, 5).mkString)

}

//12.2  如何用reduceLeft得到数组中的最大元素?
object Test2 extends App {
  val arr = Array(3, 2, 6, 8, 4, 6, 9, 3, 6, 7, 1, 2)
  print(arr.reduceLeft((a, b) => if (a > b) a else b))
}


//12.5  编写函数largest(fun:(Int)=>Int,inputs:Seq[Int]),输出在给定输入序列中给定函数的最大值。
// 举例来说，largest(x=>10*x-x*x,1 to 10)应该返回25.不得使用循环或递归
object Test5 extends App {

  def largest(fun: (Int) => Int, inputs: Seq[Int]) = {
    val s = inputs.reduceLeft((a, b) => if (fun(a) > fun(b)) a else b)
    fun(s)
  }

  println(largest(x => 10 * x - x * x, 1 to 10))
}

//12.6  修改前一个函数，返回最大的输出对应的输入。举例来说,largestAt(fun:(Int)=>Int,inputs:Seq[Int])应该返回5。
// 不得使用循环或递归
object Test6 extends App {

  def largest(fun: (Int) => Int, inputs: Seq[Int]) = {
    inputs.reduceLeft((a, b) => if (fun(a) > fun(b)) a else b)
  }

  println(largest(x => 10 * x - x * x, 1 to 10))
}

//12.7  要得到一个序列的对偶很容易，比如:
//val pairs = (1 to 10) zip (11 to 20)
//假定你想要对这个序列做某中操作—比如，给对偶中的值求和，但是你不能直接使用:
//pairs.map(_ + _)
//函数_ + _ 接受两个Int作为参数，而不是(Int,Int)对偶。编写函数adjustToPair,该函数接受一个类型为(Int,Int)=>Int的 函数作为参数，并返回一个等效的, 可以以对偶作为参数的函数。举例来说就是:adjustToPair(_ * _)((6,7))应得到42。然后用这个函数通过map计算出各个对偶的元素之和
object Test7 extends App {

  var list = List[Int]()

  def adjustToPair(fun: (Int, Int) => Int)(tup: (Int, Int)) = {
    list = fun(tup._1, tup._2) :: list
    this
  }

  def map(fun: (Int, Int) => Int): Int = {
    list.reduceLeft(fun)
  }

  val pairs = (1 to 10) zip (11 to 20)
  for ((a, b) <- pairs) {
    adjustToPair(_ * _)((a, b))
  }
  println(map(_ + _))
}

//12.8  在12.8节中，你看到了用于两组字符串数组的corresponds方法。做出一个对该方法的调用，
// 让它帮我们判断某个字符串数组里的所有元素的长度是否和某个给定的整数数组相对应
object Test8 extends App {

  val a = Array("asd", "df", "aadc")
  val b = Array(3, 2, 4)
  val c = Array(3, 2, 1)

  println(a.corresponds(b)(_.length == _))
  println(a.corresponds(c)(_.length == _))
}

//12.10  实现一个unless控制抽象，工作机制类似if,但条件是反过来的。第一个参数需要是换名调用的参数吗？你需要柯里化吗？
object Test10 extends App {

  def unless(condition: => Boolean)(block: => Unit) {
    if (!condition) {
      block
    }
  }


  var x = 10
  unless(x == 0) {
    x -= 1
    println(x)
  }
}

object LearnScala12 {
  def main(args: Array[String]): Unit = {
    //12.3  用to和reduceLeft实现阶乘函数,不得使用循环或递归
    println(1 to 10 reduceLeft (_ * _))
    //12.4  前一个实现需要处理一个特殊情况，即n<1的情况。展示如何用foldLeft来避免这个需要。(
    // (1 to -10)怎么看不懂
    println((1 to -10).foldLeft(1)(_ * _))
  }

}
