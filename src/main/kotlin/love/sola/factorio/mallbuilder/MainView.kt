package love.sola.factorio.mallbuilder

import javafx.beans.property.SimpleListProperty
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View("Mall Builder") {

    private val preferredRecipe = observableList<Recipe>()
    private val selectedRecipes = observableList<Recipe>()
    private val ingredientsProperty = SimpleListProperty<Pair<String, String>>().apply {
        selectedRecipes.onChange {
            val ingredients = flatIngredients(selectedRecipes).entries.sortedBy { it.value }
            value = ingredients.map {
                val profit = flatIngredients(selectedRecipes + chooseRecipe(it.key)).size - ingredients.size
                it.key to "${it.key} (${it.value}) ($profit)"
            }.observable()
        }
    }

    override val root: Parent = hbox(spacing = 20) {
        vbox {
            label(ingredientsProperty.sizeProperty.stringBinding { "Ingredients: $it" })
            label("Format: name (used times by recipes) (size change if remove)")
            listview(ingredientsProperty) {
                vgrow = Priority.ALWAYS
                cellFormat {
                    text = it.second
                    setOnMouseClicked { _ ->
                        selectedRecipes.add(chooseRecipe(it.first))
                    }
                }
            }
        }
        vbox {
            label(selectedRecipes.sizeProperty.stringBinding { "Chosen Recipes: $it" })
            hbox(spacing = 10) {
                val searchBar =
                    combobox<String>(values = recipes.flatMap { it.value.products.map { it.name } }.observable()) {
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.MAX_VALUE
                        makeAutocompletable()
                        setOnKeyPressed {
                            if (it.code == KeyCode.ENTER) {
                                selectedRecipes.add(chooseRecipe(value))
                            }
                        }
                    }
                button("+") {
                    action {
                        selectedRecipes.add(chooseRecipe(searchBar.value))
                    }
                }
            }
            listview<Recipe>(selectedRecipes) {
                vgrow = Priority.ALWAYS
                cellFormat {
                    text = it.products.joinToString(separator = ", ") { it.name }
                    setOnMouseClicked { _ ->
                        selectedRecipes.remove(it)
                    }
                }
            }
        }
        vbox {
            label(preferredRecipe.sizeProperty.stringBinding { "Preferred Recipes: $it" })
            listview(preferredRecipe) {
                vgrow = Priority.ALWAYS
                cellFormat {
                    text = "${it.name}: ${it.formatSimple()}"
                    setOnMouseClicked { _ ->
                        preferredRecipe.remove(it)
                    }
                }
            }
        }
    }

    fun flatIngredients(selectedRecipes: List<Recipe>): Map<String, Int> {
        val producible = selectedRecipes.flatMap { it.products }.map { it.name }.distinct()
        return selectedRecipes.flatMap { it.ingredients }.map { it.name }
            .filter { !producible.contains(it) }
            .fold(hashMapOf()) { acc, it ->
                acc.compute(it) { _, value -> (value ?: 0) + 1 }
                acc
            }
    }

    fun chooseRecipe(item: String): Recipe {
        val cachedRecipe = preferredRecipe.find { it.products.map { it.name }.contains(item) }
        if (cachedRecipe != null) {
            return cachedRecipe
        }
        val availableRecipes: List<Recipe> = recipes.values.filter {
            it.products.map { it.name }.contains(item)
        }
        if (availableRecipes.isEmpty()) {
            throw RuntimeException("no recipe for item: $item")
        }
        if (availableRecipes.size == 1) {
            return availableRecipes.single()
        }
        val dialog = find<ChooseRecipeDialog>(
            mapOf(
                ChooseRecipeDialog::item to item,
                ChooseRecipeDialog::recipes to availableRecipes
            )
        )
        dialog.openModal(block = true)
        val chosenRecipe = dialog.chosenRecipe!!
        preferredRecipe.add(chosenRecipe)
        return chosenRecipe
    }


}
