package scala.tools.nsc

import transform.{ SpecializeTypes, DefaultSpecializeTypes, Erasure, DefaultErasure }

/*
 * This object exists purely to prevent Global from being recompiled (taking a big 
 * chunk of the compiler with it). Changing a method signature in the `DefaultTypers`
 * trait now 'only' takes 8 other files with it. 
 */
object GlobalImplementations {

  private[nsc] def analyzerInstance(globalInstance: Global): typechecker.Analyzer with globalInstance.CorrectGlobalType =
    new {
      val global: globalInstance.type = globalInstance
    } with typechecker.DefaultAnalyzer

  private[nsc] def specializeTypesInstance(globalInstance: Global): SubComponent with SpecializeTypes with globalInstance.CorrectGlobalType =
    new {
      val global: globalInstance.type = globalInstance
      val runsAfter = List("")
      val runsRightAfter = Some("tailcalls")
    } with DefaultSpecializeTypes

  private[nsc] def erasureInstance(globalInstance: Global): SubComponent with Erasure with globalInstance.CorrectGlobalType =
    new {
      val global: globalInstance.type = globalInstance
      val runsAfter = List("explicitouter")
      val runsRightAfter = Some("explicitouter")
    } with DefaultErasure

  private[nsc] def treeCheckerInstance(globalInstance: Global): typechecker.TreeCheckers with globalInstance.CorrectGlobalType =
    new {
      val global: globalInstance.type = globalInstance
    } with typechecker.DefaultTreeCheckers

}