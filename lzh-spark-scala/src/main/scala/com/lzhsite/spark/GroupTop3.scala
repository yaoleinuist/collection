package com.lzhsite.spark

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.control.Breaks


/**
  * Created by lzhcode on 2018/1/27.
  */
object GroupTop3 {


  def mapToPairFunc(key: String, scores: Iterable[Int]): (String, Iterable[Int]) = {

    var top3 = new Array[Int](3)
    val loop = new Breaks
    loop.breakable {
      var scoreIerator = scores.toIterator
      while (scoreIerator.hasNext) {
        var score = scoreIerator.next();

        for (i <- 0 until (3)) {
          if (top3(i) == null) {
            top3(i) = score;
            loop.break;
          } else if (score > top3(i)) {
            for (j <- i to 0 by -1) {
              top3(j) = top3(j - 1);
            }
            top3(i) = score;
            loop.break;
          }
        }
      }
    }
    (key, top3)
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("GroupTop3")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val lines = sc.textFile("file:///D:/大数据/spark/Spark深入剖析/第一章：Spark核心编程/资料/第40讲-Spark核心编程：高级编程之topn/score.txt", 1)


    val wordPairsRDD = lines.map(line => {
      var lineSplited = line.split(" ")

      (lineSplited(0), (lineSplited(1)).toInt)
    });

    val groupedPairs = wordPairsRDD.groupByKey()

    val top3Scores = groupedPairs.map(t => {
      val key = t._1
      val scores = t._2
      // 注意Int默认值是0，Integer默认值是null
      var top3 = new Array[Integer](3);
      val loop = new Breaks;

      var scoreIerator = scores.toIterator
      while (scoreIerator.hasNext) {
        var score = scoreIerator.next();
        //注意breakable块的位置
        loop.breakable {
          for (i <- 0 to 2) {
            if (top3(i) == null) {
              top3(i) = score;
              loop.break;
            } else if (score > top3(i)) {
              for (j <- 2 to 0 by -1 if j > i) {
                top3(j) = top3(j - 1);
              }
              top3(i) = score;
              loop.break;
            }
          }
        }
      }
      (key, top3)
    })

    top3Scores.foreach(top3Score => {
      println("key= " + top3Score._1 + ",value =" + top3Score._2.mkString(" and "))
    })


  }

}

/*
val a = sc.parallelize(1 to 9, 3)
def mapDoubleFunc(a : Int) : (Int,Int) = {
(a,a*2)
}
val mapResult = a.map(mapDoubleFunc)
println(mapResult.collect().mkString)
结果(1,2)(2,4)(3,6)(4,8)(5,10)(6,12)(7,14)(8,16)(9,18)


val a = sc.parallelize(1 to 9, 3)
def doubleFunc(iter: Iterator[Int]) : Iterator[(Int,Int)] = {
var res = List[(Int,Int)]()
while (iter.hasNext)
{
val cur = iter.next;
res .::= (cur,cur*2)
}
res.iterator
}
val result = a.mapPartitions(doubleFunc)
println(result.collect().mkString)
结果(3,6)(2,4)(1,2)(6,12)(5,10)(4,8)(9,18)(8,16)(7,14)
*/
