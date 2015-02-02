package scala.tools.nsc
package typechecker


trait Namers {
  self: Globals with 
  Contexts with
  ContextErrors with
  Unapplies with
  Typers =>
    
  import global._
    
  def lockedCount:Int
  
  protected def newNamer(context: Context): Namer
  protected def linkedClassOfClassOf(original: Symbol, ctx: Context): Symbol
  protected def companionSymbolOf(original: Symbol, ctx: Context): Symbol
  
  trait Namer extends NamerContextErrors {
    def standardEnterSym(tree: Tree): Context
    def standardEnsureCompanionObject(cdef: ClassDef, creator: ClassDef => Tree = companionModuleDef(_)): Symbol
    
    private[nsc] def enterSyms(trees: List[Tree]): Namer
    
    protected def typer:Typer
    private[typechecker] def enterSym(tree: Tree): Context
    private[typechecker] def context: Context
    private[typechecker] def moduleClassTypeCompleter(tree: ModuleDef):TypeCompleter
    private[typechecker] def accessorTypeCompleter(tree: ValDef, isSetter: Boolean):TypeCompleter
    private[typechecker] def enterValueParams(vparamss: List[List[ValDef]]): List[List[Symbol]]
    private[typechecker] def addDerivedTrees(typer: Typer, stat: Tree): List[Tree]
    private[typechecker] def validateParam(tree: ValDef):Unit
    private[typechecker] def enterInScope(sym: Symbol): Symbol
    private[typechecker] def enterIfNotThere(sym: Symbol):Unit
    private[typechecker] def monoTypeCompleter(tree: Tree):TypeCompleter
    private[typechecker] def enterSyntheticSym(tree: Tree): Symbol
    protected def enterLazyVal(tree: ValDef, lazyAccessor: Symbol): TermSymbol
    protected def enterStrictVal(tree: ValDef): TermSymbol
    protected def namerOf(sym: Symbol): Namer
    protected def noFinishGetterSetter(vd: ValDef):Boolean
    protected def owner:Symbol
    protected def setPrivateWithin(tree: MemberDef, sym: Symbol): Symbol
  }
  
  protected trait TypeCompleter extends LazyType {
    private[typechecker] def tree: Tree
    private[typechecker] def complete(sym: Symbol):Unit
  }
  
  private[typechecker] trait DefaultTypeCompleter extends LazyType with TypeCompleter {
    private[typechecker] val tree: Tree
  }
}