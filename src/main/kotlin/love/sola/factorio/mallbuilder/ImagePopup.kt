package love.sola.factorio.mallbuilder

import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.Fragment
import tornadofx.borderpane
import tornadofx.center
import tornadofx.imageview

class ImagePopup : Fragment() {
    val image: Image by param()
    override val root: Parent = borderpane {
        center {
            imageview(image) {
                autosize()
            }
        }
    }
}
