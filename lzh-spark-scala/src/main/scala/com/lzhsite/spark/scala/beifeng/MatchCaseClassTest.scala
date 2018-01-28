package com.lzhsite.spark.scala

/**
 * Created by ibf on 2016/12/25.
 */
class Person5

case class Teacher(name: String, subject: String) extends Person5

case class Student2(name: String, classroom : String) extends Person5

class MatchCaseClassTest{
  def judgeIdentify(person: Person5): Unit ={
    person match {
      case Teacher(name,subject) => println(name + "," + subject)
      case Student2(name,classroom) => println(name + "," + classroom)
      case _ => println("=======================")
    }
  }
}


object MatchCaseClassTest {

  def main(args: Array[String]) {
    val p1 = Teacher("zhangsan","scala")
    val p2 = Student2("lisi","1#101")

    new MatchCaseClassTest().judgeIdentify(p2)
  }
}
