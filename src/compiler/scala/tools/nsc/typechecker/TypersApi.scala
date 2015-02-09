package scala.tools.nsc
package typechecker

import scala.reflect.macros.runtime.AbortMacroException

trait Typers {
  self: Globals with Contexts with ContextErrors with Infer with Namers =>

  import global._

  // using this concrete implementation so that it can be overridden with other defaults
  def newTyper(context: Context): Typer = new DefaultTyper(context)

  private class DefaultTyper(context:Context) extends Typer(context)
  
  protected def newTyperImplementation(context: Context, decorations:Option[OverridableTyperMethods]): (TyperApi, OverridableTyperMethods)

  case class OverridableTyperMethods(
    typed: (Tree, Mode, Type) => Tree,
    typed1: (Tree, Mode, Type) => Tree,
    finishMethodSynthesis: (Template, Symbol, Context) => Template,
    adapt: (Tree, Mode, Type, Tree) => Tree,
    stabilize: (Tree, Type, Mode, Type) => Tree,
    macroImplementationNotFoundMessage: Name => String,
    typedDocDef: (DocDef, Mode, Type) => Tree,
    missingSelectErrorTree: (Tree, Tree, Name) => Tree,
    canAdaptConstantTypeToLiteral: () => Boolean,
    canTranslateEmptyListToNil: () => Boolean
    )

  private[tools] var lastTreeToTyper: Tree

  /* Used by TypersTracking (Typers) and this (Typers) */
  protected trait TypingStackObject {
    /* Used by Implicits, Typers and Infer */
    private[typechecker] def printTyping(tree: Tree, s: => String): Unit
    /* Used by Typers and Infer */
    private[typechecker] def printTyping(s: => String): Unit
    /* Unchecked */
    private[typechecker] def showAdapt(original: Tree, adapted: Tree, pt: Type, context: Context): Unit
  }

  /* Used by Implicits, Typers and Infer */
  protected val typingStack: TypingStackObject

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
  protected def linePrecedes(t1: Tree, t2: Tree): Boolean
  protected def typePatternAdvice(sym: Symbol, ptSym: Symbol): String
  protected def withAddendum(pos: Position): String => String

  /* Used by ContextErrors and Infer */
  protected def withDisambiguation[T](locals: List[Symbol], types: Type*)(op: => T): T

  /* Used by Implicits */
  protected def setAddendum(pos: Position, msg: () => String): Unit

  protected trait checkUnusedObject {
    private[typechecker] def apply(unit: CompilationUnit): Unit
  }
  
  private[typechecker] trait TyperApi extends TyperContextErrors {
    def context: Context
    def typed(tree: Tree): Tree

    private[scala] val infer: Inferencer
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
    private[nsc] def context1: Context
    private[nsc] def typedCases(cases: List[CaseDef], pattp: Type, pt: Type): List[CaseDef]
    private[nsc] def typed(tree: Tree, pt: Type): Tree
    private[nsc] def typed(tree: Tree, mode: Mode): Tree
    private[nsc] def typedOperator(tree: Tree): Tree
    private[nsc] def atOwner(tree: Tree, owner: Symbol): Typer
    private[nsc] def typedPos(pos: Position)(tree: Tree): Tree
    private[nsc] def typedQualifier(tree: Tree): Tree
    private[nsc] def typedQualifier(tree: Tree, mode: Mode, pt: Type): Tree
    private[nsc] def namer:Namer
    private[nsc] def typedStats(stats: List[Tree], exprOwner: Symbol): List[Tree]
    
    /* Used by Infer */
    private[typechecker] def samToFunctionType(tp: Type, sam: Symbol = NoSymbol): Type

    /* Used by Macros */
    private[typechecker] def checkFeature(pos: Position, featureTrait: Symbol, construct: => String = "", immediate: Boolean = false): Boolean
    private[typechecker] def instantiatePossiblyExpectingUnit(tree: Tree, mode: Mode, pt: Type): Tree

    /* Used by Namers */
    private[typechecker] def qualifyingClass(tree: Tree, qual: Name, packageOK: Boolean): Symbol
    private[typechecker] def checkNonCyclic(pos: Position, tp: Type): Boolean
    private[typechecker] def computeMacroDefType(ddef: DefDef, pt: Type): Type
    private[typechecker] def computeType(tree: Tree, pt: Type): Type
    private[typechecker] def typedParentTypes(templ: Template): List[Tree]
    private[typechecker] def reenterTypeParams(tparams: List[TypeDef]): List[Symbol]
    private[typechecker] def typedAnnotation(ann: Tree, mode: Mode = EXPRmode): AnnotationInfo
    private[typechecker] def permanentlyHiddenWarning(pos: Position, hidden: Name, defn: Symbol): Unit

    /* Used by NamesDefaults */
    private[typechecker] def doTypedApply(tree: Tree, fun0: Tree, args: List[Tree], mode: Mode, pt: Type): Tree
    private[typechecker] def isNamedApplyBlock(tree: Tree): Boolean

    /* Used by Implicits and Typers */
    private[typechecker] def applyImplicitArgs(fun: Tree): Tree
    private[typechecker] def typed1(tree: Tree, mode: Mode, pt: Type): Tree

    /* Used by Typers */
    private[typechecker] def instantiate(tree: Tree, mode: Mode, pt: Type): Tree
    private[typechecker] def adaptToMember(qual: Tree, searchTemplate: Type, reportAmbiguous: Boolean = true, saveErrors: Boolean = true): Tree
    private[typechecker] def typedArgs(args: List[Tree], mode: Mode): List[Tree]
    private[typechecker] def makeAccessible(tree: Tree, sym: Symbol, pre: Type, site: Tree): (Tree, Type)

    /* Used by ContextErrors */
    private[typechecker] def cyclicReferenceMessage(sym: Symbol, tree: Tree): Option[String]

    /* Used by Analyzer */
    private[typechecker] def checkUnused: checkUnusedObject

    private[typechecker] val runDefinitions: definitions.RunDefinitions

    /* Used by PatternTypers (Typers) and TypeDiagnostics (Typers) */
    private[typechecker] def reallyExists(sym: Symbol): Boolean

    /* Used by Implicits and PatternTypers (Typers) */
    private[typechecker] def adapt(tree: Tree, mode: Mode, pt: Type, original: Tree = EmptyTree): Tree

    /* Used by PatternTypers (Typers) */
    private[typechecker] def typedArg(arg: Tree, mode: Mode, newmode: Mode, pt: Type): Tree
    private[typechecker] def typedType(tree: Tree, mode: Mode): Tree

    /* Unchecked */
    private[typechecker] def typedPos(pos: Position, mode: Mode, pt: Type)(tree: Tree): Tree
    private[typechecker] def typedByValueExpr(tree: Tree, pt: Type = WildcardType): Tree
    private[typechecker] def typedTypeApply(tree: Tree, mode: Mode, fun: Tree, args: List[Tree]): Tree
  }
  
  abstract class Typer(_context: Context, preventOverride:Boolean = false) extends TyperContextErrors {
    
    private[typechecker] val (internalTyper, overridden) = {
      if (preventOverride) (null.asInstanceOf[TyperApi], null.asInstanceOf[OverridableTyperMethods])
      else {
        val decorations = OverridableTyperMethods(
          typedHook,
          typed1Hook,
          finishMethodSynthesisHook,
          adaptHook,
          stabilizeHook,
          macroImplementationNotFoundMessageHook,
          typedDocDefHook,
          missingSelectErrorTreeHook,
          canAdaptConstantTypeToLiteralHook,
          canTranslateEmptyListToNilHook
        )
        newTyperImplementation(_context, Some(decorations))
      }
    }

    /* From TyperContextErrors */
    final private[nsc] lazy val TyperErrorGen:TyperErrorGenObject = internalTyper.TyperErrorGen
    
    /* Check previous commit for some of the uses */

    private def typedHook(tree: Tree, mode: Mode, pt: Type): Tree = typed(tree, mode, pt)
    private[scala] def typed(tree: Tree, mode: Mode, pt: Type): Tree = overridden.typed(tree, mode, pt)

    private def typed1Hook(tree: Tree, mode: Mode, pt: Type): Tree = typed1(tree, mode, pt)
    private[nsc] def typed1(tree: Tree, mode: Mode, pt: Type): Tree = overridden.typed1(tree, mode, pt)
    
    private def finishMethodSynthesisHook(templ: Template, clazz: Symbol, context: Context): Template = finishMethodSynthesis(templ, clazz, context)
    protected def finishMethodSynthesis(templ: Template, clazz: Symbol, context: Context): Template = overridden.finishMethodSynthesis(templ, clazz, context)
    
    private def adaptHook(tree: Tree, mode: Mode, pt: Type, original: Tree): Tree = adapt(tree, mode, pt, original)
    private[typechecker] def adapt(tree: Tree, mode: Mode, pt: Type, original: Tree = EmptyTree): Tree = overridden.adapt(tree, mode, pt, original)
    
    private def stabilizeHook(tree: Tree, pre: Type, mode: Mode, pt: Type): Tree = stabilize(tree, pre, mode, pt)
    protected def stabilize(tree: Tree, pre: Type, mode: Mode, pt: Type): Tree = overridden.stabilize(tree, pre, mode, pt)
    
    private def macroImplementationNotFoundMessageHook(name:Name):String = macroImplementationNotFoundMessage(name)
    private[nsc] def macroImplementationNotFoundMessage(name:Name):String = overridden.macroImplementationNotFoundMessage(name)
    
    private def typedDocDefHook(docDef: DocDef, mode: Mode, pt: Type): Tree = typedDocDef(docDef, mode, pt)
    protected def typedDocDef(docDef: DocDef, mode: Mode, pt: Type): Tree = overridden.typedDocDef(docDef, mode, pt)
 
    private def missingSelectErrorTreeHook(tree: Tree, qual: Tree, name: Name): Tree = missingSelectErrorTree(tree, qual, name)
    protected def missingSelectErrorTree(tree: Tree, qual: Tree, name: Name): Tree = overridden.missingSelectErrorTree(tree, qual, name)
    
    private def canAdaptConstantTypeToLiteralHook(): Boolean = canAdaptConstantTypeToLiteral
    protected def canAdaptConstantTypeToLiteral: Boolean = overridden.canAdaptConstantTypeToLiteral()
    
    private def canTranslateEmptyListToNilHook():Boolean = canTranslateEmptyListToNil
    protected def canTranslateEmptyListToNil:Boolean = overridden.canTranslateEmptyListToNil()
    
    final def context: Context = internalTyper.context
    final def typed(tree: Tree): Tree = internalTyper.typed(tree)
    final private[scala] lazy val infer: Inferencer = internalTyper.infer
    final private[scala] def typedType(tree: Tree): Tree = internalTyper.typedType(tree)
    final private[scala] def silent[T](op: Typer => T,
                                 reportAmbiguousErrors: Boolean = context.ambiguousErrors,
                                 newtree: Tree = context.tree): SilentResult[T] = internalTyper.silent(op, reportAmbiguousErrors, newtree)
    final private[scala] def typedTypeConstructor(tree: Tree): Tree = internalTyper.typedTypeConstructor(tree)
    final private[scala] def resolveClassTag(pos: Position, tp: Type, allowMaterialization: Boolean = true): Tree = internalTyper.resolveClassTag(pos, tp, allowMaterialization)
    final private[scala] def packedType(tree: Tree, owner: Symbol): Type = internalTyper.packedType(tree, owner)
    final private[scala] def resolveTypeTag(pos: Position, pre: Type, tp: Type, concrete: Boolean, allowMaterialization: Boolean = true): Tree = internalTyper.resolveTypeTag(pos, pre, tp, concrete, allowMaterialization)

    final private[nsc] def atOwner(owner: Symbol): Typer = internalTyper.atOwner(owner)
    final private[nsc] def context1: Context = internalTyper.context1
    final private[nsc] def typedCases(cases: List[CaseDef], pattp: Type, pt: Type): List[CaseDef] = internalTyper.typedCases(cases, pattp, pt)
    final private[nsc] def typed(tree: Tree, pt: Type): Tree = internalTyper.typed(tree, pt)
    final private[nsc] def typed(tree: Tree, mode: Mode): Tree = internalTyper.typed(tree, mode)
    final private[nsc] def typedOperator(tree: Tree): Tree = internalTyper.typedOperator(tree)
    final private[nsc] def atOwner(tree: Tree, owner: Symbol): Typer = internalTyper.atOwner(tree, owner)
    final private[nsc] def typedPos(pos: Position)(tree: Tree): Tree = internalTyper.typedPos(pos)(tree)
    final private[nsc] def typedQualifier(tree: Tree): Tree = internalTyper.typedQualifier(tree)
    final private[nsc] def typedQualifier(tree: Tree, mode: Mode, pt: Type): Tree = internalTyper.typedQualifier(tree, mode, pt)
    final private[nsc] def namer: Namer = internalTyper.namer
    final private[nsc] def typedStats(stats: List[Tree], exprOwner: Symbol): List[Tree] = internalTyper.typedStats(stats, exprOwner)

    /* Used by Infer */
    final private[typechecker] def samToFunctionType(tp: Type, sam: Symbol = NoSymbol): Type = internalTyper.samToFunctionType(tp, sam)

    /* Used by Macros */
    final private[typechecker] def checkFeature(pos: Position, featureTrait: Symbol, construct: => String = "", immediate: Boolean = false): Boolean = internalTyper.checkFeature(pos, featureTrait, construct, immediate)
    final private[typechecker] def instantiatePossiblyExpectingUnit(tree: Tree, mode: Mode, pt: Type): Tree = internalTyper.instantiatePossiblyExpectingUnit(tree, mode, pt)

    /* Used by Namers */
    final private[typechecker] def qualifyingClass(tree: Tree, qual: Name, packageOK: Boolean): Symbol = internalTyper.qualifyingClass(tree, qual, packageOK)
    final private[typechecker] def checkNonCyclic(pos: Position, tp: Type): Boolean = internalTyper.checkNonCyclic(pos, tp)
    final private[typechecker] def computeMacroDefType(ddef: DefDef, pt: Type): Type = internalTyper.computeMacroDefType(ddef, pt)
    final private[typechecker] def computeType(tree: Tree, pt: Type): Type = internalTyper.computeType(tree, pt)
    final private[typechecker] def typedParentTypes(templ: Template): List[Tree] = internalTyper.typedParentTypes(templ)
    final private[typechecker] def reenterTypeParams(tparams: List[TypeDef]): List[Symbol] = internalTyper.reenterTypeParams(tparams)
    final private[typechecker] def typedAnnotation(ann: Tree, mode: Mode = EXPRmode): AnnotationInfo = internalTyper.typedAnnotation(ann, mode)
    final private[typechecker] def permanentlyHiddenWarning(pos: Position, hidden: Name, defn: Symbol): Unit = internalTyper.permanentlyHiddenWarning(pos, hidden, defn)

    /* Used by NamesDefaults */
    final private[typechecker] def doTypedApply(tree: Tree, fun0: Tree, args: List[Tree], mode: Mode, pt: Type): Tree = internalTyper.doTypedApply(tree, fun0, args, mode, pt)
    final private[typechecker] def isNamedApplyBlock(tree: Tree): Boolean = internalTyper.isNamedApplyBlock(tree)

    /* Used by Implicits and Typers */
    final private[typechecker] def applyImplicitArgs(fun: Tree): Tree = internalTyper.applyImplicitArgs(fun)

    /* Used by Typers */
    final private[typechecker] def instantiate(tree: Tree, mode: Mode, pt: Type): Tree = internalTyper.instantiate(tree, mode, pt)
    final private[typechecker] def adaptToMember(qual: Tree, searchTemplate: Type, reportAmbiguous: Boolean = true, saveErrors: Boolean = true): Tree = internalTyper.adaptToMember(qual, searchTemplate, reportAmbiguous, saveErrors)
    final private[typechecker] def typedArgs(args: List[Tree], mode: Mode): List[Tree] = internalTyper.typedArgs(args, mode)
    final private[typechecker] def makeAccessible(tree: Tree, sym: Symbol, pre: Type, site: Tree): (Tree, Type) = internalTyper.makeAccessible(tree, sym, pre, site)

    /* Used by ContextErrors */
    final private[typechecker] def cyclicReferenceMessage(sym: Symbol, tree: Tree): Option[String] = internalTyper.cyclicReferenceMessage(sym, tree)
    
    /* Used by Analyzer */
    final private[typechecker] def checkUnused: checkUnusedObject = internalTyper.checkUnused

    /* Used by Adaptations */
    final protected lazy val runDefinitions: definitions.RunDefinitions = internalTyper.runDefinitions

    /* Used by PatternTypers (Typers) and TypeDiagnostics (Typers) */
    final protected def reallyExists(sym: Symbol): Boolean = internalTyper.reallyExists(sym)

    /* Used by PatternTypers (Typers) */
    final protected def typedArg(arg: Tree, mode: Mode, newmode: Mode, pt: Type): Tree = internalTyper.typedArg(arg, mode, newmode, pt)
    final protected def typedType(tree: Tree, mode: Mode): Tree = internalTyper.typedType(tree, mode) 

    /* Unchecked */
    final private[typechecker] def typedPos(pos: Position, mode: Mode, pt: Type)(tree: Tree): Tree = internalTyper.typedPos(pos, mode, pt)(tree)
    final private[typechecker] def typedByValueExpr(tree: Tree, pt: Type = WildcardType): Tree = internalTyper.typedByValueExpr(tree, pt)
    final private[typechecker] def typedTypeApply(tree: Tree, mode: Mode, fun: Tree, args: List[Tree]): Tree = internalTyper.typedTypeApply(tree, mode, fun, args)
  }

  private[nsc] trait TyperErrorGenObject {
    private[nsc] def MissingClassTagError(tree: Tree, tp: Type):Tree
    
    /* Used by Macros and Typers */
    private[typechecker] def MacroTooManyArgumentListsError(expandee: Tree):Nothing
    
    /* Used by Macros */
    private[typechecker] def MacroTooFewArgumentListsError(expandee: Tree):Nothing
    private[typechecker] def MacroTooFewArgumentsError(expandee: Tree):Nothing
    private[typechecker] def MacroTooManyArgumentsError(expandee: Tree):Nothing
    private[typechecker] def MacroGeneratedTypeError(expandee: Tree, err: TypeError = null):Nothing
    private[typechecker] def MacroFreeSymbolError(expandee: Tree, sym: FreeSymbol):Nothing
    private[typechecker] def MacroExpansionHasInvalidTypeError(expandee: Tree, expanded: Any):Nothing
    private[typechecker] def MacroGeneratedAbort(expandee: Tree, ex: AbortMacroException):Nothing
    private[typechecker] def MacroGeneratedException(expandee: Tree, ex: Throwable):Nothing
    private[typechecker] def MacroImplementationNotFoundError(expandee: Tree):Nothing
    private[typechecker] case object MacroExpansionException extends Exception with scala.util.control.ControlThrowable
    
    /* Used by Namers and Typers */
    private[typechecker] def NotAMemberError(sel: Tree, qual: Tree, name: Name):Unit
    private[typechecker] def UnstableTreeError(tree: Tree):Tree
    
    /* Used by PatternTypers (Typers) */
    private[typechecker] def WrongShapeExtractorExpansion(fun: Tree):AbsTypeError
    private[typechecker] def CaseClassConstructorError(tree: Tree, baseMessage: String):Tree
    private[typechecker] def OverloadedUnapplyError(tree: Tree):Unit
    private[typechecker] def TooManyArgsPatternError(fun: Tree):AbsTypeError
    private[typechecker] def BlackboxExtractorExpansion(fun: Tree):AbsTypeError
    private[typechecker] def UnapplyWithSingleArgError(tree: Tree):Unit
  }
  
  trait TyperContextErrors {
    
    private[nsc] val TyperErrorGen:TyperErrorGenObject
    
    /* Used by Typers */
    private[nsc] def macroImplementationNotFoundMessage(name:Name):String
  }
  
  private[scala] trait SilentResult[+T] {
    private[scala] def nonEmpty: Boolean

    private[nsc] def fold[U](none: => U)(f: T => U): U
    private[nsc] def orElse[T1 >: T](f: Seq[AbsTypeError] => T1): T1
    private[nsc] def filter(p: T => Boolean): SilentResult[T]
    private[nsc] def map[U](f: T => U): SilentResult[U]
  }
  private[scala] trait SilentResultValue[+T] extends SilentResult[T] {
    private[typechecker] def value: T
  }
  private[scala] object SilentResultValue {
    private[scala] def unapply[T](s: SilentResultValue[T]): Option[T] =
      Option(s).map(_.value)
  }

  private[scala] trait SilentTypeError extends SilentResult[Nothing] {
    private[scala] def err: AbsTypeError

    private[typechecker] def errors: List[AbsTypeError]
    /* Used by Typers */
    private[typechecker] def reportableErrors: List[AbsTypeError]
  }
  private[scala] object SilentTypeError {
    private[scala] def unapply(s: SilentTypeError): Option[AbsTypeError] =
      Option(s).flatMap(_.errors.headOption)
  }

  /* Unchecked */
  protected def fullSiteString(context: Context): String
}