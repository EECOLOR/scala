package scala.reflect.macros.runtime

import scala.tools.nsc.typechecker

trait MacroRuntimes {
  self: typechecker.Macros =>

  // used externally
  type MacroRuntime = MacroArgs => Any
}