package love.sola.factorio.mallbuilder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Recipe(
    val name: String,
    val products: List<Product>,
    val ingredients: List<Ingredient>,
    val energy: Double
) {
    fun formatSimple() =
        "${ingredients.joinToString(separator = ",") { it.formatSimple() }} -> ${products.map { it.formatSimple() }}"
}

