package ml.wolfe.nlp

/**
 * A mention of a named entity.
 * @param entityMentions sequence of entity mentions found in the sentence.
 * @param relationMentions sequence of relation mentions found in the sentence.
 * @param eventMentions sequence of event mentions found in the sentence.
 */
case class IEAnnotation(entityMentions: Seq[EntityMention],
                        relationMentions: Seq[RelationMention],
                        eventMentions: Seq[EventMention]) {}

/**
 * Companion object for the IEAnnotation class.
 */
object IEAnnotation {
  val empty = IEAnnotation(Seq.empty,Seq.empty,Seq.empty)
}

/**
 * A mention of a named entity.
 * @param label label of the entity.
 * @param start index to the token that begins the entity span.
 * @param end index to the token that ends the entity span.
 * @param id mention-specific identifier of the entity span.
 */
case class EntityMention(label: String, start: Int, end: Int, id: String = null) {}

/**
 * A directed relation mention.
 * @param label label of the relation.
 * @param arg1 index into sentence.entities() for the first argument (parent of the relation)
 * @param arg2 index into sentence.entities() for the second argument (child of the relation)
 */
case class RelationMention(label: String, arg1: Int, arg2: Int, id: String = null) {}

/**
 * An event mention.
 * @param label label of the event.
 * @param trigger trigger word for the event.
 * @param arguments a sequence of argument roles.
 */
case class EventMention(label: String, trigger: EntityMention, arguments: Seq[RoleMention], id: String = null) {}

/**
 * A role mention.
 * @param label label of the role.
 * @param arg the target of the role.
 */
case class RoleMention(label: String, arg: EntityMention) {}







/**
 * A container for syntactic annotation.
 * @param tree constituent tree for the sentence.
 * @param dependencies dependency tree for the sentence.
 */
case class SyntaxAnnotation(tree: ConstituentTree, dependencies: DependencyTree) {}

/**
 * Companion object for the SyntaxAnnotation class.
 */
object SyntaxAnnotation {
  val empty = SyntaxAnnotation(tree = ConstituentTree.empty, dependencies = DependencyTree.empty)
}

/**
 * A sparse dependency tree.  Not all nodes require a head.
 * @param nodes tuples of child, head, and label fields for each token with a head.
 */
case class DependencyTree(nodes: Seq[(Int,Int,String)]) {
  def hasHead(i: Int, j: Int) = nodes.exists(n => n._1 == i && n._2 == j)
  override def toString = nodes.mkString("\n")
}

/**
 * Companion object for the DependencyTree class.
 */
object DependencyTree {
  val empty = DependencyTree(nodes = Seq())
}

/**
 * A constituent tree.
 */
case class ConstituentTree() {}

/**
 * Companion object for the ConstituentTree class.
 */
object ConstituentTree {
  val empty = ConstituentTree()
}