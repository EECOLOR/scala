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
  
  /* Used by Analyzer and Typers */
  protected def newNamer(context: Context): Namer
  
  /* Used by Contexts */
  protected def linkedClassOfClassOf(original: Symbol, ctx: Context): Symbol
  
  /* Used by Implicits, NamesDefaults and Typers */ 
  protected def companionSymbolOf(original: Symbol, ctx: Context): Symbol
  
  trait Namer extends NamerContextErrors {
    def standardEnterSym(tree: Tree): Context
    def standardEnsureCompanionObject(cdef: ClassDef, creator: ClassDef => Tree = companionModuleDef(_)): Symbol
    
    private[nsc] def enterSyms(trees: List[Tree]): Namer
    
    /* Used by Analyzer and Typers */
    private[typechecker] def enterSym(tree: Tree): Context
    
    /* Used by Typers, Namers, MethodSynthesis (Namers), ContextErrors and AnalyzerPlugins */
    private[typechecker] def context: Context
    
    /* Used by Namers */
    private[typechecker] def moduleClassTypeCompleter(tree: ModuleDef):TypeCompleter
    private[typechecker] def monoTypeCompleter(tree: Tree):TypeCompleter
    
    /* Used by MethodSynthesis (Namers) */
    private[typechecker] def accessorTypeCompleter(tree: ValDef, isSetter: Boolean):TypeCompleter
    protected def enterLazyVal(tree: ValDef, lazyAccessor: Symbol): TermSymbol
    protected def enterStrictVal(tree: ValDef): TermSymbol
    protected def namerOf(sym: Symbol): Namer
    protected def noFinishGetterSetter(vd: ValDef):Boolean
    protected def setPrivateWithin(tree: MemberDef, sym: Symbol): Symbol
    protected def owner:Symbol
    
    /* Used by Typers */
    private[typechecker] def enterValueParams(vparamss: List[List[ValDef]]): List[List[Symbol]]
		private[typechecker] def addDerivedTrees(typer: Typer, stat: Tree): List[Tree]
		private[typechecker] def validateParam(tree: ValDef):Unit
		private[typechecker] def enterIfNotThere(sym: Symbol):Unit
    
		/* Used by MethodSynthesis (Namers) and Typers */
    private[typechecker] def enterInScope(sym: Symbol): Symbol
    
    /* Used by MethodSynthesis (Namers) and Namers */
    private[typechecker] def enterSyntheticSym(tree: Tree): Symbol
  }
  
  /* Used by ContextErrors, Implicits, Namers and TypeDiagnostics (Typers) */
  protected trait TypeCompleter extends LazyType {
    /* Used By ContextErrors, Implicits, Namers, TypeDiagnostics (Typers) */
    private[typechecker] def tree: Tree
  }
  
  /* Used by Infer */
  private[typechecker] trait DefaultTypeCompleter extends LazyType with TypeCompleter
}