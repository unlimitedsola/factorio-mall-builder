package love.sola.factorio.mallbuilder

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.application.Application
import tornadofx.App
import java.io.File

class MallBuilder : App(MainView::class) {

}

val objectMapper = jacksonObjectMapper()
val recipes = objectMapper.readValue<Map<String, Recipe>>(File("recipes.json").readText())

fun main(args: Array<String>) {
    Application.launch(MallBuilder::class.java, *args)
}
