/*
 * Copyright 2017 Guy Van den Broeck
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

package edu.ucla.cs.starai.sdd.manager.normalized

import edu.ucla.cs.starai.logic.Circuit
import edu.ucla.cs.starai.sdd.ComposableDecisionNode
import edu.ucla.cs.starai.sdd.ComposableTerminal
import edu.ucla.cs.starai.sdd.ComposableSDD
import edu.ucla.cs.starai.sdd.Compressed
import edu.ucla.cs.starai.sdd.Normalized
import edu.ucla.cs.starai.sdd.NormalizedTerminal
import edu.ucla.cs.starai.sdd.NormalizedDecision
import edu.ucla.cs.starai.sdd.CompressedDecision
import edu.ucla.cs.starai.sdd.ComposableTrueNode
import edu.ucla.cs.starai.sdd.ComposableFalseNode
import edu.ucla.cs.starai.sdd.ComposableLiteralNode

/**
 * A normalized compressed SDD that is managed by its VTree
 */
trait ManagedSDD extends Circuit[ManagedSDD]
  with ComposableSDD[ManagedSDD] 
  with Normalized with Compressed{
  
  def vtree: SDDManager
  
}

trait ManagedDecision extends ManagedSDD
  with ComposableDecisionNode[ManagedSDD] 
  with NormalizedDecision[ManagedSDD] with CompressedDecision[ManagedSDD] {
  
  def vtree: SDDManagerINode
  
}

trait ManagedTerminal extends ManagedSDD with ComposableTerminal[ManagedSDD] with NormalizedTerminal {
    
  def vtree: SDDManagerLeaf
  
}

trait ManagedTrue extends ManagedSDD with ComposableTrueNode[ManagedSDD]
trait ManagedFalse extends ManagedSDD with ComposableFalseNode[ManagedSDD]
trait ManagedLiteral extends ManagedSDD with ComposableLiteralNode[ManagedSDD]

trait CachedNegation extends ManagedSDD {
  
    override abstract lazy val unary_! = super.unary_!
    
}