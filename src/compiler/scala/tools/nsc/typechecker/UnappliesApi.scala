package scala.tools.nsc
package typechecker


trait Unapplies {
  self: Globals =>
    
  import global._
  
  def companionModuleDef(cdef: ClassDef, parents: List[Tree] = Nil, body: List[Tree] = Nil): ModuleDef
  
  /* Used by ContextErrors */
  protected def directUnapplyMember(tp: Type): Symbol
  
  /* Used by MethodSynthesis (Namers) */
  protected def factoryMeth(mods: Modifiers, name: TermName, cdef: ClassDef): DefDef
  
  /* Used by Namers */
  protected def caseModuleDef(cdef: ClassDef): ModuleDef
  protected def caseModuleApplyMeth(cdef: ClassDef): DefDef
  protected def caseModuleUnapplyMeth(cdef: ClassDef): DefDef
  protected def caseClassCopyMeth(cdef: ClassDef): Option[DefDef]
  
  /* Used by Typers and PatternTypers (Typers) */
  protected def unapplyMember(tp: Type): Symbol
}