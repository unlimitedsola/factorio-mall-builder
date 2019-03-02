package love.sola.factorio

import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import love.sola.factorio.data.Recipe
import kotlin.math.roundToInt

fun genDAG(recipes: List<Recipe>): Image {
    var graph = Factory.graph("Visualization")
        .directed().strict()
    val nodes = recipes.flatMap { it.ingredients.map { it.name } + it.products.map { it.name } }.distinct()
        .associateTo(hashMapOf()) { it to Factory.node(it).with(Shape.RECTANGLE) }
    for (recipe in recipes) {
        recipe.ingredients.forEach { ingredient ->
            recipe.products.forEach { product ->
                val parentRecipe = recipes.find { it.products.any { it.name == ingredient.name } }
                if (parentRecipe == null) {
                    nodes[ingredient.name] = nodes[ingredient.name]
                        ?.link(
                            Factory.to(nodes[product.name])
                                .with(Label.of("%.1f/s".format(ingredient.amount / recipe.energy)))
                        )
                } else {
                    val produceRate =
                        ((parentRecipe.products.single { it.name == ingredient.name }.amount!! / parentRecipe.energy)
                                * 60).roundToInt()
                    val consumeRate = ((ingredient.amount / recipe.energy) * 60).roundToInt()
                    val lcm = lcm(produceRate, consumeRate)
                    nodes[ingredient.name] = nodes[ingredient.name]
                        ?.link(
                            Factory.to(nodes[product.name])
                                .with(Label.of("${lcm / produceRate}:${lcm / consumeRate}"))
                        )
                }
            }
        }
    }
    graph = graph.with(*nodes.values.toTypedArray())
    return Graphviz.fromGraph(graph).render(Format.PNG).toImage().let { SwingFXUtils.toFXImage(it, null) }
}

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
private fun lcm(a: Int, b: Int) = a * b / gcd(a, b)
