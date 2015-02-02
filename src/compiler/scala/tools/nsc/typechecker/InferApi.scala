package scala.tools.nsc
package typechecker

import scala.reflect.internal.Depth
import scala.collection.mutable
  

trait Infer {
  self: Globals with
  Contexts with 
  ContextErrors =>
    
  import global._
  
  private[scala] trait NoInstance {
    private[scala] def getMessage():String 
  }
  private[scala] def solvedTypes(tvars: List[TypeVar], tparams: List[Symbol], variances: List[Variance], upper: Boolean, depth: Depth): List[Type]
  private[scala] def freshVar(tparam: Symbol): TypeVar
  
  protected def formalTypes(formals: List[Type], numArgs: Int, removeByName: Boolean = true, removeRepeated: Boolean = true): List[Type]
  protected def isFullyDefined(tp: Type): Boolean
  protected def skipImplicit(tp: Type):Type
  protected def normalize(tp: Type): Type
  
  private[scala] trait Inferencer extends InferencerContextErrors {
    private[scala] def checkBounds(tree: Tree, pre: Type, owner: Symbol, tparams: List[Symbol], targs: List[Type], prefix: String): Boolean
    
    private[nsc] trait ApproximateAbstractsObject {
      private[nsc] def apply(tp: Type): Type
    }
    
    private[nsc] def approximateAbstracts:ApproximateAbstractsObject
    
    private[typechecker] def setError[T <: Tree](tree: T): T
    private[typechecker] def getContext:Context
    private[typechecker] def explainTypes(tp1: Type, tp2: Type):Unit
    private[typechecker] def inferConstructorInstance(tree: Tree, undetparams: List[Symbol], pt0: Type):Unit
    private[typechecker] def inferTypedPattern(tree0: Tree, pattp: Type, pt0: Type, canRemedy: Boolean): Type
    private[typechecker] def isApplicable(undetparams: List[Symbol], ftpe: Type, argtpes0: List[Type], pt: Type): Boolean
    private[typechecker] def isStrictlyMoreSpecific(ftpe1: Type, ftpe2: Type, sym1: Symbol, sym2: Symbol): Boolean
    private[typechecker] def isApplicableSafe(undetparams: List[Symbol], ftpe: Type, argtpes0: List[Type], pt: Type): Boolean
    private[typechecker] def inferExprInstance(tree: Tree, tparams: List[Symbol], pt: Type = WildcardType, treeTp0: Type = null, keepNothings: Boolean = true, useWeaklyCompatible: Boolean = false): List[Symbol]
    private[typechecker] def isUncheckable(P0: Type):Boolean
    private[typechecker] def ensureFullyDefined(tp: Type): Type
    private[typechecker] def isCheckable(P0: Type): Boolean
    private[typechecker] def checkKindBounds(tparams: List[Symbol], targs: List[Type], pre: Type, owner: Symbol): List[String]
    private[typechecker] def adjustTypeArgs(tparams: List[Symbol], tvars: List[TypeVar], targs: List[Type], restpe: Type = WildcardType): AdjustedTypeArgs.Result
    private[typechecker] def checkAccessible(tree: Tree, sym: Symbol, pre: Type, site: Tree): Tree
    private[typechecker] def checkCheckable(tree: Tree, P0: Type, X0: Type, inPattern: Boolean, canRemedy: Boolean = false):Unit
    private[typechecker] def followApply(tp: Type): Type
    private[typechecker] def eligibleForTupleConversion(formals: List[Type], argsCount: Int): Boolean
    private[typechecker] def inferArgumentInstance(tree: Tree, undetparams: List[Symbol], strictPt: Type, lenientPt: Type):Unit
    private[typechecker] def inferExprAlternative(tree: Tree, pt: Type): Tree
    private[typechecker] def inferMethodAlternative(tree: Tree, undetparams: List[Symbol], argtpes0: List[Type], pt0: Type): Unit
    private[typechecker] def inferMethodInstance(fn: Tree, undetparams: List[Symbol],
                            args: List[Tree], pt0: Type): List[Symbol]
    private[typechecker] def inferModulePattern(pat: Tree, pt: Type):Unit
    private[typechecker] def inferPolyAlternatives(tree: Tree, argtypes: List[Type]): Unit
    private[typechecker] def isApplicableBasedOnArity(tpe: Type, argsCount: Int, varargsStar: Boolean, tuplingAllowed: Boolean): Boolean
    private[typechecker] def makeFullyDefined(tp: Type): Type
    private[typechecker] def protoTypeArgs(tparams: List[Symbol], formals: List[Type], restpe: Type, pt: Type): List[Type]
    protected def context: Context
    
    protected trait AdjustedTypeArgsObject {
      private[typechecker] type Result = mutable.LinkedHashMap[Symbol, Option[Type]]
      private[typechecker] def unapply(a:Result): Some[(List[Symbol], List[Type])]
    }
    private[typechecker] val AdjustedTypeArgs:AdjustedTypeArgsObject
    
  }
  
}