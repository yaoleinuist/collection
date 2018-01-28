package com.lzhsite.spark.scala.beifeng

/**
 * Created by ibf on 2016/12/25.
 */
class People{

  var name : String = ""

  val age : Int = 10

  def eat(): String ={
    name + "eat ... "
  }

  def watchFootBall(teamName: String): String = {
    name + "is watching match of " + teamName
  }
}

object SimpleObjectDemo {

  def main(args: Array[String]) {

    val people = new People

    people.name = "beifeng"

    println("name is = " + people.name)
    println("name is = " + people.name + " , age = " + people.age)

    println(people.eat())
    println(people.watchFootBall("manlian"))
  }
}
