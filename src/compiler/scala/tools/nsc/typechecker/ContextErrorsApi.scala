package scala.tools.nsc
package typechecker

trait ContextErrors {
  self: Globals with
  Contexts =>
    
  import global._

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