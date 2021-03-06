/*
 * Copyright 2017 Guy Van den Broeck <guyvdb@cs.ucla.edu>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.ucla.cs.starai.sdd

import edu.ucla.cs.starai.logic.Circuit
import edu.ucla.cs.starai.logic.VTreeLeaf
import scala.collection._
import scala.language.existentials
import scala.collection.mutable.ListBuffer

trait Compressed[N <: Compressed[N]] extends ComposableSDD[N] with Circuit[N]{
  
  self: N =>
  
}

trait CompressedDecision[N <: Compressed[N]] 
  extends ComposableDecisionNode[N] with Compressed[N] {
  
  self: N =>
  
    def decomp: CompressedXYDecomposition[N]
  
}


trait CompressedXYDecomposition[N <: Compressed[N]] 
  extends ComposableXYDecomposition[N] {
  
  override val elements: List[ComposableElement[N]]
  
  /**
   * A map from subs to their element
   */
  val subsCache: Set[N]
  
  assume(subsCache.size == elements.size, "Compressed SDDs cannot have repeated subs, or missing cached subs")
  
  private def removeSub(sub: N, myElements: List[ComposableElement[N]] = elements): (N,List[ComposableElement[N]]) = {
    val prefix = new ListBuffer[ComposableElement[N]]
    var remainder = myElements
    while (!remainder.isEmpty) {
      val head = remainder.head
      remainder = remainder.tail
      if(head.sub == sub)
        return (head.prime, prefix ++: remainder)
      else
        prefix += head    
    }
    throw new IllegalArgumentException(s"$sub should be part of $myElements")
  }
  
  override def mapPrimes(f: N => N): CompressedXYDecomposition[N] = {
    var removedSubs: List[N] = Nil
    var newElements: List[ComposableElement[N]] = Nil
    for(elem <- elements){
      elem.mapPrime(f) match {
        case None => removedSubs = elem.sub :: removedSubs
        case Some(fe) => newElements = fe :: newElements 
      }
    }
    new CompressedXYDecompositionImpl(newElements,subsCache -- removedSubs)
  }
  
  override def mapPrimes(f: N => N, extraPrime:N, extraSub: N): ComposableXYDecomposition[N] = {
    if(extraPrime.isConsistent) {
      var removedSubs: List[N] = Nil
      var newElements: List[ComposableElement[N]] = Nil
      for(elem <- elements){
        elem.mapPrime(f) match {
          case None => removedSubs = elem.sub :: removedSubs
          case Some(fe) => newElements = fe :: newElements 
        }
      }
      val newSubsCache = subsCache -- removedSubs
      if(newSubsCache.contains(extraSub)){
        val (oldPrime,remainder) = removeSub(extraSub,newElements)
        val newElement = new ComposableElementImpl(oldPrime || extraPrime,extraSub)
        new CompressedXYDecompositionImpl(newElement :: remainder, newSubsCache)
      }else{
        new CompressedXYDecompositionImpl(
            new ComposableElementImpl(extraPrime,extraSub) :: newElements, newSubsCache + extraSub)
      }
    }else mapPrimes(f)
  }
  
  override def mapSubs(f: N => N): CompressedXYDecomposition[N] = {
    compress(elements.map(_.mapSub(f)))
  }
  
  private def compress(elems: List[ComposableElement[N]]): CompressedXYDecomposition[N] = {
    val subMap = mutable.Map.empty[N,ComposableElement[N]]
    var remaining = elems
    while(remaining.nonEmpty) {
      val elem = remaining.head
      remaining = remaining.tail
      val newElement = subMap.get(elem.sub) match{
        case None => elem
        case Some(existingElem) => 
          new ComposableElementImpl(existingElem.prime || elem.prime, elem.sub)
      }
      subMap.put(newElement.sub, newElement)
    }
    val newElements = subMap.values.toList
    val newSubsCache = subMap.keys.toSet
    new CompressedXYDecompositionImpl(newElements,newSubsCache)
  }
  
  override def &&(that: ComposableXYDecomposition[N]): CompressedXYDecomposition[N] = that match{
    case that: CompressedXYDecomposition[N] => this && that
    case _ => ???
  }
  
  def &&(that: CompressedXYDecomposition[N]): CompressedXYDecomposition[N] = {
    val elemConjoin = for(e1 <- this.elements; e2 <- that.elements) yield e1 && e2
    compress(elemConjoin.flatten)
  }
  
  // avoids unnecessary compression on negation
  // implemented for performance
  def unary_! = {
    var newElements: List[ComposableElement[N]] = Nil
    val newSubs = mutable.Set.empty[N]
    var remaining = elements
    while(remaining.nonEmpty) {
      val elem = remaining.head
      remaining = remaining.tail
      val notElem = !elem
      newElements = notElem :: newElements
      newSubs += notElem.sub
    }
    new CompressedXYDecompositionImpl(newElements,newSubs)
  }
  
}

final class CompressedXYDecompositionImpl[N <: Compressed[N]] (
  val elements: List[ComposableElement[N]], 
  val subsCache: Set[N])
  extends CompressedXYDecomposition[N]

object CompressedXYDecomposition{
  
   def apply[N <: Compressed[N]](p: N, s: N): CompressedXYDecomposition[N] = {
     require(p.isConsistent)
     val elems = List(new ComposableElementImpl(p,s))
     new CompressedXYDecompositionImpl[N](elems,immutable.Set(s))
   }
   
   def apply[N <: Compressed[N]](p1: N, s1: N, p2: N, s2: N): CompressedXYDecomposition[N] = {
     if(!p1.isConsistent){
       require(p2.isConsistent)
       apply(p2,s2)
     }else if(!p2.isConsistent){
       require(p1.isConsistent)
       apply(p1,s1)
     }else{
       val elems = List(new ComposableElementImpl(p1,s1),new ComposableElementImpl(p2,s2))
       new CompressedXYDecompositionImpl(elems,immutable.Set(s1,s2))
     }
   }
}
