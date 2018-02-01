package com.lzhsite.scala

/**
  * Created by lzhcode on 2018/1/29.
  */
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable

/**
  * 快学scala第13章习题解答
  */
object LearnScala13 {
  //1.编写一个函数，给定字符串，产出一个包含所有字符的下标的映射
  //举例来说，indexs("Missingapi")应返回一个映射，让'M'对应{0},'i'对应集{1,4,7,10}以此类推
  //使用可变的集合映射
  def execise1(input:String):mutable.HashMap[String,ArrayBuffer[String]] = {
    val map = new scala.collection.mutable.HashMap[String,ArrayBuffer[String]]()
    var index = 0
    for(ch <- input){
      if(!map.contains(ch.toString)) map.put(ch.toString,new ArrayBuffer[String])
      var arr:ArrayBuffer[String] = map.get(ch.toString).get
      arr += index.toString  //向对应的key取得的ArrayBuffer里添加元素
      index += 1
    }
    map
  }

  //2.与第一题相同，这次用不可变映射集合
  def execise2(input:String):scala.collection.immutable.HashMap[String,ArrayBuffer[String]] = {
    var map = new scala.collection.immutable.HashMap[String,ArrayBuffer[String]]
    var index = 0
    for(ch <- input){
      if(!map.contains(ch.toString)){
        map = map + (ch.toString->new ArrayBuffer[String])
      }
      var arr:ArrayBuffer[String] = map.get(ch.toString).get
      arr += index.toString
      index += 1
    }
    map
  }

  //3.编写一个函数，从整型链表里去除所有零值
  def execise3(list:mutable.ArrayBuffer[Integer]):mutable.ArrayBuffer[Integer]={
    list.filter(_!=0)
  }

  //4.编写一个函数，接受一个字符串的集合和一个字符串到整型的映射，返回整型集合
  def execise4(arr:Array[String],map:Map[String,Integer]):Array[Integer] = {
    //arr过滤掉了map中不包含的key
    val filterArr:Array[String] = arr.filter(map.contains(_))
    val flatRtn = filterArr.flatMap(word=>{
      map.get(word)
    })
    flatRtn
  }

  //5.编写一个函数，作用于mkString相同，使用reduceLeft函数
  def execise5(joinStr:String,strArr:Array[String]):String = {
    strArr.reduceLeft(_+joinStr+_)
  }

  //8.编写一个函数，将Double数组转为二维数组
  def execise8(input:Array[Double],colnum:Integer):Array[Array[Double]] = {
    var rtn = new ArrayBuffer[Array[Double]]
    //多少行
    var count = input.length/colnum
    if(input.length % colnum != 0) count += 1
    for(i <- 0 to count){
      if(i != count - 1){
        rtn += input.slice(i*colnum,(i+1)*colnum)
      }else{
        rtn += input.slice(i*colnum,input.length)
      }
    }
    rtn.toArray
  }

  def main(args:Array[String]):Unit = {
    println("====================execise1=======================")
    println(LearnScala13.execise1("Mississippi"))

    println("====================execise2=======================")
    println(LearnScala13.execise2("Mississippi"))

    println("====================execise3=======================")
    var list3:mutable.ArrayBuffer[Integer] = new mutable.ArrayBuffer[Integer]
    list3 ++= ArrayBuffer(1,0,0,-1)
    println(LearnScala13.execise3(list3))

    println("====================execise4=========================")
    println(LearnScala13.execise4(Array("Tom","Fred","Harry"),Map("Tom"->3,"Dick"->4,"Harry"->5)))

    println("====================execise5=========================")
    println(LearnScala13.execise5(" or ",Array("1111","2222","3333")))

    println("====================execise8=========================")
    val arrs = LearnScala13.execise8(Array(1,2,3,4,5,6),3)
    for(elem <- arrs){
      println(elem.mkString(" "))
    }
  }
}

