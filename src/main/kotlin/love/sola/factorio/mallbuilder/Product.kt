package love.sola.factorio.mallbuilder

data class Product(
    val type: String, val name: String, val amount: Int?,
    val amount_min: Int?, val amount_max: Int?, val probability: Int?
) {
    fun formatSimple(): String = if (amount != null) {
        "${name}x$amount"
    } else {
        "${name}x[$amount_min~$amount_max]"
    }
}

