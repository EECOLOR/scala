package scala.tools.nsc
package typechecker

import scala.reflect.macros.runtime.AbortMacroException


trait ContextErrors {
  self: Globals with
  Contexts =>
    
  import global._

  trait TyperContextErrors {
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
    private[nsc] val TyperErrorGen:TyperErrorGenObject
  }
  
  private[scala] trait InferencerContextErrors {
    private[scala] trait InferErrorGenObject {
      private[scala] def NotWithinBoundsErrorMessage(prefix: String, targs: List[Type], tparams: List[Symbol], explaintypes: Boolean):String
      
      /* Used by Infer and Typers */
      private[typechecker] def AccessError(tree: Tree, sym: Symbol, ctx: Context, explanation: String): AbsTypeError
      
      /* Used by Checkable (Infer) */
      private[typechecker] def TypePatternOrIsInstanceTestError(tree: Tree, tp: Type):Unit
    }
    private[scala] val InferErrorGen:InferErrorGenObject
  }
  
  private[scala] trait AbsTypeError {
    private[scala] def errPos: Position
    private[scala] def errMsg: String
  }
  
  /* Used by Contexts */
  protected trait AbsAmbiguousTypeError extends AbsTypeError
  
  /* Used by Contexts and Implicits */
  protected trait DivergentImplicitTypeError extends AbsTypeError {
    /* Used by Contexts */
    private[typechecker] def withPt(pt: Type): AbsTypeError
  }
  
  protected trait NamerContextErrors {
    protected trait NamerErrorGenObject {
      /* Used by MethodSynthesis (Namers) */
      private[typechecker] def BeanPropertyAnnotationFieldWithoutLetterError(tree: Tree):Unit
      private[typechecker] def BeanPropertyAnnotationPrivateFieldError(tree: Tree):Unit
      private[typechecker] def BeanPropertyAnnotationLimitationError(tree: Tree):Unit
      private[typechecker] def GetterDefinedTwiceError(getter: Symbol):Unit
      private[typechecker] def PrivateThisCaseClassParameterError(tree: Tree):Unit
      private[typechecker] def ValOrValWithSetterSuffixError(tree: Tree):Unit
    }
    /* Used by Namers and MethodSynthesis (Namers) */
    private[typechecker] val NamerErrorGen:NamerErrorGenObject
  }
}