package com.jackson.ui;

import com.jackson.game.Game;
import com.jackson.game.pieces.Pawn;
import com.jackson.game.pieces.Piece;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Board {


    private SimpleBooleanProperty isBoardFacingWhite;
    private GridPane root;
    private AnchorPane parentRoot;
    private SplitPane splitPane;
    private Piece[][] board;
    private Game game;
    private Cell[][] cells;
    private boolean isPieceTakenHostage;
    private ImageView floatingPiece;
    private List<byte[]> currentMoves;
    private Piece selectedPiece;


    public Board(Stage stage, Piece[][] board, Game game) {
        this.currentMoves = new ArrayList<>();
        this.isPieceTakenHostage = false;
        this.board = board;
        this.game = game;
        this.cells = new Cell[8][8];
        stage.setScene(getScene());
        stage.show();
    }

    private Scene getScene() {

        this.floatingPiece = initFloatingPiece();
        this.floatingPiece.setVisible(true);
        this.parentRoot = new AnchorPane();

        this.splitPane = new SplitPane();

        VBox vBox = new VBox(8);
        vBox.setId("rightMenuVbox");

        this.splitPane.getItems().add(vBox);

        this.isBoardFacingWhite = new SimpleBooleanProperty(true);
        this.isBoardFacingWhite.addListener((observableValue, aBoolean, current) -> {
            //Rotates Board
            if (current) {
                this.root.setRotate(0);
                Game.rotateAllPieces((short) 0);
            } else {
                root.setRotate(180);
                Game.rotateAllPieces((short) 180);
            }
        });
        this.root = new GridPane();
        addCellsToGrid(); //Adds cells to board

        this.parentRoot.getChildren().add(this.root);
        this.parentRoot.getChildren().add(this.floatingPiece);


        this.splitPane.getItems().add(0, this.parentRoot);
        Scene scene = new Scene(this.splitPane);
        scene.getStylesheets().add("file:src/main/resources/stylesheets/board.css");
        return scene;
    }

    public void drawBoard(Piece[][] board) {
        for(int columns = 0; columns < board.length; columns++) {
            for(int rows = 0; rows < board[columns].length; rows++) {
                if(board[columns][rows] != null) {
                    this.root.add(board[columns][rows].getImageView(), columns, rows);
                }
            }
        }
    }

    private void addCellsToGrid() { //Adds cells to board
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                Cell cell = new Cell(i, j);
                this.cells[i][j] = cell;
                root.add(cell.getCell(), i, j);
            }
        }
    }

    private byte[] getGridIndexFromMousePos(double mouseX, double mouseY) {
        byte[] index = new byte[2];
        index[0] =  (byte)(mouseX / 75);
        index[1] = (byte) (mouseY / 75);
        return index;
    }

    private void setMovementIndicatorSquares(List<byte[]> moves) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.cells[i][j].setMovementIndicatorVisibility(false);
            }
        }

        for(byte[] move : moves) {
            this.cells[move[0]][move[1]].setMovementIndicatorVisibility(true);
        }
    }

    private ImageView initFloatingPiece() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);
        imageView.setTranslateX(-38);
        imageView.setTranslateY(-40);

        return imageView;
    }


    class Cell {

        private byte row;
        private byte column;
        private Pane pane;
        private Circle indicator;

        public Cell(byte row, byte column) {
            this.row = row;
            this.column = column;

            this.pane = new Pane();
            this.pane.getStyleClass().add("cell");
            boolean isLight = (row + column) % 2 == 0;
            this.pane.setId(isLight ? "lightCell" : "darkCell");
            this.indicator = initMovementIndicator();
            this.pane.getChildren().add(this.indicator);
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseClickedHandler()); //Adds event handling to pane
            pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new MouseDraggedHandler());
            pane.addEventHandler(MouseEvent.MOUSE_RELEASED, new MouseDroppedHandler());
        }


        private Circle initMovementIndicator() {
            Circle circle = new Circle();
            circle.setRadius(15);
            circle.setCenterY(37.5);
            circle.setCenterX(37.5);
            circle.toFront();
            circle.setId("movementIndicator");
            circle.setVisible(false);
            circle.setDisable(true);

            return circle;
        }
        
        public Pane getCell() {
        return this.pane;
    }

        public void setMovementIndicatorVisibility(boolean isVisible) {
            this.indicator.setVisible(isVisible);
        }

}

    private class MouseClickedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            //Show piece Moves or Move piece
            showMoves(mouseEvent);
        }
    }

    private class MouseDraggedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            //Show moves
            //Take Piece imageview and make it follow mouse (use property)
            if(!isPieceTakenHostage) {
                showMoves(mouseEvent);
                isPieceTakenHostage = true;

                byte[] gridIndex = getGridIndexFromMousePos(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                Piece piece = board[gridIndex[0]][gridIndex[1]];
                if(piece != null) {
                    floatingPiece.setImage(piece.getImageView().getImage());
                    floatingPiece.setVisible(true);
                }
            }
            floatingPiece.setX(mouseEvent.getSceneX());
            floatingPiece.setY(mouseEvent.getSceneY());




            //Pick up ImageView
        }
    }

    private class MouseDroppedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            //Move piece
            isPieceTakenHostage = false;
            floatingPiece.setVisible(false);
            byte[] gridIndex = getGridIndexFromMousePos(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            boolean validSquare = false;
            for(byte[] move : currentMoves) {
                if(move[0] == gridIndex[0] && move[1] == gridIndex[1]) {
                    validSquare = true;
                    break;
                }
            }
            if(validSquare) {
                //Move Piece
                game.move(selectedPiece, gridIndex);
                root.getChildren().remove(selectedPiece.getImageView());
                root.add(selectedPiece.getImageView(), gridIndex[0], gridIndex[1]);
                // TODO: 26/04/2023 Remove movement indicators and check turn condition
                selectedPiece.setColumn(gridIndex[0]);
                selectedPiece.setRow(gridIndex[1]);
                isBoardFacingWhite.setValue(!selectedPiece.isWhite());
                for(byte[] move : currentMoves) {
                    cells[move[0]][move[1]].setMovementIndicatorVisibility(false);
                }
                currentMoves.clear();
            }
        }
    }

    private void showMoves(MouseEvent mouseEvent) {



        byte[] index = getGridIndexFromMousePos(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        Piece[][] board = game.getBasicBoard();
        Piece piece = board[index[0]][index[1]];



        if(piece != null) {

            // FIXME: 26/04/2023 Basic board and gridpane don't match

            if(piece.isWhite() && !isBoardFacingWhite.getValue() || !piece.isWhite() && isBoardFacingWhite.getValue()) {
                return;
            }
            this.selectedPiece = piece;
            this.currentMoves.addAll(piece.getValidMoves(board));
            setMovementIndicatorSquares(this.currentMoves);
        }
    }

}
