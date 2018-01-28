package com.lzhsite.scala.beifeng

/**
 * Created by ibf on 2016/12/25.
 */
class Person3( val name :String , val age : Int){

  println("Person Constructor enter ...")

  val school = "beifeng"

  println("Person Constructor leave ...")

  var gender : String = _

  def this(name:String, age : Int ,gender: String){
    this(name,age)
    this.gender = gender
  }
  def this(name: String){
    this(name,0,"male")
  }
}

object ConstructorExtendDemo extends App{

  val person3 = new Person3("zhangsan",30)

  println(person3)

  val p = new Person3("lisi",40)
}
