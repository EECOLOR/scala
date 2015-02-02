package scala.tools.nsc
package typechecker


trait StdAttachments {
  self: Globals with
  Contexts =>
    
  import global._

  def markMacroImplRef(tree: Tree): Tree
  
  private[typechecker] def macroExpanderAttachment(tree: Tree): MacroExpanderAttachment
  protected def hasSuperArgs(tree: Tree): Boolean
  protected def isDynamicRewrite(tree: Tree): Boolean
  protected def isMacroExpansionSuppressed(tree: Tree): Boolean
  protected def isMacroImplRef(tree: Tree): Boolean
  protected def macroExpandee(tree: Tree): Tree
  protected def markDynamicRewrite(tree: Tree): Tree
  protected def superArgs(tree: Tree): Option[List[List[Tree]]]
  protected def suppressMacroExpansion(tree: Tree):Tree
  protected def unmarkDynamicRewrite(tree: Tree): Tree
  protected def unmarkMacroImplRef(tree: Tree): Tree
  protected def unsuppressMacroExpansion(tree: Tree): Tree
  protected def hasMacroExpansionAttachment(any: Any): Boolean
  protected def linkExpandeeAndDesugared(expandee: Tree, desugared: Tree): Unit
  protected def linkExpandeeAndExpanded(expandee: Tree, expanded: Any): Unit
  
  /** After being synthesized by the parser, primary constructors aren't fully baked yet.
   *  A call to super in such constructors is just a fill-me-in-later dummy resolved later
   *  by `parentTypes`. This attachment coordinates `parentTypes` and `typedTemplate` and
   *  allows them to complete the synthesis.
   */
  protected case class SuperArgsAttachment(argss: List[List[Tree]])

  protected case class MacroRuntimeAttachment(delayed: Boolean, typerContext: Context, macroContext: Option[MacroContext])
  
  private[scala] trait MacroExpanderAttachment {
    private[typechecker] def original: Tree
    private[typechecker] def desugared: Tree
  }
  private[scala] object MacroExpanderAttachment {
    private[scala] def unapply(m:MacroExpanderAttachment):Option[(Tree, Tree)] = 
      Option(m).map(m => (m.original, m.desugared))
  }
  
  private[scala] trait MacroExpansionAttachment {
    private[typechecker] def expandee: Tree 
    private[typechecker] def expanded: Any
  }
  private[scala] object MacroExpansionAttachment {
    private[scala] def unapply(m:MacroExpansionAttachment):Option[(Tree, Any)] = 
      Option(m).map(m => (m.expandee, m.expanded))
  }
  
  private[tools] type MacroContext = UnaffiliatedMacroContext { val universe: self.global.type }
  
  /** Carries information necessary to expand the host tree.
   *  At times we need to store this info, because macro expansion can be delayed until its targs are inferred.
   *  After a macro application has been successfully expanded, this attachment is destroyed.
   */
  protected type UnaffiliatedMacroContext = scala.reflect.macros.contexts.Context
}