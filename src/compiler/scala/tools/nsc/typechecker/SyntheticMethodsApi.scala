package scala.tools.nsc
package typechecker

private[nsc] trait SyntheticMethods {
  self: Globals with 
  Contexts with
  ast.TreeDSL =>
    
  import global._
  
  /* Used by Typers */
  private[typechecker] def addSyntheticMethods(templ: Template, clazz0: Symbol, context: Context): Template
  
  /* Used by Namers */
  private[typechecker] def clearRenamedCaseAccessors(caseclazz: Symbol): Unit
  
  /* Used by Unapplies */
  private[typechecker] def caseAccessorName(caseclazz: Symbol, paramName: TermName):TermName
}