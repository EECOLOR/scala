/* NSC -- new Scala compiler
 * Copyright 2005-2013 LAMP/EPFL
 * @author  Martin Odersky
 */

package scala.tools.nsc
package typechecker

/**
 *  @author Lukas Rytz
 *  @version 1.0
 */
trait DefaultAnalyzerPlugins extends AnalyzerPlugins { 
  //self: Analyzer =>
  self: Globals with 
  Typers with 
  Macros with 
  Namers with 
  Unapplies with 
  Contexts =>

  import global._

  /** A list of registered analyzer plugins */
  private var analyzerPlugins: List[AnalyzerPlugin] = Nil

  /** Registers a new analyzer plugin */
  def addAnalyzerPlugin(plugin: AnalyzerPlugin) {
    if (!analyzerPlugins.contains(plugin))
      analyzerPlugins = plugin :: analyzerPlugins
  }

  private abstract class CumulativeOp[T] {
    def default: T
    def accumulate: (T, AnalyzerPlugin) => T
  }

  private def invoke[T](op: CumulativeOp[T]): T = {
    if (analyzerPlugins.isEmpty) op.default
    else analyzerPlugins.foldLeft(op.default)((current, plugin) =>
      if (!plugin.isActive()) current else op.accumulate(current, plugin))
  }

  /** @see AnalyzerPlugin.pluginsPt */
  def pluginsPt(pt: Type, typer: Typer, tree: Tree, mode: Mode): Type =
    // performance opt
    if (analyzerPlugins.isEmpty) pt
    else invoke(new CumulativeOp[Type] {
      def default = pt
      def accumulate = (pt, p) => p.pluginsPt(pt, typer, tree, mode)
    })

  /** @see AnalyzerPlugin.pluginsTyped */
  def pluginsTyped(tpe: Type, typer: Typer, tree: Tree, mode: Mode, pt: Type): Type =
    // performance opt
    if (analyzerPlugins.isEmpty) addAnnotations(tree, tpe)
    else invoke(new CumulativeOp[Type] {
      // support deprecated methods in annotation checkers
      def default = addAnnotations(tree, tpe)
      def accumulate = (tpe, p) => p.pluginsTyped(tpe, typer, tree, mode, pt)
    })

  /** @see AnalyzerPlugin.pluginsTypeSig */
  def pluginsTypeSig(tpe: Type, typer: Typer, defTree: Tree, pt: Type): Type = invoke(new CumulativeOp[Type] {
    def default = tpe
    def accumulate = (tpe, p) => p.pluginsTypeSig(tpe, typer, defTree, pt)
  })

  /** @see AnalyzerPlugin.pluginsTypeSigAccessor */
  def pluginsTypeSigAccessor(tpe: Type, typer: Typer, tree: ValDef, sym: Symbol): Type = invoke(new CumulativeOp[Type] {
    def default = tpe
    def accumulate = (tpe, p) => p.pluginsTypeSigAccessor(tpe, typer, tree, sym)
  })

  /** @see AnalyzerPlugin.canAdaptAnnotations */
  def canAdaptAnnotations(tree: Tree, typer: Typer, mode: Mode, pt: Type): Boolean = invoke(new CumulativeOp[Boolean] {
    // support deprecated methods in annotation checkers
    def default = global.canAdaptAnnotations(tree, mode, pt)
    def accumulate = (curr, p) => curr || p.canAdaptAnnotations(tree, typer, mode, pt)
  })

  /** @see AnalyzerPlugin.adaptAnnotations */
  def adaptAnnotations(tree: Tree, typer: Typer, mode: Mode, pt: Type): Tree = invoke(new CumulativeOp[Tree] {
    // support deprecated methods in annotation checkers
    def default = global.adaptAnnotations(tree, mode, pt)
    def accumulate = (tree, p) => p.adaptAnnotations(tree, typer, mode, pt)
  })

  /** @see AnalyzerPlugin.pluginsTypedReturn */
  def pluginsTypedReturn(tpe: Type, typer: Typer, tree: Return, pt: Type): Type = invoke(new CumulativeOp[Type] {
    def default = adaptTypeOfReturn(tree.expr, pt, tpe)
    def accumulate = (tpe, p) => p.pluginsTypedReturn(tpe, typer, tree, pt)
  })

  /** A list of registered macro plugins */
  private var macroPlugins: List[MacroPlugin] = Nil

  /** Registers a new macro plugin */
  def addMacroPlugin(plugin: MacroPlugin) {
    if (!macroPlugins.contains(plugin))
      macroPlugins = plugin :: macroPlugins
  }

  private abstract class NonCumulativeOp[T] {
    def position: Position
    def description: String
    def default: T
    def custom(plugin: MacroPlugin): Option[T]
  }

  private def invoke[T](op: NonCumulativeOp[T]): T = {
    if (macroPlugins.isEmpty) op.default
    else {
      val results = macroPlugins.filter(_.isActive()).map(plugin => (plugin, op.custom(plugin)))
      results.flatMap { case (p, Some(result)) => Some((p, result)); case _ => None } match {
        case (p1, _) :: (p2, _) :: _ => typer.context.error(op.position, s"both $p1 and $p2 want to ${op.description}"); op.default
        case (_, custom) :: Nil => custom
        case Nil => op.default
      }
    }
  }

  /** @see MacroPlugin.pluginsTypedMacroBody */
  def pluginsTypedMacroBody(typer: Typer, ddef: DefDef): Tree = invoke(new NonCumulativeOp[Tree] {
    def position = ddef.pos
    def description = "typecheck this macro definition"
    def default = standardTypedMacroBody(typer, ddef)
    def custom(plugin: MacroPlugin) = plugin.pluginsTypedMacroBody(typer, ddef)
  })

  /** @see MacroPlugin.pluginsIsBlackbox */
  def pluginsIsBlackbox(macroDef: Symbol): Boolean = invoke(new NonCumulativeOp[Boolean] {
    def position = macroDef.pos
    def description = "compute boxity for this macro definition"
    def default = standardIsBlackbox(macroDef)
    def custom(plugin: MacroPlugin) = plugin.pluginsIsBlackbox(macroDef)
  })

  /** @see MacroPlugin.pluginsMacroExpand */
  def pluginsMacroExpand(typer: Typer, expandee: Tree, mode: Mode, pt: Type): Tree = invoke(new NonCumulativeOp[Tree] {
    def position = expandee.pos
    def description = "expand this macro application"
    def default = standardMacroExpand(typer, expandee, mode, pt)
    def custom(plugin: MacroPlugin) = plugin.pluginsMacroExpand(typer, expandee, mode, pt)
  })

  /** @see MacroPlugin.pluginsMacroArgs */
  def pluginsMacroArgs(typer: Typer, expandee: Tree): MacroArgs = invoke(new NonCumulativeOp[MacroArgs] {
    def position = expandee.pos
    def description = "compute macro arguments for this macro application"
    def default = standardMacroArgs(typer, expandee)
    def custom(plugin: MacroPlugin) = plugin.pluginsMacroArgs(typer, expandee)
  })

  /** @see MacroPlugin.pluginsMacroRuntime */
  def pluginsMacroRuntime(expandee: Tree): MacroRuntime = invoke(new NonCumulativeOp[MacroRuntime] {
    def position = expandee.pos
    def description = "compute macro runtime for this macro application"
    def default = standardMacroRuntime(expandee)
    def custom(plugin: MacroPlugin) = plugin.pluginsMacroRuntime(expandee)
  })

  /** @see MacroPlugin.pluginsEnterSym */
  def pluginsEnterSym(namer: Namer, tree: Tree): Context =
    if (macroPlugins.isEmpty) namer.standardEnterSym(tree)
    else invoke(new NonCumulativeOp[Context] {
      def position = tree.pos
      def description = "enter a symbol for this tree"
      def default = namer.standardEnterSym(tree)
      def custom(plugin: MacroPlugin) = {
        val hasExistingSym = tree.symbol != NoSymbol
        val result = plugin.pluginsEnterSym(namer, tree)
        if (result && hasExistingSym) Some(namer.context)
        else if (result && tree.isInstanceOf[Import]) Some(namer.context.make(tree))
        else if (result) Some(namer.context)
        else None
      }
    })

  /** @see MacroPlugin.pluginsEnsureCompanionObject */
  def pluginsEnsureCompanionObject(namer: Namer, cdef: ClassDef, creator: ClassDef => Tree = companionModuleDef(_)): Symbol = invoke(new NonCumulativeOp[Symbol] {
    def position = cdef.pos
    def description = "enter a companion symbol for this tree"
    def default = namer.standardEnsureCompanionObject(cdef, creator)
    def custom(plugin: MacroPlugin) = plugin.pluginsEnsureCompanionObject(namer, cdef, creator)
  })

  /** @see MacroPlugin.pluginsEnterStats */
  def pluginsEnterStats(typer: Typer, stats: List[Tree]): List[Tree] = {
    // performance opt
    if (macroPlugins.isEmpty) stats
    else macroPlugins.foldLeft(stats)((current, plugin) =>
      if (!plugin.isActive()) current else plugin.pluginsEnterStats(typer, stats))
  }
}
