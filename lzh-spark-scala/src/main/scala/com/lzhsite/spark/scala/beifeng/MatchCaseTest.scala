package com.lzhsite.spark.scala.beifeng

/**
 * Created by ibf on 2016/12/25.
 */
object MatchCaseTest {

  def main(args: Array[String]) {
    judgeGrade("A","tom");
  }

  def judgeGrade(grade: String,name : String): Unit ={

    grade match {
      case "A" => println("Excellent")
      case "B" => println("Good")
      case "C" => println("just so so")
      case _grade if name == "tom" => println("very good")
      case _ => println("You need word harder")
    }
  }



}
