package love.sola.factorio.mallbuilder

data class Ingredient(val type: String, val name: String, val amount: Int){
    fun formatSimple() = "${name}x$amount"
}
