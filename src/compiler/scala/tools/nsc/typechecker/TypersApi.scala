package scala.tools.nsc
package typechecker


trait Typers { 
  self: Globals with
  Contexts with
  ContextErrors with
  Infer =>

  import global._
    
  def newTyper(context: Context): Typer

  private[tools] var lastTreeToTyper: Tree
  
  /* Used by TypersTracking (Typers) and this (Typers) */
  protected trait TypingStackObject {
    /* Used by Implicits, Typers and Infer */
    private[typechecker] def printTyping(tree: Tree, s: => String):Unit
    /* Used by Typers and Infer */
    private[typechecker] def printTyping(s: => String):Unit
  }
  
  /* Used by Implicits, Typers and Infer */
  protected val typingStack:TypingStackObject
  
  private[scala] def treeSymTypeMsg(tree: Tree): String
  
  private[nsc] def exampleTuplePattern(names: List[Name]): String
   
  /* Used by SuperAccessors */
  private[typechecker] def restrictionError(pos: Position, unit: CompilationUnit, msg: String): Unit
  
  /* Used by ContextErrors, RefChecks and TypeDiagnostics (Typers) */
  private[typechecker] def abstractVarMessage(sym: Symbol): String
  
  /* Used by ContextErrors, Infer and RefChecks */
  private[typechecker] def foundReqMsg(found: Type, req: Type): String
  
  /* Used by ContextErrors, Infer, RefChecks and TypeDiagnostics (Typers) */
  private[typechecker] def underlyingSymbol(member: Symbol): Symbol
  
  /* Used by ContextErrors */
  protected def alternatives(tree: Tree): List[Type]
  protected def decodeWithKind(name: Name, owner: Symbol): String
  protected def linePrecedes(t1: Tree, t2: Tree):Boolean
  protected def typePatternAdvice(sym: Symbol, ptSym: Symbol):String
  protected def withAddendum(pos: Position):String => String
  
  /* Used by ContextErrors and Infer */
  protected def withDisambiguation[T](locals: List[Symbol], types: Type*)(op: => T): T
  
  /* Used by Implicits */
  protected def setAddendum(pos: Position, msg: () => String):Unit
  
  trait Typer extends TyperContextErrors {
    def context:Context
    def typed(tree: Tree): Tree
    
    private[scala] val infer:Inferencer
    private[scala] def typedType(tree: Tree): Tree
    private[scala] def typed(tree: Tree, mode: Mode, pt: Type): Tree
    private[scala] def silent[T](op: Typer => T,
                  reportAmbiguousErrors: Boolean = context.ambiguousErrors,
                  newtree: Tree = context.tree): SilentResult[T]
    private[scala] def typedTypeConstructor(tree: Tree): Tree
    private[scala] def resolveClassTag(pos: Position, tp: Type, allowMaterialization: Boolean = true): Tree
    private[scala] def packedType(tree: Tree, owner: Symbol): Type
    private[scala] def resolveTypeTag(pos: Position, pre: Type, tp: Type, concrete: Boolean, allowMaterialization: Boolean = true): Tree

    private[nsc] def atOwner(owner: Symbol): Typer
    private[nsc] def context1:Context
    private[nsc] def typedCases(cases: List[CaseDef], pattp: Type, pt: Type): List[CaseDef]
    private[nsc] def typed(tree: Tree, pt: Type): Tree
    private[nsc] def typed(tree: Tree, mode: Mode): Tree
    private[nsc] def typedOperator(tree: Tree): Tree
    private[nsc] def atOwner(tree: Tree, owner: Symbol): Typer
    private[nsc] def typedPos(pos: Position)(tree: Tree):Tree
    
    /* Used by Infer */
    private[typechecker] def samToFunctionType(tp: Type, sam: Symbol = NoSymbol): Type
    
    /* Used by Macros */
    private[typechecker] def checkFeature(pos: Position, featureTrait: Symbol, construct: => String = "", immediate: Boolean = false): Boolean
    private[typechecker] def instantiatePossiblyExpectingUnit(tree: Tree, mode: Mode, pt: Type): Tree
    
    /* Used by Namers */
    private[typechecker] def qualifyingClass(tree: Tree, qual: Name, packageOK: Boolean):Symbol
    private[typechecker] def checkNonCyclic(pos: Position, tp: Type): Boolean
    private[typechecker] def computeMacroDefType(ddef: DefDef, pt: Type): Type
    private[typechecker] def computeType(tree: Tree, pt: Type): Type
    private[typechecker] def typedParentTypes(templ: Template): List[Tree]
		private[typechecker] def reenterTypeParams(tparams: List[TypeDef]): List[Symbol]
		private[typechecker] def typedAnnotation(ann: Tree, mode: Mode = EXPRmode): AnnotationInfo
		private[typechecker] def permanentlyHiddenWarning(pos: Position, hidden: Name, defn: Symbol):Unit
    
		/* Used by Namers and NamesDefaults */
		private[typechecker] def typedQualifier(tree: Tree): Tree
    
		/* Used by NamesDefaults */
    private[typechecker] def doTypedApply(tree: Tree, fun0: Tree, args: List[Tree], mode: Mode, pt: Type): Tree
    private[typechecker] def isNamedApplyBlock(tree: Tree):Boolean
    
    /* Used by Implicits and Typers */
    private[typechecker] def applyImplicitArgs(fun: Tree): Tree
    private[typechecker] def typed1(tree: Tree, mode: Mode, pt: Type): Tree
    
    /* Used by Typers */
    private[typechecker] def instantiate(tree: Tree, mode: Mode, pt: Type): Tree
    private[typechecker] def adaptToMember(qual: Tree, searchTemplate: Type, reportAmbiguous: Boolean = true, saveErrors: Boolean = true): Tree
    private[typechecker] def typedArgs(args: List[Tree], mode: Mode):List[Tree]
		private[typechecker] def makeAccessible(tree: Tree, sym: Symbol, pre: Type, site: Tree): (Tree, Type)
    
    /* Used by ContextErrors */
    private[typechecker] def cyclicReferenceMessage(sym: Symbol, tree: Tree):Option[String]

    /* Used by Adaptations */
    protected val runDefinitions: definitions.RunDefinitions
    
    protected trait checkUnusedObject {
      private[typechecker] def apply(unit: CompilationUnit):Unit
    }
    /* Used by Analyzer */
    protected def checkUnused:checkUnusedObject
    
    /* Used by PatternTypers (Typers) and TypeDiagnostics (Typers) */
    protected def reallyExists(sym: Symbol):Boolean
    
    /* Used by Implicits and PatternTypers (Typers) */
    protected def adapt(tree: Tree, mode: Mode, pt: Type, original: Tree = EmptyTree): Tree
    
    /* Used by PatternTypers (Typers) */
    protected def typedArg(arg: Tree, mode: Mode, newmode: Mode, pt: Type): Tree
    protected def typedType(tree: Tree, mode: Mode): Tree
  }
  
  private[scala] trait SilentResult[+T] {
    private[scala] def nonEmpty:Boolean
    
    private[nsc] def fold[U](none: => U)(f: T => U): U
    private[nsc] def orElse[T1 >: T](f: Seq[AbsTypeError] => T1): T1
    private[nsc] def filter(p: T => Boolean): SilentResult[T]
    private[nsc] def map[U](f: T => U): SilentResult[U]
  }
  private[scala] trait SilentResultValue[+T] extends SilentResult[T] {
    private[typechecker] def value:T
  }
  private[scala] object SilentResultValue {
    private[scala] def unapply[T](s:SilentResultValue[T]):Option[T] =
      Option(s).map(_.value)
  }
  
  private[scala] trait SilentTypeError extends SilentResult[Nothing] {
    private[scala] def err: AbsTypeError
    
    private[typechecker] def errors: List[AbsTypeError]
    /* Used by Typers */
    private[typechecker] def reportableErrors: List[AbsTypeError]
  }
  private[scala] object SilentTypeError {
    private[scala] def unapply(s:SilentTypeError):Option[AbsTypeError] = 
      Option(s).flatMap(_.errors.headOption)
  }
}