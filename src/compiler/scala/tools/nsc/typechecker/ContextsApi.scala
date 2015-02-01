package scala.tools.nsc
package typechecker

import scala.collection.{immutable, mutable}


trait Contexts {
  self: Globals with
  ContextErrors with
  Implicits with
  NamesDefaults =>
    
  import global._

  def rootContext(unit: CompilationUnit, tree: Tree = EmptyTree, throwing: Boolean = false, checking: Boolean = false): Context
  
  private[nsc] def rootContextPostTyper(unit: CompilationUnit, tree: Tree = EmptyTree): Context
  private[nsc] def NoContext:Context
  
  private[typechecker] def lastAccessCheckDetails:String
  private[typechecker] def resetContexts():Unit
  
  trait ImportInfo {
    def qual: Tree
    def importedSymbol(name: Name): Symbol
    def allImportedSymbols: Iterable[Symbol]
    
    private[typechecker] def tree: Import
    private[typechecker] def posOf(sel: ImportSelector):Position
    private[typechecker] def depth: Int
    private[typechecker] def isExplicitImport(name: Name): Boolean
    private[typechecker] def importedSymbol(name: Name, requireExplicit: Boolean):Symbol
  }
  
  trait Context {
    def owner: Symbol
    def imports: List[ImportInfo]
    def make(tree: Tree = tree, owner: Symbol = owner,
             scope: Scope = scope, unit: CompilationUnit = unit,
             reporter: ContextReporter = this.reporter): Context
 
    private[scala] def tree: Tree
    private[scala] def unit: CompilationUnit
    private[scala] var openImplicits: List[OpenImplicit]
    private[scala] def enclosingContextChain: List[Context]
    private[scala] def inSelfSuperCall:Boolean
    private[scala] def withMacrosEnabled[T](op: => T): T
    private[scala] def withMacrosDisabled[T](op: => T): T
    private[scala] def withImplicitsDisabled[T](op: => T): T
    private[scala] def withImplicitsEnabled[T](op: => T): T
    private[scala] def error(pos: Position, msg: String):Unit
    private[scala] def warning(pos: Position, msg: String):Unit
    
    private[tools] def initRootContext(throwing: Boolean = false, checking: Boolean = false): Unit
    
    private[nsc] def scope: Scope
    private[nsc] def outer: Context
    private[nsc] def enclClass: Context
    private[nsc] def enclClassOrMethod: Context
    private[nsc] def ambiguousErrors:Boolean
    private[nsc] def isAccessible(sym: Symbol, pre: Type, superAccess: Boolean = false): Boolean
    private[nsc] def prefix: Type
    private[nsc] var macrosEnabled:Boolean
    private[nsc] def bufferErrors:Boolean
    private[nsc] def makeImplicit(reportAmbiguousErrors: Boolean):Context
    private[nsc] def makeNewScope(tree: Tree, owner: Symbol, reporter: ContextReporter = this.reporter): Context         
    private[nsc] def makeSilent(reportAmbiguousErrors: Boolean = ambiguousErrors, newtree: Tree = tree): Context
    
    private[typechecker] def tryTwice(tryOnce: Boolean => Unit):Unit
    private[typechecker] def siteString:String
    private[typechecker] def enclClass_=(context: Context)
    private[typechecker] def reporter: ContextReporter
    private[typechecker] def enclMethod: Context
    private[typechecker] var undetparams: List[Symbol]
    private[typechecker] def undetparamsString:String
    private[typechecker] def implicitss: List[List[ImplicitInfo]]
    private[typechecker] def reportErrors:Boolean
    private[typechecker] def enclosingCaseDef:Context
    private[typechecker] var savedTypeBounds: List[(Symbol, Type)]
    private[typechecker] def pushTypeBounds(sym: Symbol):Unit
    private[typechecker] def inSilentMode(expr: => Boolean): Boolean
    private[typechecker] def extractUndetparams(): List[Symbol]
    private[typechecker] var inConstructorSuffix:Boolean
    private[typechecker] def prefix_=(tp: Type):Unit
    private[typechecker] var implicitsEnabled:Boolean
    private[typechecker] var enrichmentEnabled:Boolean
    private[typechecker] def lookupSymbol(name: Name, qualifies: Symbol => Boolean): NameLookup
    private[typechecker] def nextEnclosing(p: Context => Boolean): Context
    private[typechecker] def lookup(name: Name, expectedOwner: Symbol):Symbol
    private[typechecker] var namedApplyBlockInfo: Option[(Tree, NamedApplyInfo)]
    private[typechecker] def isNameInScope(name: Name):Boolean
    private[typechecker] def savingUndeterminedTypeParams[A](reportAmbiguous: Boolean = ambiguousErrors)(body: => A): A
    private[typechecker] def isInPackageObject(sym: Symbol, pkg: Symbol): Boolean
    private[typechecker] def featureWarning(pos: Position, featureName: String, featureDesc: String, featureTrait: Symbol, construct: => String = "", required: Boolean): Unit
    private[typechecker] def inTypeConstructorAllowed:Boolean
    private[typechecker] def enclosingSubClassContext(clazz: Symbol): Context
    private[typechecker] def returnsSeen:Boolean
    private[typechecker] def restoreTypeBounds(tp: Type): Type
    private[typechecker] var diagUsedDefaults: Boolean
    private[typechecker] def enclosingNonImportContext: Context
    private[typechecker] def inPatAlternative:Boolean
    private[typechecker] var retyping:Boolean
    private[typechecker] def inSecondTry:Boolean
    private[typechecker] def inSuperInit:Boolean
    private[typechecker] def starPatterns:Boolean
    private[typechecker] def enclosingApply:Context
    private[typechecker] def defaultModeForTyped: Mode
    private[typechecker] def withinTypeConstructorAllowed[T](op: => T): T
    private[typechecker] def withinStarPatterns[T](op: => T): T
    private[typechecker] def withinReturnExpr[T](op: => T): T
    private[typechecker] def withinSecondTry[T](op: => T): T
    private[typechecker] def withinPatAlternative[T](op: => T): T
    private[typechecker] def withinSuperInit[T](op: => T): T
    private[typechecker] def withImplicitsDisabledAllowEnrichment[T](op: => T): T
    private[typechecker] def makeConstructorContext:Context
    private[typechecker] def makeNonSilent(newtree: Tree): Context
    private[typechecker] def echo(pos: Position, msg: String):Unit
    private[typechecker] def deprecationWarning(pos: Position, sym: Symbol, msg: String): Unit
    private[typechecker] def deprecationWarning(pos: Position, sym: Symbol): Unit
    private[typechecker] def inSelfSuperCall_=(value:Boolean):Unit
    private[typechecker] def issue(err: AbsTypeError):Unit      
    private[typechecker] def issueAmbiguousError(err: AbsAmbiguousTypeError):Unit
    private[typechecker] def update(mask: ContextMode, value: Boolean)
    private[typechecker] var contextMode: ContextMode
    private[typechecker] def firstImport:Option[ImportInfo]
    private[typechecker] def depth: Int
    
    protected def outerDepth:Int
  }
  
  private[typechecker] trait ContextReporter {
    private[typechecker] def hasErrors:Boolean
    private[typechecker] def propagateImplicitTypeErrorsTo(target: ContextReporter):Unit
    private[typechecker] def firstError: Option[AbsTypeError]
    private[typechecker] def retainDivergentErrorsExcept(saved: DivergentImplicitTypeError):Unit
    private[typechecker] def errors: immutable.Seq[AbsTypeError]
    private[typechecker] def clearAllErrors():Unit
    private[typechecker] def reportFirstDivergentError(fun: Tree, param: Symbol, paramTp: Type)(implicit context: Context): Unit
    private[typechecker] def propagatingErrorsTo[T](target: ContextReporter)(expr: => T): T
    private[typechecker] def emitWarnings():Unit
    private[typechecker] def isBuffering: Boolean
    private[typechecker] def withFreshErrorBuffer[T](expr: => T): T
    private[typechecker] def errorBuffer: mutable.LinkedHashSet[AbsTypeError]
    private[typechecker] def makeImmediate: ContextReporter
    private[typechecker] def isThrowing: Boolean
    private[typechecker] def ++=(errors: Traversable[AbsTypeError]): Unit
    private[typechecker] def clearAll():Unit
    private[typechecker] def issue(err: AbsTypeError)(implicit context: Context): Unit
    private[typechecker] def issueAmbiguousError(err: AbsAmbiguousTypeError)(implicit context: Context): Unit
    private[typechecker] def echo(pos: Position, msg: String): Unit
    private[typechecker] def warning(pos: Position, msg: String): Unit
    private[typechecker] def error(pos: Position, msg: String): Unit
  }
}