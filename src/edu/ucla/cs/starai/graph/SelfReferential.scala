package edu.ucla.cs.starai.graph

import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom
import scala.collection.GenTraversableOnce

/**
 * A Traversable that contains itself
 */
trait SelfReferential[+A] extends Traversable[A]{
  
  // cannot use the toString from TraversableLike: it recurses infinitely if this is part of the iterable
  override def toString = s"$stringPrefix@$hashCode"
  
  // also need to override flatten, and other undefined operations for SelfReferentials?
  override def flatten[B](implicit asTraversable: A ⇒ GenTraversableOnce[B]): Traversable[B] = 
    throw new UnsupportedOperationException
    
  //hashCode is safe  
  
}