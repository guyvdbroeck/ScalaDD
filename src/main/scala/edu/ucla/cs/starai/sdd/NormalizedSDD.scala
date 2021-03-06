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
import edu.ucla.cs.starai.logic.VTreeINode
import edu.ucla.cs.starai.logic.VTree
import scala.language.existentials

trait Normalized extends SDD with Circuit[Normalized]

trait NormalizedDecision[+N <: Normalized] extends DecisionNode[N] with Normalized {
  
  self: N =>
    
  def vtree: VTreeINode[T] forSome { type T <: VTree[T] }
    
  assume(primes.forall{_.respects(vtree.vl)}, "no vtree nodes are skipped")
  assume(subs.forall{_.respects(vtree.vr)}, "no vtree nodes are skipped")
  
}

trait NormalizedTerminal extends TerminalNode with Normalized {
  
  def vtree: VTreeLeaf[T] forSome { type T <: VTree[T] }
  
}
