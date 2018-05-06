package love.sola.factorio.mallbuilder

import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image

fun genDAG(recipes: List<Recipe>): Image {
    var graph = Factory.graph("Visualization")
        .directed().strict()
    val nodes = recipes.flatMap { it.ingredients.map { it.name } + it.products.map { it.name } }.distinct()
        .associateTo(hashMapOf()) { it to Factory.node(it).with(Shape.RECTANGLE) }
    for (recipe in recipes) {
        recipe.ingredients.forEach { ingredient ->
            recipe.products.forEach { product ->
                nodes[ingredient.name] = nodes[ingredient.name]
                    ?.link(
                        Factory.to(nodes[product.name])
                            .with(Label.of("${(ingredient.amount / recipe.energy) * 60}/min"))
                    )
            }
        }
    }
    graph = graph.with(*nodes.values.toTypedArray())
    return Graphviz.fromGraph(graph).render(Format.PNG).toImage().let { SwingFXUtils.toFXImage(it, null) }
}
