package com.lzhsite.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

/**
  * @author Administrator
  */
object TransformationOperation {

  def main(args: Array[String]) {
    // map()  
    // filter()  
    // flatMap()  
    // groupByKey() 
    // reduceByKey()  
    // sortByKey() 
    join()
  }

  def map() {
    val conf = new SparkConf()
      .setAppName("map")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val numbers = Array(1, 2, 3, 4, 5)
    val numberRDD = sc.parallelize(numbers, 1)
    val multipleNumberRDD = numberRDD.map { num => num * 2 }

    multipleNumberRDD.foreach { num => println(num) }
  }

  def filter() {
    val conf = new SparkConf()
      .setAppName("filter")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val numbers = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val numberRDD = sc.parallelize(numbers, 1)
    val evenNumberRDD = numberRDD.filter { num => num % 2 == 0 }

    evenNumberRDD.foreach { num => println(num) }
  }

  def flatMap() {
    val conf = new SparkConf()
      .setAppName("flatMap")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val lineArray = Array("hello you", "hello me", "hello world")
    val lines = sc.parallelize(lineArray, 1)
    val words = lines.flatMap { line => line.split(" ") }

    words.foreach { word => println(word) }
  }

  def groupByKey() {
    val conf = new SparkConf()
      .setAppName("groupByKey")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val scoreList = Array(Tuple2("class1", 80), Tuple2("class2", 75),
      Tuple2("class1", 90), Tuple2("class2", 60))
    val scores = sc.parallelize(scoreList, 1)
    val groupedScores = scores.groupByKey()

    groupedScores.foreach(score => {
      println(score._1);
      score._2.foreach { singleScore => println(singleScore) };
      println("=============================")
    })
  }

  def reduceByKey() {
    val conf = new SparkConf()
      .setAppName("groupByKey")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val scoreList = Array(Tuple2("class1", 80), Tuple2("class2", 75),
      Tuple2("class1", 90), Tuple2("class2", 60))
    val scores = sc.parallelize(scoreList, 1)
    val totalScores = scores.reduceByKey(_ + _)

    totalScores.foreach(classScore => println(classScore._1 + ": " + classScore._2))
  }

  def sortByKey() {
    val conf = new SparkConf()
      .setAppName("sortByKey")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val scoreList = Array(Tuple2(65, "leo"), Tuple2(50, "tom"),
      Tuple2(100, "marry"), Tuple2(85, "jack"))
    val scores = sc.parallelize(scoreList, 1)
    val sortedScores = scores.sortByKey(false)

    sortedScores.foreach(studentScore => println(studentScore._1 + ": " + studentScore._2))
  }

  def join() {
    val conf = new SparkConf()
      .setAppName("join")
      .setMaster("local")
    val sc = new SparkContext(conf)

    val studentList = Array(
      Tuple2(1, "leo"),
      Tuple2(2, "jack"),
      Tuple2(3, "tom"));

    val scoreList = Array(
      Tuple2(1, 100),
      Tuple2(2, 90),
      Tuple2(3, 60));

    val students = sc.parallelize(studentList);
    val scores = sc.parallelize(scoreList);

    val studentScores = students.join(scores)

    studentScores.foreach(studentScore => {
      println("student id: " + studentScore._1);
      println("student name: " + studentScore._2._1)
      println("student socre: " + studentScore._2._2)
      println("=======================================")
    })
  }

  def join2() {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val sqlContext = new SQLContext(sc)

    /**
      * id      name
      * 1       zhangsan
      * 2       lisi
      * 3       wangwu
      */
    val idName = sc.parallelize(Array((1, "zhangsan"), (2, "lisi"), (3, "wangwu")))

    /**
      * id      age
      * 1       30
      * 2       29
      * 4       21
      */
    val idAge = sc.parallelize(Array((1, 30), (2, 29), (4, 21)))

    /** *******************************RDD **********************************/

    println("*********************************RDD**********************************")

    println("\n内关联（inner join）\n")
    // 内关联（inner join）
    //  只保留两边id相等的部分
    /**
      * (1,(zhangsan,30))
      * (2,(lisi,29))
      */
    idName.join(idAge).collect().foreach(println)

    println("\n左外关联（left out join）\n")
    // 左外关联（left out join）
    // 以左边的数据为标准, 左边的数据一律保留
    // 右边分三情况:
    //      一: 左边的id, 右边有, 则合并数据; (1,(zhangsan,Some(30)))
    //      二: 左边的id, 右边没有, 则右边为空; (3,(wangwu,None))
    //      三: 右边的id, 左边没有, 则不保留; 右边有id为4的行, 但结果中并未保留
    /**
      * (1,(zhangsan,Some(30)))
      * (2,(lisi,Some(29)))
      * (3,(wangwu,None))
      */
    idName.leftOuterJoin(idAge).collect().foreach(println)

    println("\n右外关联（right outer join）\n")
    // 右外关联（right outer join）
    // 以右边的数据为标准, 右边的数据一律保留
    // 左边分三种情况:
    //      一: 右边的id, 左边有, 则合并数据; (1,(Some(zhangsan),30))
    //      二: 右边的id, 左边没有, 则左边为空; (4,(None,21))
    //      三: 左边的id, 右边没有, 则不保留; 左边有id为3的行, 但结果中并为保留
    /**
      * (1,(Some(zhangsan),30))
      * (2,(Some(lisi),29))
      * (4,(None,21))
      */
    idName.rightOuterJoin(idAge).collect().foreach(println)

    println("\n全外关联（full outer join）\n")
    // 全外关联（full outer join）
    /**
      *
      * (1,(Some(zhangsan),Some(30)))
      * (2,(Some(lisi),Some(29)))
      * (3,(Some(wangwu),None))
      * (4,(None,Some(21)))
      */
    idName.fullOuterJoin(idAge).collect().foreach(println)

    /** *******************************DataFrame **********************************/
    val schema1 = StructType(Array(StructField("id", DataTypes.IntegerType, nullable = true), StructField("name", DataTypes.StringType, nullable = true)))
    val idNameDF = sqlContext.createDataFrame(idName.map(t => Row(t._1, t._2)), schema1)

    val schema2 = StructType(Array(StructField("id", DataTypes.IntegerType, nullable = true), StructField("age", DataTypes.IntegerType, nullable = true)))
    val idAgeDF = sqlContext.createDataFrame(idAge.map(t => Row(t._1, t._2)), schema2)
    println("*********************************DataFrame**********************************")

    println("\n内关联（inner join）\n")
    // 相当于调用, idNameDF.join(idAgeDF, Seq("id"), "inner").collect().foreach(println)
    // 这里只是调用了封装的API
    idNameDF.join(idAgeDF, "id").collect().foreach(println)

    println("\n左外关联（left out join）\n")
    idNameDF.join(idAgeDF, Seq("id"), "left_outer").collect().foreach(println)

    println("\n右外关联（right outer join）\n")
    idNameDF.join(idAgeDF, Seq("id"), "right_outer").collect().foreach(println)

    println("\n全外关联（full outer join）\n")
    idNameDF.join(idAgeDF, Seq("id"), "outer").collect().foreach(println)

    println("\nleft semi join\n")
    // left semi join
    // 左边的id, 在右边有, 就保留左边的数据; 右边的数据不保留, 只有id的有意义的
    /**
      * [1,zhangsan]
      * [2,lisi]
      */
    idNameDF.join(idAgeDF, Seq("id"), "leftsemi").collect().foreach(println)
  }
  def cogroup() {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val sqlContext = new SQLContext(sc)

    /**
      * id      name
      * 1       zhangsan
      * 2       lisi
      * 3       wangwu
      */
    val idName = sc.parallelize(Array((1, "zhangsan"), (2, "lisi"), (3, "wangwu")))

    /**
      * id      age
      * 1       30
      * 2       29
      * 4       21
      */
    val idAge = sc.parallelize(Array((1, 30), (2, 29), (4, 21)))

    println("\ncogroup\n")

    /**
      * (1,(CompactBuffer(zhangsan),CompactBuffer(30)))
      * (2,(CompactBuffer(lisi),CompactBuffer(29)))
      * (3,(CompactBuffer(wangwu),CompactBuffer()))
      * (4,(CompactBuffer(),CompactBuffer(21)))
      */
    idName.cogroup(idAge).collect().foreach(println)

    println("\njoin\n")
    // fullOuterJoin于cogroup的结果类似, 只是数据结构不一样
    /**
      * (1,(Some(zhangsan),Some(30)))
      * (2,(Some(lisi),Some(29)))
      * (3,(Some(wangwu),None))
      * (4,(None,Some(21)))
      */
    idName.fullOuterJoin(idAge).collect().foreach(println)

    /**
      * id      score
      * 1       100
      * 2       90
      * 2       95
      */
    val idScore = sc.parallelize(Array((1, 100), (2, 90), (2, 95)))

    println("\ncogroup, 出现相同id时\n")

    /**
      * (1,(CompactBuffer(zhangsan),CompactBuffer(100)))
      * (2,(CompactBuffer(lisi),CompactBuffer(90, 95)))
      * (3,(CompactBuffer(wangwu),CompactBuffer()))
      */
    idName.cogroup(idScore).collect().foreach(println)

    println("\njoin, 出现相同id时\n")

    /**
      * (1,(Some(zhangsan),Some(100)))
      * (2,(Some(lisi),Some(90)))
      * (2,(Some(lisi),Some(95)))
      * (3,(Some(wangwu),None))
      */
    idName.fullOuterJoin(idScore).collect().foreach(println)
  }

}