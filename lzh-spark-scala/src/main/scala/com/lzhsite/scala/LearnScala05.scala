package com.lzhsite.scala

/**
  * Created by lzhcode on 2018/1/28.
  */

//1.改进Counter类，让它不要在大于Int.maxValue时变为负数
class Counter{
  private var value = 0
  def increment():Unit = {
    if(value < Int.MaxValue){
      value = value + 1
    }
  }

  def current() = value
}

//2.写一个BankAccount类，加入deposit和withdraw方法，和一个只读的balance属性
class BankAccount{
  private var balance:Double = 0

  def getBalance():Double = {
    balance
  }

  def deposit(num:Double):Unit = {
    balance = balance + num
  }

  def withdraw(num:Double):Unit = {
    if(num < balance){
      balance = balance - num
    }else{
      throw new Exception("withdraw money over total money")
    }
  }
}

//3.定义一个Time类，有两个属性hours和minutes，hour介于0和23之间
//另外还有一个检测时间是否早于另一个时间的方法，before(other:Time):Boolean
class Time3(var phours:Int,var pminutes:Int){
  private var hours:Int = 0
  private var minutes:Int = 0

  if(phours >=0 && phours <=23){
    hours = phours
  }else{
    throw new Exception("construct Time3 error")
  }
  if(minutes >= 0 && minutes <= 59){
    minutes = pminutes
  }else{
    throw new Exception("construct Time3 error")
  }

  def getHours() = hours
  def getMinutes() = minutes

  def before(other:Time3):Boolean={
    if(hours < other.getHours() || (hours == other.getHours() && minutes < other.getMinutes())){
      true
    }else{
      false
    }
  }
}

//4.重新实现Timer类，内部呈现改成字午夜起的分钟数
class Time4(phours:Int,pminutes:Int) {
  var minutes:Int = 0

  if(!(phours >= 0 && phours <=23)){
    throw new Exception("construct error")
  }
  if(!(pminutes >= 0 && pminutes <=59)){
    throw new Exception("construct error")
  }

  minutes = phours*60 + pminutes

  def getHours():Int={
    minutes/60
  }

  def getMinutes():Int={
    minutes%60
  }

  def before(other:Time4):Boolean = {
    if(minutes < other.getHours*60+other.getMinutes){
      true
    }else{
      false
    }
  }
}

//5.创建一个Student类，加入可读写的JavaBeans属性name(类型为String)和id(类型为Long)。有哪些方法被生产？
// (用javap查看。)你可以在Scala中调用JavaBeans的getter和setter方法吗？应该这样做吗？
//
//import scala.beans.BeanProperty
////答案里导入的包 scala2.11.8没有这个类了，在API中查找包位置
//class Student {
//  @BeanProperty var name:String = _
//  @BeanProperty var id:Long = _
//}
//javap –c Student 查看 生成了name()\name_=\setName()\getName()   id()\id_=\setId()\getId()


//6.在5.2节的Persion类中加一个主构造器，将负年龄换为0
class Persion(page:Int){
  var age:Int = 0

  if(page < 0){
    age = 0
  }else{
    age = page
  }

  def getAge():Int = age
}
//7.编写一个Person类，其主构造器接受一个字符串，该字符串包含名字，空格和姓，如new Person("Fred Smith")。
// 提供只读属性firstName和lastName。主构造器参数应该是var,val还是普通参数？为什么？
//val,如果为var的话，对应的字符串有get和set方法，因为只提供只读属性firstName和lastName，不能重复赋值。

//8.创建一个Car类，以只读属性对应制造商，型号名称，型号年份以及一个可读写的属性用于车牌。提供四组构造器。
// 每个构造器fc都要求制造商和型号为必填。型号年份和车牌可选，如果未填，则型号年份为-1，车牌为空串。
// 你会选择哪一个作为你的主构造器？为什么？

//class Car(val maker:String,val typeName:String){
//
//  辅助构造器参数分别为 型号年份、车牌、型号年份和车牌，不知道对不。
//
//  9.考虑如下的类
//
//  class Employ(val name:String,var salary:Double){
//    def this(){this("John Q. Public",0.0)}
//  }
//
//  重写该类,使用显示的字段定义，和一个缺省主构造器。你更倾向于使用哪种形式？为什么？
//
//  class Employ{
//    val name:String = "John Q. Public"
//    var salary:Double = 0.0
//  }

object LearnScala05 {
  def main(args:Array[String]):Unit={
    println("==================execise1====================")
    var counter = new Counter
    for(i <- 1 to Int.MaxValue) counter.increment()
    println(counter.current())

    println("==================execise2====================")
    var bank = new BankAccount
    bank.deposit(200)
    bank.withdraw(100)
    println(bank.getBalance())

    println("==================execise3====================")
    var time31 = new Time3(11,23)
    var time32 = new Time3(12,11)
    var time33 = new Time3(6,56)
    println(time31.getHours()+":"+time31.getMinutes())
    println(time31.before(time32))
    println(time31.before(time33))

    println("==================execise4====================")
    var time41 = new Time4(11,23)
    var time42 = new Time4(12,11)
    var time43 = new Time4(6,56)
    println(time41.getHours()+":"+time41.getMinutes())
    println(time41.before(time42))
    println(time41.before(time43))

    println("==================execise6====================")
    var person = new Persion(-1)
    println(person.getAge())

    //其余均是问答题，不再在代码中阐述
  }
}
