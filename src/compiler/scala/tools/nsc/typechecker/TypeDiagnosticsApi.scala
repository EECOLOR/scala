package scala.tools.nsc
package typechecker


trait TypeDiagnostics {
  self: Globals =>

  import global._

  private[scala] def treeSymTypeMsg(tree: Tree): String
  
  private[nsc] def exampleTuplePattern(names: List[Name]): String
    
  private[typechecker] def restrictionError(pos: Position, unit: CompilationUnit, msg: String): Unit
  private[typechecker] def abstractVarMessage(sym: Symbol): String
  private[typechecker] def foundReqMsg(found: Type, req: Type): String
  private[typechecker] def underlyingSymbol(member: Symbol): Symbol
  protected def alternatives(tree: Tree): List[Type]
  protected def decodeWithKind(name: Name, owner: Symbol): String
  protected def linePrecedes(t1: Tree, t2: Tree):Boolean
  protected def typePatternAdvice(sym: Symbol, ptSym: Symbol):String
  protected def withDisambiguation[T](locals: List[Symbol], types: Type*)(op: => T): T
  protected def setAddendum(pos: Position, msg: () => String):Unit
  protected def withAddendum(pos: Position):String => String
  
  protected trait TyperDiagnostics {
    private[typechecker] def cyclicReferenceMessage(sym: Symbol, tree: Tree):Option[String]
    private[typechecker] def permanentlyHiddenWarning(pos: Position, hidden: Name, defn: Symbol):Unit
  }
}