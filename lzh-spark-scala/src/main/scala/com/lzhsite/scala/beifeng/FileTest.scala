package com.lzhsite.scala.beifeng


import java.io._

import scala.io.Source
import scala.util.Try

/**
  * Created by lzhcode on 2018/1/28.
  */
object FileTest {
  //一.写入文件
  //
  //
  //1.FileWriter
  //专门用于处理文件的字符写入流对象
  // new FileWriter("/home/hadoop3/file/num1.txt",true)如果文件不存在，则会新建文件，
  // true是在文件末尾追加写入，默认为false,从文件开头重新写，如果之前有内容，则会删除原有内容。

  def fileTest1(): Unit = {
    println(Try {
      val out = new FileWriter("/home/hadoop3/file/num1.txt", true)
      for (i <- 0 to 15)
        out.write(i.toString)
      out.close()
    })
  }


  //  2.RandomAccessFile
  //  该对象特点：
  //  1，该对象只能操作文件，所以构造函数接收两种类型的参数。
  //  a.字符串路径。
  //  b.File对象。
  //  2，该对象既可以对文件进行读取，也可以写入。
  //  在进行对象实例化时，必须要指定的该对象的操作模式，r rw等。
  //  注意；该对象在实例化时，如果要操作的文件不存在，会自动建立。
  //  如果要操作的文件存在，则不会建立。
  //  如果存在的文件有数据，那么在没有指定指针位置的情况下，写入数据，会将文件开头的数据覆盖。
  def fileTest2(): Unit = {

    val randomFile = new RandomAccessFile("/home/hadoop3/file/num.txt", "rw")
    val fileLength = randomFile.length; //得到文件长度

    randomFile.seek(fileLength); //指针指向文件末尾
    for (i <- 'a' to 'g')
      randomFile.writeBytes(i.toString); //写入数据
    randomFile.close();
  }

  //  二.读文件
  //
  //  1.读取行
  def fileTest3(): Unit = {
    val source = Source.fromFile("myfile.txt", "UTF-8")
    // 第一个参数可以是字符串或java.io.File
    //如果你知道文件使用的是当前平台缺省的字符编码，则可以略去第二个字符编码参数
    val lineIterator = source.getLines
    //处理 L
    for (l <- lineIterator) {

    }

    //或者也可以对迭代器应用toArray或toBuffer方法，将这些行放到数组或数组缓冲中
    val lines = source.getLines.toArray
    val contents = source.mkString

    source.close() //记得调用close
  }

  //2.读取字符
  def fileTest4(): Unit = {

    val source = Source.fromFile("myfile.txt", "UTF-8")
    for (c <- source) {

    } //处理 c

    //想查看某个字符但又不处理它的话，可以调用source对象的buffered方法
    val iter = source.buffered
    while (iter.hasNext) {
      if (iter.head == "aaa") {
        // 处理 iter.next
      } else {

      }
    }
    source.close()
  }

  // 3.读取词法单元和数字
  def fileTest5(): Unit = {
    val source = Source.fromFile("myfile.txt", "UTF-8")
    val tokens = source.mkString.split("\\s+")

    val numbers = for (w <- tokens) yield w.toDouble
    //或者
    // val numbers = tokens.map(_.toDouble)
  }

  //4.读取二进制文件
  def fileTest5(filename: String): Unit = {
    val file = new File(filename)
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
  }

  //3.从URL或其他源读取
  def fileTest6(): Unit = {
    val source1 = Source.fromURL("http://horstamnn.com", "UTF-8")
    val source2 = Source.fromString("Hello, World!") //从给定的字符串读取——对调试很有用

    val source3 = Source.stdin //从标准输入读取
  }
}
