package scala.tools.nsc
package typechecker


trait Implicits {
  self: Globals with
  Contexts =>
    
  import global._
  
  private[scala] def inferImplicit(tree: Tree, pt: Type, reportAmbiguous: Boolean, isView: Boolean, context: Context, saveAmbiguousDivergent: Boolean, pos: Position): SearchResult
  private[scala] def inferImplicit(tree: Tree, pt: Type, isView: Boolean, context: Context, silent: Boolean, withMacrosDisabled: Boolean, pos: Position, onError: (Position, String) => Unit): Tree
  
  private[nsc] def inferImplicit(tree: Tree, pt: Type, reportAmbiguous: Boolean, isView: Boolean, context: Context, saveAmbiguousDivergent: Boolean): SearchResult
  private[nsc] def allViewsFrom(tp: Type, context: Context, tpars: List[Symbol]): List[(SearchResult, List[TypeConstraint])]
  
  protected def newImplicitInfo(name: Name, pre: Type, sym: Symbol):ImplicitInfo
  protected def resetImplicits():Unit
  protected def SearchFailure:SearchResult
  protected def inferImplicit(tree: Tree, pt: Type, reportAmbiguous: Boolean, isView: Boolean, context: Context): SearchResult
  
  private[scala] trait OpenImplicit {
    private[scala] def info: ImplicitInfo
    private[scala] def pt: Type
    private[scala] def tree: Tree
  }
  
  private[scala] trait SearchResult {
    private[scala] def tree: Tree
    
    private[nsc] def subst: TreeTypeSubstituter
    
    /* Used by Implicits */
    private[typechecker] def isFailure:Boolean
    private[typechecker] def isDivergent:Boolean
    private[typechecker] def isAmbiguousFailure:Boolean
    
    /* Used by Implicits and Typers */
    private[typechecker] def isSuccess:Boolean
  }
  
  private[scala] trait ImplicitInfo {
    private[scala] def pre: Type
    private[scala] def sym: Symbol
    
    /* Used by Implicits */
    private[typechecker] def isStablePrefix:Boolean
    private[typechecker] def isCyclicOrErroneous:Boolean
    private[typechecker] var useCountView:Int
    private[typechecker] var useCountArg:Int
    
    /* Used by Implicits and ContextErrors */
    private[typechecker] def name: Name
    private[typechecker] def tpe: Type
  }
  
  /* Used by Contexts and Implicits */
  protected object OpenImplicit {
    /* Used by Implicits */
    private[typechecker] def unapply(o:OpenImplicit):Option[(ImplicitInfo, Type, Tree)] =
      Option(o).map(o => (o.info, o.pt, o.tree))
  }
  
  /* Used by Implicits and ContextErrors */
  protected trait ImplicitSearch {
    /* Used by ContextErrors */
    private[typechecker] def context:Context
  }
  
  protected trait ImplicitNotFoundMsgObject {
	  /* Used by ContextErrors */
    private[typechecker] def unapply(sym: Symbol): Option[(Message)]
    /* Used by RefChecks */
    private[typechecker] def check(sym: Symbol): Option[String] 
    
    /* Used by ContextErrors */
    private[typechecker] trait Message {
    	/* Used by ContextErrors */
      private[typechecker] def format(paramName: Name, paramTp: Type): String
    }
  }
  /* Used by RefChecks and ContextErrors */
  private[typechecker] val ImplicitNotFoundMsg:ImplicitNotFoundMsgObject
}
