package com.lzhsite.scala.beifeng

/**
 * Created by ibf on 2016/12/25.
 */
/**
class Student(name : String, age : Int){

}

object Student{
  def apply(name : String, age : Int) = new Student(name, age)
}
  */

case class Student(name : String , age : Int)

object CaseClassDemo {

  def main(args: Array[String]) {
    val stu = Student("zhangsan",30)
  }
}
