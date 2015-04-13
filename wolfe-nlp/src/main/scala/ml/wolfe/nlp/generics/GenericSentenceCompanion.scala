package ml.wolfe.nlp.generics

import ml.wolfe.nlp.structures.{CharOffsets, TokenLike, Token, SentenceLike}

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable
import scala.language.{higherKinds, implicitConversions}

/**
 * @author Ingolf Becker
 * @date 31/03/2015
 */
trait GenericSentenceCompanion[ThisSentence[_ <: ThisSentence, _ <: TokenBound] <: SentenceLike[ThisSentence, _ <: TokenLike], TokenBound <: TokenLike[TokenBound]] {
  def newBuilder[S <: ThisSentence, T <: TokenBound] : mutable.Builder[T, ThisSentence[S, T]] = IndexedSeq.newBuilder[T] mapResult(fromSentence(empty(),_))

  implicit def canBuildFrom[OldT <: TokenBound, NewT <: TokenBound]: CanBuildFrom[ThisSentence[OldT], NewT, ThisSentence[NewT]]

  def newCanBuildFrom[OldT <: TokenBound, NewT <: TokenBound] :  CanBuildFrom[ThisSentence[OldT], NewT, ThisSentence[NewT]] = new CanBuildFrom[ThisSentence[OldT], NewT, ThisSentence[NewT]] {
    def apply(from: ThisSentence[OldT]): mutable.Builder[NewT, ThisSentence[NewT]] = IndexedSeq.newBuilder[NewT] mapResult (fromSentence(from, _))
    def apply(): mutable.Builder[NewT, ThisSentence[NewT]] = newBuilder
  }

  def fromSentence[NewT <: TokenBound](old: ThisSentence[_ <: ThisSentence,_ <: TokenBound], tokens: IndexedSeq[NewT]) : ThisSentence[ThisSentence, NewT]

  def empty() : ThisSentence[ThisSentence, TokenBound] = {
    val t = SentenceLike.newBuilder[ThisSentence, TokenBound].result()
    fromBasicSentence(t)
  }

  implicit def canBuildFromBasic[OldS <: SentenceLike[T], T <: TokenBound] : CanBuildFrom[OldS,T, ThisSentence[T]]

  def newCanBuildFromBasic[OldS <: SentenceLike[T], T <: TokenBound] : CanBuildFrom[OldS,T, ThisSentence[T]] = new CanBuildFrom[OldS,T, ThisSentence[T]] {
    override def apply(from: OldS): mutable.Builder[T, ThisSentence[T]] = IndexedSeq.newBuilder[T] mapResult {
      val s : ThisSentence[T] = fromBasicSentence(from)
      fromSentence(s, _)
    }
    override def apply(): mutable.Builder[T, ThisSentence[T]] = newBuilder[T]
  }

  def fromBasicSentence[OldS <: SentenceLike[OldS, T], T <: TokenBound](basicS: OldS) : ThisSentence[ThisSentence, T]

}