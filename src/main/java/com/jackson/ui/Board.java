package com.jackson.ui;

import com.jackson.game.Game;
import com.jackson.game.MoveTask;
import com.jackson.game.Player;
import com.jackson.game.pieces.Piece;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

    private GridPane root;

    public Scene getScene() {
        root = new GridPane();
        addCellsToGrid(root); //Adds cells to board

        Scene scene = new Scene(root);
        scene.getStylesheets().add("file:src/main/resources/stylesheets/board.css");
        return scene;
    }

    public void drawBoard(Player white, Player black) {
        for(Node node : root.getChildren()) {
            ((VBox) node).getChildren().clear();
        }

        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(white.getPieces());
        allPieces.addAll(black.getPieces());

        for(Piece piece : allPieces) {
            root.add(piece.getImageView(), piece.getColumn(), piece.getRow());
        }
    }

    private void addCellsToGrid(GridPane pane) { //Adds cells to board
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                pane.add(new Cell(i, j).getCell(), j, i);
            }
        }
    }

    public boolean isCellOccupied(byte targetRow, byte targetColumn) {
        return false;
    }

    class Cell {

        private byte row;
        private byte column;
        private VBox vBox;
        private Piece pieceInCell;

        public Cell(byte row, byte column) {
            this.row = row;
            this.column = column;

            this.vBox = new VBox();
            this.vBox.getStyleClass().add("cell");
            boolean isLight = (row + column) % 2 == 0;
            this.vBox.setId(isLight ? "lightCell" : "darkCell");

            this.vBox.setOnMouseClicked(mouseEvent -> {
                //Add movement indicators
                this.pieceInCell = Game.getPieceInCell(row, column);
                if(this.pieceInCell != null) {
                    //Add Movement Indicators
                    MoveTask moveTask = new MoveTask(this.pieceInCell, Game.getAllPieces());
                    Thread moveThread = new Thread(moveTask);
                    moveThread.setDaemon(true);
                    moveThread.start();
                }
            });

        }



        public VBox getCell() {
            return this.vBox;
        }
    }


}
