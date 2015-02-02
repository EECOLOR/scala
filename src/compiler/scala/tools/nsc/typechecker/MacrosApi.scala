package scala.tools.nsc
package typechecker

import scala.reflect.macros.runtime.MacroRuntimes
import scala.tools.reflect.FastTrack

trait Macros extends MacroRuntimes {
  self: Globals with
  StdAttachments with
  // needed for FastTrack
  Analyzer =>
    
  import global._

  def standardMacroExpand(typer: Typer, expandee: Tree, mode: Mode, pt: Type): Tree
  def standardTypedMacroBody(typer: Typer, macroDdef: DefDef): Tree
  def standardMacroArgs(typer: Typer, expandee: Tree): MacroArgs
  
  case class MacroArgs(c: MacroContext, others: List[Any])
  
  def macroExpandWithCallbacks(typer: Typer, expandee:Tree, mode: Mode, pt: Type, listener: MacroExpanderListener): Tree
  
  trait MacroExpanderListener {
    def onSuccess: Option[Tree => Tree] = None
    def onFallback: Option[Tree => Tree] = None
    def onSuppressed: Option[Tree => Tree] = None
    def onDelayed: Option[Tree => Tree] = None
    def onSkipped: Option[Tree => Tree] = None
    def onFailure: Option[Tree => Tree] = None
  }
  
  private[scala] def computeMacroDefTypeFromMacroImplRef(macroDdef: DefDef, macroImplRef: Tree): Type
  private[scala] def openMacros:List[MacroContext]
  private[scala] def defaultMacroClassloader: ClassLoader
  private[scala] def enclosingMacroPosition:Position
  private[scala] def findMacroClassLoader(): ClassLoader
  private[scala] def untypeMetalevel(tp: Type): Type
  private[scala] def increaseMetalevel(pre: Type, tp: Type): Type
  private[scala] def transformTypeTagEvidenceParams(macroImplRef: Tree, transform: (Symbol, Symbol) => Symbol): List[List[Symbol]]
  private[scala] def loadMacroImplBinding(macroDef: Symbol): Option[MacroImplBinding]
  private[scala] def fastTrack:FastTrack[self.type]
  
  protected def macroLogVerbose(msg: => Any):Unit
  protected def globalSettings:Settings
  protected def isBlackbox(macroDef: Symbol): Boolean
  protected def notifyUndetparamsInferred(undetNoMore: List[Symbol], inferreds: List[Type]): Unit
  protected def standardIsBlackbox(macroDef: Symbol): Boolean
  protected def standardMacroRuntime(expandee: Tree): MacroRuntime
  protected def hasPendingMacroExpansions:Boolean
  protected def macroExpand(typer: Typer, expandee: Tree, mode: Mode, pt: Type): Tree
  protected def macroExpandAll(typer: Typer, expandee: Tree): Tree
  protected def notifyUndetparamsAdded(newUndets: List[Symbol]): Unit
  protected def typedMacroBody(typer: Typer, macroDdef: DefDef): Tree
  
  private[scala] trait MacroImplBinding {
    private[typechecker] def isBlackbox:Boolean
    private[scala] def isBundle:Boolean
    private[typechecker] def signature: List[List[Fingerprint]]
    private[typechecker] def targs: List[Tree]
    private[typechecker] def is_??? : Boolean
    private[scala] def className: String
    private[scala] def methName: String
  }
}
