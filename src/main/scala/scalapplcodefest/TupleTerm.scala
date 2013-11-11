package scalapplcodefest

import scala.collection.SeqProxy

/**
 * @author Sebastian Riedel
 */
trait TupleTerm extends Term[Product] with SeqProxy[Term[Any]] {
  def variables = this.map(_.variables).reduce(_ ++ _)
}

case class TupleTerm2[A1, A2](a1: Term[A1], a2: Term[A2]) extends TupleTerm with Term[(A1, A2)] {
  import SetCastHelper._
  def self = Seq(a1, a2)
  def eval(state: State) = for (b1 <- a1.eval(state); b2 <- a2.eval(state)) yield (b1, b2)
  def default = (a1.default, a2.default)
  def domain[C >: (A1, A2)] = CartesianProductTerm2(a1.domain,a2.domain).as[C]
}

case class TupleTerm3[A1, A2, A3](a1: Term[A1], a2: Term[A2], a3: Term[A3])
  extends TupleTerm with Term[(A1, A2, A3)] {
  import SetCastHelper._
  def self = Seq(a1, a2, a3)
  def eval(state: State) = for (b1 <- a1.eval(state); b2 <- a2.eval(state); b3 <- a3.eval(state)) yield (b1, b2, b3)
  def default = (a1.default, a2.default, a3.default)
  def domain[C >: (A1, A2, A3)] = CartesianProductTerm3(a1.domain, a2.domain, a3.domain).as[C]
}

case class CartesianProduct2[A1,A2](d1:Set[A1],d2:Set[A2]) extends SetValue[(A1,A2)] {
  def contains(elem: (A1, A2)) = d1(elem._1) && d2(elem._2)
  def iterator = for (v1 <- d1.iterator; v2 <- d2.iterator) yield (v1,v2)
}
case class CartesianProduct3[A1,A2,A3](d1:Set[A1],d2:Set[A2],d3:Set[A3]) extends SetValue[(A1,A2,A3)] {
  def contains(elem: (A1, A2, A3)) = d1(elem._1) && d2(elem._2) && d3(elem._3)
  def iterator = for (v1 <- d1.iterator; v2 <- d2.iterator; v3 <- d3.iterator) yield (v1,v2,v3)
}


case class CartesianProductTerm2[A1,A2](a1:Term[Set[A1]],a2:Term[Set[A2]]) extends Term[Set[(A1,A2)]] {
  def eval(state: State) =
    for (b1 <- a1.eval(state); b2 <- a2.eval(state)) yield CartesianProduct2(b1,b2)
  def variables = a1.variables ++ a2.variables
  def default = Set((a1.default.head,a2.default.head))
  def domain[C >: Set[(A1, A2)]] = Constant(Util.setToBeImplementedLater)
}

case class CartesianProductTerm3[A1,A2,A3](a1:Term[Set[A1]],a2:Term[Set[A2]],a3:Term[Set[A3]]) extends Term[Set[(A1,A2,A3)]] {
  def eval(state: State) =
    for (b1 <- a1.eval(state); b2 <- a2.eval(state); b3 <- a3.eval(state)) yield CartesianProduct3(b1,b2,b3)
  def variables = a1.variables ++ a2.variables
  def default = Set((a1.default.head,a2.default.head,a3.default.head))
  def domain[C >: Set[(A1, A2, A3)]] = Constant(Util.setToBeImplementedLater)
}

object Wrapped3 {
  def unroll[A1,A2,A3](term:TupleTerm2[(A1,A2),A3]):Option[TupleTerm3[A1,A2,A3]] = term match {
    case TupleTerm2(TupleTerm2(a1,a2),a3) => Some(TupleTerm3(a1,a2,a3))
    case _ => None
  }
}