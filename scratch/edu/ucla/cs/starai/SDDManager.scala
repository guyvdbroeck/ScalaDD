package edu.ucla.cs.starai;

import edu.ucla.cs.starai.sdd.manager.SDDManager
import edu.ucla.cs.starai.logic.VTree


object SDDManagerScratch extends App {
    
  val vtree = VTree.balanced(8)
  
  println(s"Vtree variables = ${vtree.variables}")
  
  val mgr = SDDManager(vtree)
  
  println(s"False size = ${mgr.False.sddSize}")
  println(s"False model count = ${mgr.False.modelCount}")
  
  val x1 = mgr.literal(1)
  val x2 = mgr.literal(2)
  val x3 = mgr.literal(3)
  val x4 = mgr.literal(4)
  val x5 = mgr.literal(5)
  val x6 = mgr.literal(6)
  val x7 = mgr.literal(7)
  val x8 = mgr.literal(8)
  
  val c1 = !x1 ||  x2 || !x5
  println(s"Clause 1 size = ${c1.sddSize}")
  println(s"Clause 1 model count = ${c1.modelCount}")
  
  val c2 =  x1 || !x3 ||  x6
  println(s"Clause 2 size = ${c2.sddSize}")
  println(s"Clause 2 model count = ${c2.modelCount}")
  
  val c3 = !x2 ||  x4 ||  x7
  val c4 =  x2 ||  x7 || !x8
  val c5 = !x3 || !x4 ||  x6
  val c6 =  x4 || !x6 ||  x8
  val c7 =  x3 ||  x4 || !x8
  val c8 = !x5 ||  x6 || !x7
  val c9 =  x6 ||  x7 || !x8
  
  val cnf = c1 && c2 && c3 && c4 && c5 && c6 && c7 && c8 && c9
  println(s"CNF size = ${cnf.sddSize}")
  println(s"CNF model count = ${cnf.modelCount}")
  
  val unsat = cnf && !c5
  println(s"UNSAT size = ${unsat.sddSize}")
  println(s"UNSAT model count = ${unsat.modelCount}")
  
  println(s"Manager unique nodes cache size = ${mgr.uniqueNodesCacheSize}")
  
  
  println(s"UNSAT SDD = ${unsat}")
  
}

