package scala.tools.nsc
package typechecker

import scala.reflect.ClassTag


trait NamesDefaults {
  self: Globals with
  Namers with
  Contexts with
  Typers =>
  
  import global._
  
  /* Used by Infer and Typers */
  protected def allArgsArePositional(a: Array[Int]):Boolean
  
  /* Used by Infer */
  protected def makeNamedTypes(syms: List[Symbol]):List[NamedType]
  protected def missingParams[T](args: List[T], params: List[Symbol], argName: T => Option[Name]): (List[Symbol], Boolean)
  protected def reorderArgs[T: ClassTag](args: List[T], pos: Int => Int): List[T]
  
  /* Used by Typers */
  protected def removeNames(typer: Typer)(args: List[Tree], params: List[Symbol]): (List[Tree], Array[Int])
  protected def addDefaults(givenArgs: List[Tree], qual: Option[Tree], targs: List[Tree],
		  previousArgss: List[List[Tree]], params: List[Symbol],
		  pos: scala.reflect.internal.util.Position, context: Context): (List[Tree], List[Symbol])
  protected def isNamedArg(arg: Tree):Boolean
  protected def transformNamedApplication(typer: Typer, mode: Mode, pt: Type)(tree: Tree, argPos: Int => Int): Tree
           
  /* Used by Namers */
  // Default getters of constructors are added to the companion object in the
  // typeCompleter of the constructor (methodSig). To compute the signature,
  // we need the ClassDef. To create and enter the symbols into the companion
  // object, we need the templateNamer of that module class. These two are stored
  // as an attachment in the companion module symbol
  protected class ConstructorDefaultsAttachment(val classWithDefault: ClassDef, var companionModuleClassNamer: Namer)
  
  /* Used by Contexts, NamedDefaults and Typers */
  protected trait NamedApplyInfo {
    /* Used by NamedApplyInfo (unapply) */
    private[typechecker] def qual: Option[Tree]
    private[typechecker] def targs: List[Tree]
    private[typechecker] def vargss: List[List[Tree]]
    private[typechecker] def blockTyper: Typer
  }
  /* Used by NamedDefaults and Typers */
  protected object NamedApplyInfo {
	  /* Used by NamedDefaults and Typers */
    private[typechecker] def unapply(n:NamedApplyInfo):Option[(Option[Tree], List[Tree], List[List[Tree]], Typer)] =
      Option(n).map(n => (n.qual, n.targs, n.vargss, n.blockTyper))
  }
}