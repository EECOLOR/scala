package scala.tools.nsc
package typechecker


trait Typers { 
  self: Globals with
  Contexts with
  ContextErrors with
  TypeDiagnostics with
  Infer =>

  import global._
    
  def newTyper(context: Context): Typer

  private[tools] var lastTreeToTyper: Tree
  
  protected trait TypingStackObject {
    private[typechecker] def printTyping(tree: Tree, s: => String):Unit
    private[typechecker] def printTyping(s: => String):Unit
  }
  
  protected val typingStack:TypingStackObject
  
  trait Typer extends TyperDiagnostics with TyperContextErrors {
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
    
    protected def context_=(context:Context):Unit
    private[typechecker] def samToFunctionType(tp: Type, sam: Symbol = NoSymbol): Type
    private[typechecker] def checkFeature(pos: Position, featureTrait: Symbol, construct: => String = "", immediate: Boolean = false): Boolean
    private[typechecker] def instantiatePossiblyExpectingUnit(tree: Tree, mode: Mode, pt: Type): Tree
    private[typechecker] def qualifyingClass(tree: Tree, qual: Name, packageOK: Boolean):Symbol
    private[typechecker] def checkNonCyclic(pos: Position, tp: Type): Boolean
    private[typechecker] def computeMacroDefType(ddef: DefDef, pt: Type): Type
    private[typechecker] def computeType(tree: Tree, pt: Type): Type
    private[typechecker] def typedParentTypes(templ: Template): List[Tree]
    private[typechecker] def reenterTypeParams(tparams: List[TypeDef]): List[Symbol]
    private[typechecker] def typedQualifier(tree: Tree): Tree
    private[typechecker] def typedAnnotation(ann: Tree, mode: Mode = EXPRmode): AnnotationInfo
    private[typechecker] def doTypedApply(tree: Tree, fun0: Tree, args: List[Tree], mode: Mode, pt: Type): Tree
    private[typechecker] def isNamedApplyBlock(tree: Tree):Boolean
    private[typechecker] def applyImplicitArgs(fun: Tree): Tree
    private[typechecker] def instantiate(tree: Tree, mode: Mode, pt: Type): Tree
    private[typechecker] def adaptToMember(qual: Tree, searchTemplate: Type, reportAmbiguous: Boolean = true, saveErrors: Boolean = true): Tree
    private[typechecker] def typedArgs(args: List[Tree], mode: Mode):List[Tree]
    private[typechecker] def typed1(tree: Tree, mode: Mode, pt: Type): Tree
    private[typechecker] def makeAccessible(tree: Tree, sym: Symbol, pre: Type, site: Tree): (Tree, Type)
    protected val runDefinitions: definitions.RunDefinitions
    
    protected trait checkUnusedObject {
      private[typechecker] def apply(unit: CompilationUnit):Unit
    }
    protected def checkUnused:checkUnusedObject
    
    protected def reallyExists(sym: Symbol):Boolean
    protected def adapt(tree: Tree, mode: Mode, pt: Type, original: Tree = EmptyTree): Tree
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
    private[typechecker] def reportableErrors: List[AbsTypeError]
  }
  private[scala] object SilentTypeError {
    private[scala] def unapply(s:SilentTypeError):Option[AbsTypeError] = 
      Option(s).flatMap(_.errors.headOption)
  }
}