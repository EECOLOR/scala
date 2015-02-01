package scala.tools.nsc
package transform

trait Erasure extends typechecker.Typers 
  with scala.reflect.internal.transform.Erasure
  with typechecker.Contexts
  with typechecker.Globals { self:
  // required by typechecker.Typers
  //typechecker.Globals with
  //typechecker.Contexts
  typechecker.ContextErrors with
  typechecker.TypeDiagnostics with
  typechecker.Infer with
  // required by typechecker.Contexts
  //typechecker.ContextErrors with
  typechecker.Implicits with
  typechecker.NamesDefaults =>

  import global._

  private [nsc] def minimizeParents(parents: List[Type]): List[Type]
  private [nsc] def javaSig(sym0: Symbol, info: Type): Option[String]
  private [nsc] def prepareSigMap:TypeMap  
  
  private [transform] def implClass(iface: Symbol): Symbol
  private [transform] def resolveAnonymousBridgeClash(sym: Symbol, bridge: Symbol):Unit
}