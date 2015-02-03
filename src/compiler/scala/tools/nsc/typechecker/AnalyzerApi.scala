package scala.tools.nsc
package typechecker


trait Analyzer extends Globals
  with Contexts
  with Namers
  with Typers
  with Infer
  with Implicits
  with Unapplies
  with Macros
  with ContextErrors
  with StdAttachments
  with NamesDefaults
  with AnalyzerPlugins
{  
  def namerFactory:SubComponent
  def packageObjects:SubComponent
  def typerFactory:SubComponent
}