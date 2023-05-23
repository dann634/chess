package com.jackson.ui

import com.jackson.game.Game
import com.jackson.main.Main
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class EndGameController {

    fun getScene(winReason : String, isWhiteWinner : Boolean) : Scene {
        val vBox = VBox()
        vBox.id = "vbox"


        val winningColour = if(isWhiteWinner) {
            "White"
        } else {
            "Black"
        }

        val titleText = if(winReason == "checkmate") {
            "$winningColour won by checkmate!!"
        } else {
            "Draw by Stalemate"
        }
        val title = Label(titleText)
        title.id = "title"

        val hBox = HBox(12.0)
        hBox.alignment = Pos.CENTER
        hBox.children.addAll(createPlayAgainButton(), createExitButton())

        vBox.children.addAll(title, hBox)

        val scene = Scene(vBox)
        scene.stylesheets.add("file:src/main/resources/stylesheets/endGame.css")

        return scene
    }

    private fun createPlayAgainButton() : Button {
        val btn = Button("Play Again")
        btn.id = "btn"

        btn.setOnAction {
            Game().reset()
        }

        return btn
    }
    private fun createExitButton() : Button {
        val btn = Button("Exit")
        btn.id = "btn"

        btn.setOnAction {
            Main.getStage().close()
        }

        return btn

    }

}