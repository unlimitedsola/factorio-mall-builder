package love.sola.factorio.mallbuilder

import javafx.embed.swing.SwingFXUtils
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.input.DataFormat
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import tornadofx.*
import javax.imageio.ImageIO

class ImagePopup : Fragment() {
    val image: Image by param()
    override val root: Parent = borderpane {
        center {
            imageview(image) {
                isPreserveRatio = true
                fitToParentSize()
                fitWidthProperty().bind((parent as Region).widthProperty())
                fitHeightProperty().bind((parent as Region).heightProperty())
                contextmenu {
                    item("Copy") {
                        action {
                            clipboard.put(DataFormat.IMAGE, image)
                        }
                    }
                    item("Save as") {
                        action {
                            val file = chooseFile(
                                "Save",
                                mode = FileChooserMode.Save,
                                filters = arrayOf(FileChooser.ExtensionFilter("Image", "*.png"))
                            ).single()
                            ImageIO.write(image.let { SwingFXUtils.fromFXImage(it, null) }, "png", file)
                        }
                    }
                }
            }
        }
    }
}
