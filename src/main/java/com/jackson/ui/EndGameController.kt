package com.jackson.ui

import com.jackson.game.Game
import com.jackson.game.Move
import com.jackson.main.Main
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class EndGameController {

    private lateinit var moveList : List<Move>

    fun getScene(winReason : String, isWhiteWinner : Boolean, moveList : List<Move>) : Scene {
        val vBox = VBox()
        vBox.id = "vbox"

        this.moveList = moveList


        //Title
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


        val scrollPaneVbox = VBox()
        scrollPaneVbox.alignment = Pos.CENTER

        val scrollPane = ScrollPane()
        scrollPane.content = scrollPaneVbox
        scrollPane.prefWidth = 200.0
        scrollPaneVbox.prefWidthProperty().bind(scrollPane.widthProperty()) // FIXME: NOT CENTERED

        //Move List
        val moveListTitle = Label("Move List")
        moveListTitle.id = "moveLabel"

        scrollPaneVbox.children.addAll(moveListTitle)
        for(i in 0..moveList.size step 2) {

            val hbox = HBox(24.0)
            hbox.id = "moveHBox"

            val indexLabel = Label(((i / 2) + 1).toString() + ".")
            hbox.children.add(indexLabel)

            for (j in i..i+1) {
                if(j < this.moveList.size) {
                    val move = Label(this.moveList[j].generateMoveString())
                    hbox.children.add(move)
                }
            }

            scrollPaneVbox.children.add(hbox)

        }

        val rootHBox = HBox()
        rootHBox.maxHeight(600.0)
        rootHBox.children.addAll(scrollPane, vBox)

        val scene = Scene(rootHBox)
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