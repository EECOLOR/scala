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
  
  /* Used by Infer */
  private[typechecker] def lastAccessCheckDetails:String
  
  /* Used by Typers */
  protected def resetContexts():Unit
  
  trait ImportInfo {
    def qual: Tree
    def importedSymbol(name: Name): Symbol
    def allImportedSymbols: Iterable[Symbol]
    
    /* Used by Contexts */
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
    def lookup(name: Name, expectedOwner: Symbol):Symbol
 
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
    private[tools] def outer: Context
    private[tools] def scope: Scope
    private[tools] def enclClass: Context
    private[tools] def prefix: Type
    
    private[nsc] def enclClassOrMethod: Context
    private[nsc] def ambiguousErrors:Boolean
    private[nsc] def isAccessible(sym: Symbol, pre: Type, superAccess: Boolean = false): Boolean
    private[nsc] var macrosEnabled:Boolean
    private[nsc] def bufferErrors:Boolean
    private[nsc] def makeImplicit(reportAmbiguousErrors: Boolean):Context
    private[nsc] def makeNewScope(tree: Tree, owner: Symbol, reporter: ContextReporter = this.reporter): Context         
    private[nsc] def makeSilent(reportAmbiguousErrors: Boolean = ambiguousErrors, newtree: Tree = tree): Context
    
    /* Used by Infer */
    private[typechecker] def tryTwice(tryOnce: Boolean => Unit):Unit
    
    /* Used by most of the typechecker parts and this (Context) as default argument value */
    private[typechecker] def reporter: ContextReporter
    
    /* Used by Implicits, Macros, NamesDefaults, Typers and TypersTracking (Typers) */
    private[typechecker] var undetparams: List[Symbol]
    
    /* Used by Implicits */
    private[typechecker] def undetparamsString:String
    
    /* Used by Implicits and Contexts */
    private[typechecker] def implicitss: List[List[ImplicitInfo]]
    
    /* Used by Contexts, Infer, Typers and TypeDiagnostics (Typers) */ 
    private[typechecker] def reportErrors:Boolean
    
    /* Used by Infer and Typers */
    private[typechecker] def enclosingCaseDef:Context
    private[typechecker] def savedTypeBounds: List[(Symbol, Type)]
    
    /* Used by Infer */
    private[typechecker] def pushTypeBounds(sym: Symbol):Unit
    private[typechecker] def inSilentMode(expr: => Boolean): Boolean
    
    /* Used by Macros and Typers */
    private[typechecker] def extractUndetparams(): List[Symbol]
    
    /* Used by Namers */
    private[typechecker] def inConstructorSuffix:Boolean
    private[typechecker] def makeNonSilent(newtree: Tree): Context
    
    /* Used by Macros, Typers and TypersTracking (Typers) */
    private[typechecker] def implicitsEnabled:Boolean
    private[typechecker] def enrichmentEnabled:Boolean
    
    /* Used by Macros */
    private[typechecker] def implicitsEnabled_=(value:Boolean):Unit
    private[typechecker] def enrichmentEnabled_=(value:Boolean):Unit
    
    /* Used by Namers and Contexts */
    private[typechecker] def nextEnclosing(p: Context => Boolean): Context
    
    /* used by NamesDefauls and Typers */
    private[typechecker] var namedApplyBlockInfo: Option[(Tree, NamedApplyInfo)]
 		private[typechecker] def isNameInScope(name: Name):Boolean
 		private[typechecker] def savingUndeterminedTypeParams[A](reportAmbiguous: Boolean = ambiguousErrors)(body: => A): A
    
    /* Used by Typers */
    private[typechecker] def savedTypeBounds_=(value: List[(Symbol, Type)]):Unit
    private[typechecker] def inConstructorSuffix_=(value:Boolean):Unit
    private[typechecker] def isInPackageObject(sym: Symbol, pkg: Symbol): Boolean
    private[typechecker] def featureWarning(pos: Position, featureName: String, featureDesc: String, featureTrait: Symbol, construct: => String = "", required: Boolean): Unit
    private[typechecker] def enclosingSubClassContext(clazz: Symbol): Context
    private[typechecker] def returnsSeen:Boolean
    private[typechecker] def restoreTypeBounds(tp: Type): Type
    private[typechecker] def enclosingNonImportContext: Context
    private[typechecker] def inPatAlternative:Boolean
    private[typechecker] var retyping:Boolean
    private[typechecker] def inSecondTry:Boolean
    private[typechecker] def inSuperInit:Boolean
    private[typechecker] def starPatterns:Boolean
    private[typechecker] def enclosingApply:Context
    private[typechecker] def defaultModeForTyped: Mode
    private[typechecker] def withinTypeConstructorAllowed[T](op: => T): T
    private[typechecker] def withinReturnExpr[T](op: => T): T
    private[typechecker] def withinSecondTry[T](op: => T): T
    private[typechecker] def withinPatAlternative[T](op: => T): T
    private[typechecker] def withinSuperInit[T](op: => T): T
    private[typechecker] def withImplicitsDisabledAllowEnrichment[T](op: => T): T
    private[typechecker] def echo(pos: Position, msg: String):Unit
    private[typechecker] def deprecationWarning(pos: Position, sym: Symbol): Unit
    /* Used by Typers and TypersTracking (Typers) */
    private[typechecker] def inTypeConstructorAllowed:Boolean
    /* Used by TypersTracking (Typers) */
    private[typechecker] def siteString:String
    /* Used by PatternTypers (Typers) */
    private[typechecker] def withinStarPatterns[T](op: => T): T
    /* Used by Typers and TypeDiagnostics (Typers) */
    private[typechecker] def lookupSymbol(name: Name, qualifies: Symbol => Boolean): NameLookup
    
    /* Used by Contexts */
    private[typechecker] def diagUsedDefaults: Boolean
    private[typechecker] def inSelfSuperCall_=(value:Boolean):Unit
    private[typechecker] var contextMode: ContextMode
    private[typechecker] def update(mask: ContextMode, value: Boolean):Unit
    private[typechecker] def firstImport:Option[ImportInfo]
    private[typechecker] def depth: Int
    protected def outerDepth:Int
    
    /* Used by Contexts and Typers */
    private[typechecker] def diagUsedDefaults_=(value: Boolean):Unit
    private[typechecker] def enclMethod: Context
    
    /* Used by Namers and Typers */
    private[typechecker] def makeConstructorContext:Context
    
    /* Used by Adaptations (Typers), NamesDefaults, Typers */
    private[typechecker] def deprecationWarning(pos: Position, sym: Symbol, msg: String): Unit
    
    /* Used by Contexts, ContextErrors, Implicits, Typers and PatternTypers (Typers) */
    private[typechecker] def issue(err: AbsTypeError):Unit
    
    /* Used by ContextErrors and Implicits */
    private[typechecker] def issueAmbiguousError(err: AbsAmbiguousTypeError):Unit
  }
  
  /* Used by Contexts and Typers */
  protected trait ContextReporter {
    /* Used by Contexts, Implicits, Infer, SyntheticMethods and Typers */
    private[typechecker] def hasErrors:Boolean
    
    /* Used by Implicits */
    private[typechecker] def propagateImplicitTypeErrorsTo(target: ContextReporter):Unit
    private[typechecker] def retainDivergentErrorsExcept(saved: DivergentImplicitTypeError):Unit
    private[typechecker] def clearAllErrors():Unit
    
    /* Used by Implicits and Typers */
    private[typechecker] def firstError: Option[AbsTypeError]
    
    /* Used by Implicits, Macros and Typers */
    private[typechecker] def errors: immutable.Seq[AbsTypeError]
    
    /* Used by Typers */
    private[typechecker] def reportFirstDivergentError(fun: Tree, param: Symbol, paramTp: Type)(implicit context: Context): Unit
    private[typechecker] def propagatingErrorsTo[T](target: ContextReporter)(expr: => T): T
    private[typechecker] def emitWarnings():Unit
    private[typechecker] def withFreshErrorBuffer[T](expr: => T): T
    
    /* Used by Contexts */
    private[typechecker] def isBuffering: Boolean
    private[typechecker] def errorBuffer: mutable.LinkedHashSet[AbsTypeError]
    private[typechecker] def makeImmediate: ContextReporter
    private[typechecker] def isThrowing: Boolean
    private[typechecker] def clearAll():Unit
    private[typechecker] def issueAmbiguousError(err: AbsAmbiguousTypeError)(implicit context: Context): Unit
    private[typechecker] def issue(err: AbsTypeError)(implicit context: Context): Unit
    private[typechecker] def warning(pos: Position, msg: String): Unit
    private[typechecker] def error(pos: Position, msg: String): Unit
    
    /* Used by Contexts and Implicits */
    private[typechecker] def ++=(errors: Traversable[AbsTypeError]): Unit
    private[typechecker] def echo(pos: Position, msg: String): Unit
  }
}