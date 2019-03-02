package love.sola.factorio.data

data class Recipe(
    val name: String,
    val category: String,
    val hidden: Boolean,
    val order: String,
    val products: List<Product>,
    val ingredients: List<Ingredient>,
    val energy: Double
) {
    fun formatSimple() =
        "${ingredients.joinToString(separator = ",") { it.formatSimple() }} -> ${products.map { it.formatSimple() }}"
}

data class Ingredient(val type: String, val name: String, val amount: Int, val catalyst_amount: Int?) {
    fun formatSimple() = "$name x $amount"
}

data class Product(
    val type: String, val name: String, val amount: Int?, val catalyst_amount: Int?,
    val amount_min: Int?, val amount_max: Int?, val probability: Int?
) {
    fun formatSimple(): String = if (amount != null) {
        "$name x $amount"
    } else {
        "$name x $amount_min-$amount_max"
    }
}
