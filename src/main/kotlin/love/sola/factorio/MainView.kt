package love.sola.factorio

import javafx.beans.property.SimpleListProperty
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import love.sola.factorio.data.Recipe
import tornadofx.*

class MainView : View("Mall Builder") {

    private val preferredRecipe = observableList<Recipe>()
    private val selectedRecipes = observableList<Recipe>()
    private val ingredientsProperty = SimpleListProperty<Pair<String, String>>().apply {
        selectedRecipes.onChange {
            val ingredients = flatIngredients(selectedRecipes).entries.sortedBy { it.value }
            value = ingredients.map {
                val profit = try {
                    flatIngredients(selectedRecipes + chooseRecipe(it.key)).size - ingredients.size
                } catch (e: Exception) {
                    "-"
                }
                it.key to "${it.key} (${it.value}) ($profit)"
            }.observable()
        }
    }

    override val root: Parent = gridpane {
        hgap = 10.0
        padding = tornadofx.insets(10)
        row {
            constraintsForRow(0).vgrow = Priority.ALWAYS
            vbox {
                gridpaneColumnConstraints {
                    percentWidth = 40.0
                    hgrow = Priority.ALWAYS
                }
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
                gridpaneColumnConstraints {
                    percentWidth = 40.0
                    hgrow = Priority.ALWAYS
                }
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
                            runLater {
                                requestFocus()
                            }
                        }
                    button("+") {
                        action {
                            selectedRecipes.add(chooseRecipe(searchBar.value))
                        }
                    }
                }
                listview(selectedRecipes) {
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
                gridpaneColumnConstraints {
                    percentWidth = 20.0
                    hgrow = Priority.ALWAYS
                }
                button("Visualize") {
                    action {
                        find<ImagePopup>(
                            mapOf(
                                ImagePopup::image to genDAG(
                                    selectedRecipes
                                )
                            )
                        )
                            .openModal()
                    }
                }
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
