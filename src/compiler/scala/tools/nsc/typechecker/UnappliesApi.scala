package scala.tools.nsc
package typechecker


trait Unapplies {
  self: Globals =>
    
  import global._
  
  def companionModuleDef(cdef: ClassDef, parents: List[Tree] = Nil, body: List[Tree] = Nil): ModuleDef
  
  protected def directUnapplyMember(tp: Type): Symbol
  protected def factoryMeth(mods: Modifiers, name: TermName, cdef: ClassDef): DefDef
  protected def caseModuleDef(cdef: ClassDef): ModuleDef
  protected def caseModuleApplyMeth(cdef: ClassDef): DefDef
  protected def caseModuleUnapplyMeth(cdef: ClassDef): DefDef
  protected def caseClassCopyMeth(cdef: ClassDef): Option[DefDef]
  protected def unapplyMember(tp: Type): Symbol
}