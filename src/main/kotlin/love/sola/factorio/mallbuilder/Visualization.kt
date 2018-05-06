package love.sola.factorio.mallbuilder

import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image

fun genDAG(recipes: List<Recipe>): Image {
    var graph = Factory.graph("Visualization")
        .directed().strict()
    val nodes = recipes.flatMap { it.ingredients }.map { it.name }.distinct()
        .associateTo(hashMapOf()) { it to Factory.node(it).with(Shape.RECTANGLE) }
    for (recipe in recipes) {
        recipe.ingredients.forEach {
            nodes[it.name] = nodes[it.name]?.link(*recipe.products.map { it.name }.toTypedArray())
        }
    }
    graph = graph.with(*nodes.values.toTypedArray())
    return Graphviz.fromGraph(graph).render(Format.PNG).toImage().let { SwingFXUtils.toFXImage(it, null) }
}
