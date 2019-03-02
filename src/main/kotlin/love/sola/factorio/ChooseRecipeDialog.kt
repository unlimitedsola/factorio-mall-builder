package love.sola.factorio

import javafx.geometry.Pos
import javafx.scene.Parent
import love.sola.factorio.data.Recipe
import tornadofx.*

class ChooseRecipeDialog : Fragment() {
    val item: String by param()
    val recipes: List<Recipe> by param()
    var chosenRecipe: Recipe? = null

    override val root: Parent = vbox(alignment = Pos.CENTER) {
        label("For item '$item' there are multiple recipes available, please choose one of them from below:")
        val recipeList = listview(recipes.observable()) {
            cellFormat {
                text = "${it.name}: ${it.formatSimple()}"
                onDoubleClick {
                    chosenRecipe = it
                    close()
                }
            }
        }
        button("Confirm") {
            action {
                val selectedRecipe = recipeList.selectedItem
                if (selectedRecipe != null) {
                    chosenRecipe = selectedRecipe
                    close()
                }
            }
        }
    }
}
