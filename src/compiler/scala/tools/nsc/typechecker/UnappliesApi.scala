package scala.tools.nsc
package typechecker


trait Unapplies {
  self: Globals =>
    
  import global._
  
  def companionModuleDef(cdef: ClassDef, parents: List[Tree] = Nil, body: List[Tree] = Nil): ModuleDef
  
  private[typechecker] def directUnapplyMember(tp: Type): Symbol
  private[typechecker] def factoryMeth(mods: Modifiers, name: TermName, cdef: ClassDef): DefDef
  private[typechecker] def caseModuleDef(cdef: ClassDef): ModuleDef
  private[typechecker] def caseModuleApplyMeth(cdef: ClassDef): DefDef
  private[typechecker] def caseModuleUnapplyMeth(cdef: ClassDef): DefDef
  private[typechecker] def caseClassCopyMeth(cdef: ClassDef): Option[DefDef]
  private[typechecker] def unapplyMember(tp: Type): Symbol
}