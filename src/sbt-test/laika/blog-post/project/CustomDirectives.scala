import laika.directive.Directives.Blocks
import laika.directive.StandardDirectives
import laika.render.HTMLWriter
import laika.rewrite.{DocumentCursor, TreeCursor}
import laika.tree.Documents.{Document, DocumentTree}
import laika.tree.Elements
import laika.tree.Elements._
import com.typesafe.config.Config

// Just sample, probably not the best way to do custom toc for Laika
object CustomDirectives extends StandardDirectives {

  case class PostsTocElement(title: Seq[Span], by: String, path: PathInfo, options: Elements.Options)
    extends Elements.Element with Elements.Block

  val postsToc: Blocks.Directive = Blocks.create("postsToc") {
    import Blocks.Combinators._

    def titleOrName (content: Document) =
      if (content.title.nonEmpty) content.title
      else Seq(Text(content.name))

    def by(config: Config) =
      if (config.hasPath("by"))
        config.getString("by")
      else ""

    cursor.map { cursor =>
      val posts = cursor.root.children.collect {
        case pd: TreeCursor =>
          pd.children.collect {
            case d: DocumentCursor =>
              val path = d.target.path
              val refPath = cursor.parent.target.path
              PostsTocElement(titleOrName(d.target),
                by(d.config),
                PathInfo.fromPath(path, refPath.parent),
                Styles("toc"))
          }
      }.flatten

      BlockSequence(posts)
    }
  }

  val postsRenderer: HTMLWriter => RenderFunction = { out => {
      case PostsTocElement(title, by, path, options) =>
        out << Paragraph(List(
          CrossLink(title, "", path),
          LineBreak(),
          SpanSequence(Seq(Text("by "), Text(by)))
        ), options)
    }
  }
}
