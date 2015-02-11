package scala
package tools.nsc
package transform

import scala.collection.immutable

trait SpecializeTypes {
  
  val global:Global
  
  import global._
  
  private[transform] def specializedTypeVars(sym: Symbol): immutable.Set[Symbol]
  private[transform] def newDuplicator(casts: Map[Symbol, Type]):Duplicator
  private[transform] def newImplementationAdapter(
		  from: List[Symbol],
		  to: List[Symbol],
		  targetClass: Symbol,
		  addressFields: Boolean):ImplementationAdapter
  
  private[transform] trait Duplicator extends typechecker.Contexts { 
    self:
    // required by typechecker.Contexts
    typechecker.Globals with
    typechecker.ContextErrors with
    typechecker.Implicits with
    typechecker.NamesDefaults =>
  
    private[transform] def retyped(context: Context, tree: Tree, oldThis: Symbol, newThis: Symbol, env: scala.collection.Map[Symbol, Type]): Tree
  }
  private[transform] trait ImplementationAdapter {
    private[transform] def apply[T <: Tree](tree: T): T
  }
}