package scala.tools.nsc

import reporters.Reporter
import transform._

class GlobalDefault(currentSettings: Settings, reporter: Reporter) extends Global(currentSettings, reporter) {
  lazy val analyzer: typechecker.Analyzer with CorrectGlobalType = new {
    val global: GlobalDefault.this.type = GlobalDefault.this
  } with typechecker.DefaultAnalyzer

  lazy val specializeTypes: SubComponent with SpecializeTypes with CorrectGlobalType = new {
    val global: GlobalDefault.this.type = GlobalDefault.this
    val runsAfter = List("")
    val runsRightAfter = Some("tailcalls")
  } with DefaultSpecializeTypes

  lazy val treeChecker: typechecker.TreeCheckers with CorrectGlobalType = new {
    val global: GlobalDefault.this.type = GlobalDefault.this
  } with typechecker.DefaultTreeCheckers
  
  lazy val erasureInstance: SubComponent with Erasure with CorrectGlobalType = new {
    val global: GlobalDefault.this.type = GlobalDefault.this
    val runsAfter = List("explicitouter")
    val runsRightAfter = Some("explicitouter")
  } with DefaultErasure

}