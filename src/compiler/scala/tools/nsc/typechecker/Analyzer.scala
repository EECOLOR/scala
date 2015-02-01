/* NSC -- new Scala compiler
 * Copyright 2005-2013 LAMP/EPFL
 * @author  Martin Odersky
 */

package scala.tools.nsc
package typechecker

import scala.reflect.internal.util.Statistics


/** The main attribution phase.
 */
trait DefaultAnalyzer extends Analyzer 
    with DefaultContexts
    with DefaultNamers
    with DefaultTypers
    with DefaultInfer
    with DefaultImplicits
    with EtaExpansion
    with SyntheticMethods
    with DefaultUnapplies
    with DefaultMacros
    with DefaultNamesDefaults
    with DefaultTypeDiagnostics
    with DefaultContextErrors
    with DefaultStdAttachments
    with DefaultAnalyzerPlugins {
  
  import global._
  
  object namerFactory extends {
    val global: DefaultAnalyzer.this.global.type = DefaultAnalyzer.this.global
  } with SubComponent {
    val phaseName = "namer"
    val runsAfter = List[String]("parser")
    val runsRightAfter = None
    def newPhase(_prev: Phase): StdPhase = new StdPhase(_prev) {
      override val checkable = false
      override def keepsTypeParams = false

      def apply(unit: CompilationUnit) {
        newNamer(rootContext(unit)).enterSym(unit.body)
      }
    }
  }

  object packageObjects extends {
    val global: DefaultAnalyzer.this.global.type = DefaultAnalyzer.this.global
  } with SubComponent {
    val phaseName = "packageobjects"
    val runsAfter = List[String]()
    val runsRightAfter= Some("namer")

    def newPhase(_prev: Phase): StdPhase = new StdPhase(_prev) {
      override val checkable = false
      import global._

      val openPackageObjectsTraverser = new Traverser {
        override def traverse(tree: Tree): Unit = tree match {
          case ModuleDef(_, _, _) =>
            if (tree.symbol.name == nme.PACKAGEkw) {
              openPackageModule(tree.symbol, tree.symbol.owner)
            }
          case ClassDef(_, _, _, _) => () // make it fast
          case _ => super.traverse(tree)
        }
      }

      def apply(unit: CompilationUnit) {
        openPackageObjectsTraverser(unit.body)
      }
    }
  }

  object typerFactory extends {
    val global: DefaultAnalyzer.this.global.type = DefaultAnalyzer.this.global
  } with SubComponent {
    import scala.reflect.internal.TypesStats.typerNanos
    val phaseName = "typer"
    val runsAfter = List[String]()
    val runsRightAfter = Some("packageobjects")
    def newPhase(_prev: Phase): StdPhase = new StdPhase(_prev) {
      override def keepsTypeParams = false
      resetTyper()
      // the log accumulates entries over time, even though it should not (Adriaan, Martin said so).
      // Lacking a better fix, we clear it here (before the phase is created, meaning for each
      // compiler run). This is good enough for the resident compiler, which was the most affected.
      undoLog.clear()
      override def run() {
        val start = if (Statistics.canEnable) Statistics.startTimer(typerNanos) else null
        global.echoPhaseSummary(this)
        for (unit <- currentRun.units) {
          applyPhase(unit)
          undoLog.clear()
        }
        if (Statistics.canEnable) Statistics.stopTimer(typerNanos, start)
      }
      def apply(unit: CompilationUnit) {
        try {
          val typer = newTyper(rootContext(unit))
          unit.body = typer.typed(unit.body)
          if (global.settings.Yrangepos && !global.reporter.hasErrors) global.validatePositions(unit.body)
          for (workItem <- unit.toCheck) workItem()
          if (settings.warnUnusedImport)
            warnUnusedImports(unit)
          if (settings.warnUnused)
            typer checkUnused unit
        }
        finally {
          unit.toCheck.clear()
        }
      }
    }
  }
}
