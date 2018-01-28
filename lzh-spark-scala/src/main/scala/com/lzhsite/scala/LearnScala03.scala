package com.lzhsite.scala

import java.awt.datatransfer._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by lzhcode on 2018/1/28.
  */
object LearnScala03 {



  //1.编写一段代码，将a设置为一个n个随机整数的数组，要求随机数介于0（包含）和n（不包含）之间。
  def execise1(): Unit = {
    val n = 100 //n是自己给定的
    val a = scala.util.Random
    val b = new Array[Int](n) // new Array 和 Array 是有区别的
    for (i <- 0 until b.length) {
      b(i) = a.nextInt(n)
    }

    println(b.mkString(" and "))
  }

  //  2.编写一个循环，将整数数组中相邻的元素置换。
  //  例如，Array(1,2,3,4,5)置换后变为Array(2,1,4,3,5)
  def execise2(): Unit = {
    var a = Array(1, 2, 3, 4, 5)
    for (i <- 0 until(a.length, 2) if i < a.length - 1) {
      val tmp = a(i)
      a(i) = a(i + 1)
      a(i + 1) = tmp
    }

    println(a.toBuffer.mkString(" and "))
  }

  // 3.重复前一个练习，不过这一次生成一个新的值交换过的数组。用for/yield。
  def execise3(): Unit = {
    val a = Array(1, 2, 3, 4, 5)
    val b = (for (i <- 0 until(a.length, 2))
      yield
        if (i < a.length - 1) Array(a(i + 1), a(i))
        else Array(a(i))).flatten
    println(b.mkString(" and "))
  }

  // 4.给定一个整数数组，产出一个新的数组，包含元素组中的所有正值，以原有顺序排列，之后的元素是所有零或负值，以原有顺序排序。
  def execise4(): Unit = {
    val a = Array(1, -1, -3, 0, 4, -2, 4, 87, 0, -10, 9)
    val b = ArrayBuffer[Int]()
    val c = ArrayBuffer[Int]()
    a.foreach(arg => if (arg > 0) b += arg else c += arg)
    b ++= c
    println(b.mkString(" and "))
  }

  //5.如何计算Array[Double]的平均值
  def execise5(): BigDecimal = {
    val h =  ArrayBuffer[BigDecimal](0.1, 46.3, 24.0, 234.2, -4.2)

    val hMean:BigDecimal = h.sum / h.length
    hMean
  }

  // 6.如何重新组织Array[Int]的元素将它们以反序排列？对于ArrayBuffer[Int]你又会怎么做呢？
  def execise6(): Unit = {
    //以上一题的Array[Double]为例
    val h = Array(0.1, 46.3, 24.0, 234.2, -4.2)
    //  val hReverse = h.reverse
    for (i <- 0 until h.length / 2) {
      val tmp = h(i)
      h(i) = h(h.length - 1 - i)
      h(h.length - 1 - i) = tmp
    }
    println(h.mkString(" "))


  }

  //7.编写代码，去掉数组中的所有值，去掉重复项。(查看Scaladoc)
  def execise7(): Unit = {
    val g = Array(1, -1, -3, 0, 4, -2, 4, 87, 0, -10, 9)
    println(g.distinct.mkString(" "))
  }

  //8.重新编写3.4节结尾的示例。收集负值元素的下标，反序，去掉最后一个下标，然后对每个下标调用a.remove(i).比较这样做的效率和3.4节中另外两种方法的效率。
  // 略

  // 9.创建一个由java.util.TimeZone.getAvailableIDs返回的时区集合，判断条件是它们在美洲。去掉”America/”前缀并排序。
  def execise9(): Unit = {
    val timeZone = java.util.TimeZone.getAvailableIDs
    val americaTimeZone = timeZone.filter(_.take(8) == "America/")
    val sortedAmericaTimeZone = americaTimeZone.map(_.drop(8)).sorted
    for (i <- 0 until sortedAmericaTimeZone.length) {
      println(sortedAmericaTimeZone(i))
    }
  }

  //10.引入java.awt.datatransfer._并构建一个类型为SystemFlavorMap类型的对象
  //val flavors = SystemFlavorMap.getDefaultFlavorMap().asInstanceOf[SystemFlavorMap]
  //然后以DataFlavor.imageFlavor为参数调用getNativesForFlavor方法，以Scala缓冲保存返回值。
  // （为什么用这样一个晦涩难懂的类？因为在Java标准中很难找得到使用java.util.List的代码。）


  def execise10(): Unit = {
    val flavors = SystemFlavorMap.getDefaultFlavorMap().asInstanceOf[SystemFlavorMap]
    val xx = flavors.getNativesForFlavor(DataFlavor.imageFlavor).toArray.toBuffer
    println(xx.mkString("  "))
  }

  def main(args: Array[String]): Unit = {
    println("================execise1==============")
    println(LearnScala03.execise1())


    println("================execise2==============")
    LearnScala03.execise2()

    println("================execise3==============")
    LearnScala03.execise3()

    println("================execise4==============")
    LearnScala03.execise4()

    println("================execise5==============")
    println(LearnScala03.execise5())

    println("================execise6==============")
    println(LearnScala03.execise4())

    println("================execise7==============")
    LearnScala03.execise7()

    println("================execise9==============")
    LearnScala03.execise9()

    println("================execise10==============")
    LearnScala03.execise10()
  }
}
