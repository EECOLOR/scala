package scala.tools.nsc
package typechecker

import scala.reflect.ClassTag


trait NamesDefaults {
  self: Globals with
  Namers with
  Contexts with
  Typers =>
  
  import global._
  
  private[typechecker] def allArgsArePositional(a: Array[Int]):Boolean
  private[typechecker] def makeNamedTypes(syms: List[Symbol]):List[NamedType]
  private[typechecker] def missingParams[T](args: List[T], params: List[Symbol], argName: T => Option[Name]): (List[Symbol], Boolean)
  private[typechecker] def reorderArgs[T: ClassTag](args: List[T], pos: Int => Int): List[T]
  private[typechecker] def removeNames(typer: Typer)(args: List[Tree], params: List[Symbol]): (List[Tree], Array[Int]) 
  private[typechecker] def addDefaults(givenArgs: List[Tree], qual: Option[Tree], targs: List[Tree],
                  previousArgss: List[List[Tree]], params: List[Symbol],
                  pos: scala.reflect.internal.util.Position, context: Context): (List[Tree], List[Symbol])
  private[typechecker] def isNamedArg(arg: Tree):Boolean
  private[typechecker] def transformNamedApplication(typer: Typer, mode: Mode, pt: Type)
                               (tree: Tree, argPos: Int => Int): Tree
                  
  // Default getters of constructors are added to the companion object in the
  // typeCompleter of the constructor (methodSig). To compute the signature,
  // we need the ClassDef. To create and enter the symbols into the companion
  // object, we need the templateNamer of that module class. These two are stored
  // as an attachment in the companion module symbol
  private[typechecker] class ConstructorDefaultsAttachment(val classWithDefault: ClassDef, var companionModuleClassNamer: Namer)
  
  private[typechecker] trait NamedApplyInfo {
    private[typechecker] def qual: Option[Tree]
    private[typechecker] def targs: List[Tree]
    private[typechecker] def vargss: List[List[Tree]]
    private[typechecker] def blockTyper: Typer
  }
  private[typechecker] object NamedApplyInfo {
    private[typechecker] def unapply(n:NamedApplyInfo):Option[(Option[Tree], List[Tree], List[List[Tree]], Typer)] =
      Option(n).map(n => (n.qual, n.targs, n.vargss, n.blockTyper))
  }
}