package com.lzhsite.scala.beifeng

import scala.util.control.Breaks

/**
 * Created by ibf on 2016/12/24.
 */
object BreakTest {

  def main(args: Array[String]) {

    val numList = List(1,2,3,4,5,6,7,8,9,10)

    val loop = new Breaks

    loop.breakable(for(a <- numList){
      println("Value of : " + a)

      if(a == 4){
        //break
        loop.break()
      }
    })
    println("After the loop")
  }
}
