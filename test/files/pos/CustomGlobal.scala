package custom

import scala.tools.nsc._, reporters._, typechecker._

/** Demonstration of a custom Global with a custom Typer,
 *  decoupled from trunk.  Demonstration:
 *
{{{
scalac -d . CustomGlobal.scala && scala -nc -Yglobal-class custom.CustomGlobal \
  -e 'class Bippy(x: Int) ; def f = new Bippy(5)'

I'm typing a Bippy! It's a ClassDef.
I'm typing a Bippy! It's a Ident.
I'm typing a Bippy! It's a DefDef.
}}}
 *
 */
class CustomGlobal(currentSettings: Settings, reporter: Reporter) extends Global(currentSettings, reporter) {
  override lazy val analyzer = new {
    val global: CustomGlobal.this.type = CustomGlobal.this
  } with DefaultAnalyzer {
    override def newTyper(context: Context, settings: TyperSettings = TyperSettings.Default) = 
      super.newTyper(context, settings.copy(decorations = Some(newCustomTyper)))
      
    private def newCustomTyper(typer:Typer) = {
      val customTyper = new CustomTyper(typer)
      TyperDecorations(
        typedHook = Some(customTyper.typed)
      )
    }

    class CustomTyper(typer: Typer) {
      import typer._
      
      def typed(`super.typed`: (Tree, Mode, Type) => Tree)(tree: Tree, mode: Mode, pt: Type): Tree = {
        if (tree.summaryString contains "Bippy")
          println("I'm typing a Bippy! It's a " + tree.shortClass + ".")

        `super.typed`(tree, mode, pt)
      }
    }
  }
}
