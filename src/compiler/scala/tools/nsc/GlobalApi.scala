package scala.tools.nsc

import scala.tools.nsc.ast.Trees
import scala.tools.nsc.ast.CorrectReporter
import scala.tools.nsc.plugins.Plugins
import scala.tools.nsc.ast.DocComments
import scala.tools.nsc.ast.Positions
import scala.tools.nsc.symtab.SymbolTable
import scala.tools.nsc.ast.Printers
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.ast.parser.SyntaxAnalyzer
import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.internal.pickling.PickleBuffer
import scala.collection.mutable
import scala.reflect.internal.util.SourceFile
import scala.tools.nsc.ast.parser.TreeBuilder
import java.net.URL

// Some parts of Global are only used by one class
// maybe think about splitting it up
trait Global extends SymbolTable
  with CompilationUnits
  with Plugins
  with PhaseAssembly
  with Trees
  with Printers
  with DocComments
  with CorrectReporter
  with Positions
  with Reporting
  with Parsing {

  type CorrectGlobalType = {
    val global: Global.this.type
  }

  // change this into a def to see which types should be pulled out of Analyzer
  val analyzer: typechecker.Analyzer with CorrectGlobalType
  val specializeTypes: SubComponent with transform.SpecializeTypes with CorrectGlobalType
  override lazy val erasure: SubComponent with transform.Erasure with CorrectGlobalType = erasureImplementation
  def erasureImplementation: SubComponent with transform.Erasure with CorrectGlobalType

  // TODO depend on interface instead of implementation
  val syntaxAnalyzer: SyntaxAnalyzer with CorrectGlobalType
  val icodes: backend.icode.ICodes with CorrectGlobalType
  def genicode: backend.icode.GenICode with CorrectGlobalType
  val loaders: GlobalSymbolLoaders with CorrectGlobalType
  val scalaPrimitives: backend.ScalaPrimitives with CorrectGlobalType
  trait CustomGen {
    def mkAttributedCast(tree: Tree, pt: Type): Tree
  }
  override val gen: ast.TreeGen with CustomGen with CorrectGlobalType = genImplementation
  def genImplementation: ast.TreeGen with CustomGen with CorrectGlobalType
  val genASM: backend.jvm.GenASM with CorrectGlobalType
  def genBCode: backend.jvm.GenBCode with CorrectGlobalType
  val copyPropagation: backend.icode.analysis.CopyPropagation with CorrectGlobalType
  val inliner: backend.opt.Inliners with CorrectGlobalType
  def closureElimination: backend.opt.ClosureElimination with CorrectGlobalType
  override lazy val postErasure: transform.PostErasure with CorrectGlobalType = postErasureImplementation
  def postErasureImplementation: transform.PostErasure with CorrectGlobalType
  type CustomNodePrinters = {
    def showUnit(unit: CompilationUnit): Unit
  }
  val nodePrinters: ast.NodePrinters with CustomNodePrinters with CorrectGlobalType
  def extensionMethods: transform.ExtensionMethods with CorrectGlobalType
  def deadCode: backend.opt.DeadCodeElimination with CorrectGlobalType
  def cleanup : transform.CleanUp with CorrectGlobalType
  def flatten : transform.Flatten with CorrectGlobalType
  // Using this construction to be able to override the concrete implementation in the
  // trait. If this is an abstract member, the one from the super trait will be selected 
  override lazy val refChecks: typechecker.RefChecks with CorrectGlobalType = refChecksImplementation
  def refChecksImplementation: typechecker.RefChecks with CorrectGlobalType
  override lazy val uncurry: transform.UnCurry with CorrectGlobalType = uncurryImplementation
  def uncurryImplementation: transform.UnCurry with CorrectGlobalType
  val overridingPairs:transform.OverridingPairs with CorrectGlobalType
  val analysis: backend.icode.analysis.TypeFlowAnalysis with CorrectGlobalType
  val explicitOuter: transform.ExplicitOuter with CorrectGlobalType
  val patmat: transform.patmat.PatternMatching with CorrectGlobalType
  def nodeToString: Tree => String
  val constfold: typechecker.ConstantFolder with CorrectGlobalType
  def treeBuilder: TreeBuilder with CorrectGlobalType
  
  type SymbolPair = overridingPairs.SymbolPair
  
  def echoPhaseSummary(ph: Phase): Unit

  def newRun(): Run

  trait Run extends RunContextApi with RunReporting with RunParsing {
    def phaseNamed(name: String): Phase
    def compiles(sym: Symbol): Boolean
    def erasurePhase: Phase
    def lambdaliftPhase: Phase
    def uncurryPhase: Phase
    def typerPhase: Phase
    def namerPhase: Phase
    def picklerPhase: Phase
    def posterasurePhase: Phase
    def icodePhase: Phase
    def refchecksPhase: Phase
    def explicitouterPhase: Phase
    def specializePhase: Phase
    def mixinPhase: Phase
    def flattenPhase: Phase
    def symSource: mutable.Map[Symbol, io.AbstractFile]
    def compile(filenames: List[String]): Unit
    def runDefinitions: definitions.RunDefinitions
    def symData: mutable.Map[Symbol, PickleBuffer]
    def cancel(): Unit
    def size: Int
    var currentUnit: CompilationUnit
    def informUnitStarting(phase: Phase, unit: CompilationUnit): Unit
    def advanceUnit(): Unit
    def isDefined: Boolean
    def registerPickle(sym: Symbol): Unit
    def compileLate(file: io.AbstractFile): Unit
    def compileLate(unit: CompilationUnit):Unit
    def canRedefine(sym: Symbol):Boolean
    def compiledFiles: mutable.Set[String]
    def compileUnits(units: List[CompilationUnit], fromPhase: Phase): Unit
    def compileSources(sources: List[SourceFile]):Unit
  }

  def signalParsedDocComment(comment: String, pos: Position):Unit
  def extendCompilerClassPath(urls: URL*): Unit
  def registerTopLevelSym(sym: Symbol):Unit
  def getSourceFile(f: io.AbstractFile): BatchSourceFile
  def newSourceFile(code: String, filename: String = "<console>"):BatchSourceFile
  def classPath: util.ClassFileLookup[io.AbstractFile]
  def settings: Settings
  def currentRun: Run
  var currentSettings: Settings
  var reporter: Reporter
  def phaseDescriptions: String
  def phaseFlagDescriptions: String
  def phaseNames: List[String]
  def currentUnit: CompilationUnit
  val typer: analyzer.Typer
  def registerContext(c: analyzer.Context): Unit
  def newUnitParser(unit: CompilationUnit): syntaxAnalyzer.UnitParser
  def newUnitParser(code: String, filename: String = "<console>"): syntaxAnalyzer.UnitParser
  def newCompilationUnit(code: String, filename: String = "<console>"):CompilationUnit
  def signalParseProgress(pos: Position): Unit
  def reportThrowable(t: Throwable): Unit
  protected def addToPhasesSet(sub: SubComponent, descr: String): Unit
  protected def phasesSet: mutable.HashSet[SubComponent]
  def globalPhase: Phase
  def exitingPickler[T](op: => T): T
  def enteringFlatten[T](op: => T): T
  def exitingPostErasure[T](op: => T): T
  def enteringIcode[T](op: => T): T
  def exitingRefchecks[T](op: => T): T
  def enteringExplicitOuter[T](op: => T): T
  def enteringTyper[T](op: => T): T
  def enteringSpecialize[T](op: => T): T
  def exitingErasure[T](op: => T): T
  def enteringUncurry[T](op: => T): T
  def exitingFlatten[T](op: => T): T
  def enteringPickler[T](op: => T): T
  def enteringMixin[T](op: => T): T
  def exitingMixin[T](op: => T): T
  def exitingExplicitOuter[T](op: => T): T
  def exitingUncurry[T](op: => T): T
  def exitingSpecialize[T](op: => T): T
  def exitingTyper[T](op: => T): T
  def createJavadoc: Boolean
  protected var lastSeenSourceFile: SourceFile
  def withInfoLevel[T](infolevel: nodePrinters.InfoLevel.Value)(op: => T): T
  def ifDebug(body: => Unit)
  def printTypings:Boolean
  def getSourceFile(name: String): SourceFile
  def devWarning(msg: => String): Unit
  def devWarning(pos: Position, msg: => String):Unit
  def logError(msg: String, t: Throwable): Unit
  def signalDone(context: analyzer.Context, old: Tree, result: Tree):Unit
  protected def computePhaseDescriptors: List[SubComponent]
  
  type ThisPlatform = backend.JavaPlatform { val global: Global.this.type }
  val platform: ThisPlatform

  abstract class GlobalPhase(prev: Phase) extends Phase(prev) {
    phaseWithId(id) = this

    def run() {
      echoPhaseSummary(this)
      currentRun.units foreach applyPhase
    }

    def apply(unit: CompilationUnit): Unit

    private val isErased = prev.name == "erasure" || prev.erasedTypes
    override def erasedTypes: Boolean = isErased
    private val isFlat = prev.name == "flatten" || prev.flatClasses
    override def flatClasses: Boolean = isFlat
    private val isSpecialized = prev.name == "specialize" || prev.specialized
    override def specialized: Boolean = isSpecialized
    private val isRefChecked = prev.name == "refchecks" || prev.refChecked
    override def refChecked: Boolean = isRefChecked

    /** Is current phase cancelled on this unit? */
    def cancelled(unit: CompilationUnit) = {
      // run the typer only if in `createJavadoc` mode
      val maxJavaPhase = if (createJavadoc) currentRun.typerPhase.id else currentRun.namerPhase.id
      reporter.cancelled || unit.isJava && this.id > maxJavaPhase
    }

    final def withCurrentUnit(unit: CompilationUnit)(task: => Unit) {
      if ((unit ne null) && unit.exists)
        lastSeenSourceFile = unit.source

      if (settings.debug && (settings.verbose || currentRun.size < 5))
        inform("[running phase " + name + " on " + unit + "]")

      val unit0 = currentUnit
      try {
        currentRun.currentUnit = unit
        if (!cancelled(unit)) {
          currentRun.informUnitStarting(this, unit)
          task
        }
        currentRun.advanceUnit()
      } finally {
        //assert(currentUnit == unit)
        currentRun.currentUnit = unit0
      }
    }

    final def applyPhase(unit: CompilationUnit) = withCurrentUnit(unit)(apply(unit))
  }
}

object Global {
  def apply(settings: Settings, reporter: Reporter): Global = new DefaultGlobal(settings, reporter)
}