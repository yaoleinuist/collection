package com.lzhsite.scala

import scala.math.sqrt


/**
  * Created by lzhcode on 2018/1/29.
  */
object LearnScala14 {
 
//  14.2 利用模式匹配，编写一个swap函数，接受一个整数的对偶，返回对偶的两个组成部件互换位置的新对偶
   
  def swap[S,T](tup: (S,T)) = {
    tup match {
      case (a ,b) => (b,a)
    }
  }


  //  14.3 利用模式匹配，编写一个swap函数，交换数组中的前两个元素的位置，前提条件是数组长度至少为2

  def swap(arr: Array[String]) = {
    arr match {
      // Array(b,a) ++ ar合并成新数组
      case Array(a,b, ar @ _*) => Array(b,a) ++ ar
      case _ => arr
    }
  }


  //14.4 添加一个样例类Multiple，作为Item的子类。
  // 举例来说，Multiple(10,Article("Blackwell Toster",29.95))描述的是10个烤面包机。
  // 当然了，你应该可以在第二个参数的位置接受任何Item，无论是Bundle还是另一个Multiple。
  // 扩展price函数以应对新的样例。

  abstract class Item

  case class Multiple(num : Int,item : Item) extends Item

  case class Article(description : String , price : Double) extends Item
  case class Bundle(description : String , discount : Double , item : Item*) extends Item

  object Test1 extends App{

    def price(it : Item) : Double = it match {
      case Article(_,p) => p
      //调用所有item类price方法返回值的的总和,_表示当前的item
      case Bundle(_,disc,its @ _*) => its.map(price _).sum - disc
      case Multiple(n,it) => n * price(it)
    }

  }

  //  14.5 我们可以用列表制作只在叶子节点存放值的树。举例来说，列表((3 8) 2 (5))描述的是如下这样一棵树:
  //    *
  //  / | \
  //  *  2  *
  //  /  \    |
  //  3   8    5
  //  不过，有些列表元素是数字，而另一些是列表。在Scala中，你不能拥有异构的列表，因此你必须使用List[Any]。
  //  编写一个leafSum函数，计算所有叶子节点中的元素之和，用模式匹配来区分数字和列表。


  def leafSum_5(list: List[Any]): Int = {

    var total = 0

    list.foreach {
      lst =>
        lst match {
          //l是一个数组
          case l: List[Any] => total += leafSum_5(l)
          //i是int
          case i: Int => total += i
        }
    }
    total
  }


  //14.6 制作这样的树更好的做法是使用样例类。我们不妨从二叉树开始。

  //编写一个函数计算所有叶子节点中的元素之和。
  sealed abstract class BinaryTree
  case class Leaf(value : Int) extends BinaryTree
  case class Node6(left : BinaryTree,right : BinaryTree) extends BinaryTree
  case class Node7(tr: BinaryTree*) extends BinaryTree
  case class Node8(ch : Char , tr: BinaryTree*) extends BinaryTree

  def leafSum_6(tree: BinaryTree): Int = {
    tree match {
      case Node6(a,b) => leafSum_6(a) + leafSum_6(b)
      case Leaf(v) => v
    }
  }

  //14.7 扩展前一个练习中的树，使得每个节点可以有任意多的后代，并重新实现leafSum函数。
  // 第五题中的树应该能够通过下述代码表示：

  def leafSum_7(tree: BinaryTree): Int = {
    tree match {
      //看不懂怎么可以吧函数名做参数传进来
      case Node7(r @ _*) => r.map(leafSum_7).sum
      case Leaf(v) => v
    }
  }


  //  14.8 扩展前一个练习中的树，使得每个非叶子节点除了后代之外，能够存放一个操作符。然后编写一个eval函数来计算它的值。
  // 举例来说：
  //          +
  //        / | \
  //      *  2  -
  //    /  \    |
  //  3     8   5
  //  上面这棵树的值为(3 * 8) + 2 + (-5) = 21
  //Node8('+' , Node8('*',Leaf(3), Leaf(8)), Leaf(2), Node8('-' , Leaf(5)))
  //怎么知道要这样构造

  def eval(tree: BinaryTree): Int = {
      tree match {
        case Node8(c : Char , r @ _*) =>
          if( c == '+') r.map(eval).sum
          else if (c == '*') r.map(eval).reduceLeft(_ * _)
          else r.map(eval).foldLeft(0)(_ - _)
        case Leaf(v) => v
      }
    }



  //14.9 编写一个函数，计算List[Option[Int]]中所有非None值之和。不得使用match语句。
  def execise9(): Unit = {
    val l : List[Option[Int]] = List(Option(-1),None,Option(2))
    println(l.map(_.getOrElse(0)).sum)
  }


  //14.10 编写一个函数，将两个类型为Double=>Option[Double]的函数组合在一起，产生另一个同样类型的函数。
  //  如果其中一个函数返回None，则组合函数也应返回None。例如：
  //  def f(x : Double) = if ( x >= 0) Some(sqrt(x)) else None
  //  def g(x : Double) = if ( x != 1) Some( 1 / ( x - 1)) else None
  //  val h = compose(f,g)
  //h(2)将得到Some(1)，而h(1)和h(0)将得到None
  //看不懂

  def execise10(): Unit = {
    def f(x : Double) = if ( x >= 0) Some(sqrt(x)) else None
    def g(x : Double) = if ( x != 1) Some( 1 / ( x - 1)) else None
    def compose(f : (Double => Option[Double]), g : (Double => Option[Double])):(Double => Option[Double])={
      (x : Double) =>
        if (f(x) == None || g(x) == None) None
        else g(x)
    }
    val h = compose(f,g)
    println(h(2))
  }


  def main(args:Array[String]):Unit = {
    println("====================execise1=======================")
    println(swap[String,Int](("1",2)))
    println("====================execise2======================")
    println(swap(Array("1","2","3","4")).mkString)
    println("====================execise3======================")
    val p =Test1.price(Multiple(10,Article("Blackwell Toster",29.95)))
    println(p)
    println("====================execise5======================")
    val l: List[Any] = List(List(3, 8), 2, List(5))
    println(leafSum_5(l))
    println("====================execise6======================")
    val r6 = Node6(Leaf(3),Node6(Leaf(3),Leaf(9)))
    println(leafSum_6(r6))
    println("====================execise7======================")
    val r7 = Node7(Node7(Leaf(3), Leaf(8)), Leaf(2), Node7(Leaf(5)))
    println(leafSum_7(r7))
    println("====================execise8======================")
    val r8 = Node8('+' , Node8('*',Leaf(3), Leaf(8)), Leaf(2), Node8('-' , Leaf(5)))
    println(eval(r8))
    println("====================execise9======================")
    execise9();
    println("====================execise10======================")
    execise10();
  }
}
