package com.lzhsite.scala

/**
  * Created by lzhcode on 2018/1/28.
  */
import java.util

import scala.collection.mutable
import scala.io.Source
import scala.collection.immutable.SortedMap
import scala.collection.JavaConversions.mapAsScalaMap

/**
  * 快学scala 04习题
  */
object LearnScala04 {
  //1.设计1个映射，其中包含你需要的一些装备，以及他们的价格
  //然后构建另一组映射，采用同样的键，但在价格上打九折
  def execise1():Unit = {
    var map1 = Map[String,Double](
      "青龙偃月刀"->2.33,"雌雄双股剑"->1.11,"丈八蛇矛"->1.23
    )
    for((key,value)<-map1) print(key+":"+value+" ")
    println()

    var map2 = new mutable.HashMap[String,Double]()
    for((key,value)<-map1) map2(key) = value*0.9
    for((key,value)<-map2) print(key+":"+value+" ")
    println()
  }

  //2.编写一段程序，从文件中读取单词；用一个可变的映射清点单词出现的频率
  def execise2(filepath:String):Unit = {
    var countMap = new mutable.HashMap[String,Int]()
    Source.fromFile(filepath).getLines().foreach(
      line => {
        for(word <- line.split(" ")){
          if(!countMap.keySet.contains(word.toLowerCase)) countMap(word.toLowerCase) = 0
          countMap(word.toLowerCase) = countMap(word.toLowerCase) + 1
        }
      }
    )

    for((word,wordcount) <- countMap){
      printf("word:%s,count:%d\n",word,wordcount)
    }
  }

  //3.重复做前一个练习，这次用不可变映射
  def execise3(filepath:String):Unit = {
    var countMap = Map[String,Int]()
    Source.fromFile(filepath).getLines().foreach(
      line => {
        for(word <- line.split(" ")){
          if(!countMap.keySet.contains(word.toLowerCase)){
            var newCountMap:Map[String,Int] = countMap + (word.toLowerCase->1)
            countMap = newCountMap
          }else{
            val count:Int = countMap(word.toLowerCase) + 1
            var newCountMap:Map[String,Int] = countMap - word.toLowerCase + (word.toLowerCase->count)
            countMap = newCountMap
          }
        }
      }
    )

    for((word,wordcount) <- countMap){
      printf("word:%s,count:%d\n",word,wordcount)
    }
  }

  //4.重复前一个练习，这次用排序集合SortedMap，使得单词可以以排序的方式输出
  def execise4(filepath:String):Unit = {
    var countMap = SortedMap[String,Int]()
    Source.fromFile(filepath).getLines().foreach(
      line => {
        for(word <- line.split(" ")){
          if(!countMap.keySet.contains(word.toLowerCase)){
            var newCountMap:SortedMap[String,Int] = countMap + (word.toLowerCase->1)
            countMap = newCountMap
          }else{
            val count:Int = countMap(word.toLowerCase) + 1
            var newCountMap:SortedMap[String,Int] = countMap - word.toLowerCase + (word.toLowerCase->count)
            countMap = newCountMap
          }
        }
      }
    )

    for((word,wordcount) <- countMap){
      printf("word:%s,count:%d\n",word,wordcount)
    }
  }

  //5.重复前一个练习，这次用java.util.TreeMap，并使其适用于scala
  def execise5(filepath:String):Unit = {
    var countMap = new util.TreeMap[String,Int]()
    Source.fromFile(filepath).getLines().foreach(
      line => {
        for(word <- line.split(" ")) {
          if (!countMap.keySet().contains(word.toLowerCase)) countMap(word.toLowerCase) = 0
          countMap(word.toLowerCase) = countMap(word.toLowerCase) + 1
        }
      }
    )

    for((word,wordcount) <- countMap){
      printf("word:%s,count:%d\n",word,wordcount)
    }
  }

  //7.打印java系统属性表格
  def execise7():Unit = {
    var sysmap = System.getProperties
    var maxlen:Int = 0
    for((key,value) <- sysmap if String.valueOf(key).length > maxlen) maxlen = String.valueOf(key).length
    //" "*(maxlen-String.valueOf(key).length)保证左边占位符一致
    for((key,value)<-sysmap) println(key+" "*(maxlen-String.valueOf(key).length)+"|"+value)
  }

  //8.编写一个函数minmax(values:Array[Int]),返回数组中最大值和最小值的对偶
  def minmax(values:Array[Int]):Tuple2[Int,Int]={
    Tuple2[Int,Int](values.min,values.max)
  }

  //9.编写一个函数lteqgt(values:Array[Int],v:Int),返回数组中小于v，等于v和大于v的数量
  //要求一起返回
  def lteqgt(values:Array[Int],v:Int):Tuple3[Int,Int,Int]={
    var lcount = 0
    var ecount = 0
    var gcount = 0
    for(elem <- values){
      if(elem < v){
        lcount = lcount + 1
      }else if(elem == v){
        ecount = ecount + 1
      }else{
        gcount = gcount + 1
      }
    }
    Tuple3[Int,Int,Int](lcount,ecount,gcount)
  }

  def main(args:Array[String]):Unit={
    println("=================execise1==================")
    LearnScala04.execise1()

    var resource=LearnScala04.getClass.getResource("/").getPath()
    var path= resource+"myfile.txt"
    println("=================execise2==================")
    LearnScala04.execise2(path)

    println("=================execise3==================")
    LearnScala04.execise3(path)

    println("=================execise4==================")
    LearnScala04.execise4(path)

    println("=================execise5==================")
    LearnScala04.execise5(path)

    //第六题看不明白题意

    println("=================execise7==================")
    LearnScala04.execise7

    println("=================execise8==================")
    var tuple8 = LearnScala04.minmax(Array(3,-1,2,6,4))
    println(tuple8._1+","+tuple8._2)

    println("=================execise9==================")
    var tuple9 = LearnScala04.lteqgt(Array(1,2,2,3,3,3),2)
    println(tuple9._1+","+tuple9._2+","+tuple9._3)

    //10."Hello".zip("world")得到的是Vector(((H,w), (e,o), (l,r), (l,l), (o,d)))
    print("Hello".zip("world"))
  }
}

