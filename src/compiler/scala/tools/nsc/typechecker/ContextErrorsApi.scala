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
      
      private[typechecker] def MacroTooManyArgumentListsError(expandee: Tree):Nothing
      private[typechecker] def MacroTooFewArgumentListsError(expandee: Tree):Nothing
      private[typechecker] def MacroTooFewArgumentsError(expandee: Tree):Nothing
      private[typechecker] def MacroTooManyArgumentsError(expandee: Tree):Nothing
      private[typechecker] def MacroGeneratedTypeError(expandee: Tree, err: TypeError = null):Nothing
      private[typechecker] def MacroFreeSymbolError(expandee: Tree, sym: FreeSymbol):Nothing
      private[typechecker] def MacroExpansionHasInvalidTypeError(expandee: Tree, expanded: Any):Nothing
      private[typechecker] def MacroGeneratedAbort(expandee: Tree, ex: AbortMacroException):Nothing
      private[typechecker] def MacroGeneratedException(expandee: Tree, ex: Throwable):Nothing
      private[typechecker] def MacroImplementationNotFoundError(expandee: Tree):Nothing
      private[typechecker] def NotAMemberError(sel: Tree, qual: Tree, name: Name):Unit
      private[typechecker] def UnstableTreeError(tree: Tree):Tree
      private[typechecker] def WrongShapeExtractorExpansion(fun: Tree):AbsTypeError
      private[typechecker] def CaseClassConstructorError(tree: Tree, baseMessage: String):Tree
      private[typechecker] def OverloadedUnapplyError(tree: Tree):Unit
      private[typechecker] def TooManyArgsPatternError(fun: Tree):AbsTypeError
      private[typechecker] def BlackboxExtractorExpansion(fun: Tree):AbsTypeError
      private[typechecker] def UnapplyWithSingleArgError(tree: Tree):Unit
      
      private[typechecker] trait MacroExpansionExceptionObject
      private[typechecker] val MacroExpansionException:MacroExpansionExceptionObject
    }
    private[nsc] val TyperErrorGen:TyperErrorGenObject
  }
  
  private[scala] trait InferencerContextErrors {
    private[scala] trait InferErrorGenObject {
      private[scala] def NotWithinBoundsErrorMessage(prefix: String, targs: List[Type], tparams: List[Symbol], explaintypes: Boolean):String
      private[typechecker] def AccessError(tree: Tree, sym: Symbol, ctx: Context, explanation: String): AbsTypeError
      private[typechecker] def TypePatternOrIsInstanceTestError(tree: Tree, tp: Type):Unit
    }
    private[scala] val InferErrorGen:InferErrorGenObject
  }
  
  private[scala] trait AbsTypeError {
    private[scala] def errPos: Position
    private[scala] def errMsg: String
  }
  
  private[typechecker] trait AbsAmbiguousTypeError extends AbsTypeError
  private[typechecker] trait DivergentImplicitTypeError extends AbsTypeError {
    private[typechecker] def withPt(pt: Type): AbsTypeError
  }
  
  private[typechecker] trait ErrorUtilsObject
  private[typechecker] def ErrorUtils:ErrorUtilsObject
  
  private[typechecker] trait NamerContextErrors {
    private[typechecker] trait NamerErrorGenObject {
      private[typechecker] def BeanPropertyAnnotationFieldWithoutLetterError(tree: Tree):Unit
      private[typechecker] def BeanPropertyAnnotationPrivateFieldError(tree: Tree):Unit
      private[typechecker] def BeanPropertyAnnotationLimitationError(tree: Tree):Unit
      private[typechecker] def GetterDefinedTwiceError(getter: Symbol):Unit
      private[typechecker] def PrivateThisCaseClassParameterError(tree: Tree):Unit
      private[typechecker] def ValOrValWithSetterSuffixError(tree: Tree):Unit
    }
    private[typechecker] val NamerErrorGen:NamerErrorGenObject
  }
}