package com.jackson.ui;

import com.jackson.game.Game;
import com.jackson.game.pieces.King;
import com.jackson.game.pieces.Piece;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private HBox HBox;
    private Piece[][] board;
    private Game game;
    private Cell[][] cells;
    private boolean isPieceTakenHostage;
    private ImageView floatingPiece;
    private List<byte[]> currentMoves;
    private Piece selectedPiece;
    private SoundEffectsController soundEffectsController;

    private byte[] checkedCellIndex;


    public Board(Stage stage, Piece[][] board, Game game) {
        this.currentMoves = new ArrayList<>();
        this.isPieceTakenHostage = false;
        this.board = board;
        this.game = game;
        this.cells = new Cell[8][8];
        this.checkedCellIndex = new byte[]{0, 0};
        stage.setScene(getScene());
        stage.show();
    }

    private Scene getScene() {
        this.soundEffectsController = new SoundEffectsController();
        this.floatingPiece = initFloatingPiece();
        this.floatingPiece.setVisible(true);
        this.parentRoot = new AnchorPane();
        this.parentRoot.setId("anchorPane");

        this.HBox = new HBox();

        this.isBoardFacingWhite = new SimpleBooleanProperty(true);
        this.isBoardFacingWhite.addListener((observableValue, aBoolean, current) -> {
            //Rotates Board
//            if (current) {
//                this.root.setRotate(0);
//                Game.rotateAllPieces((short) 0);
//            } else {
//                root.setRotate(180);
//                Game.rotateAllPieces((short) 180);
//            }
        });
        this.root = new GridPane();
        addCellsToGrid(); //Adds cells to board

        this.parentRoot.getChildren().add(this.root);
        this.parentRoot.getChildren().add(this.floatingPiece);

//        this.HBox.getChildren().add(this.parentRoot);
//        this.HBox.getChildren().add(getRightMenu());
        Scene scene = new Scene(this.parentRoot);
        scene.getStylesheets().add("file:src/main/resources/stylesheets/board.css");
        return scene;
    }

    private VBox getRightMenu() {

        VBox mainRoot = new VBox();
        mainRoot.setAlignment(Pos.CENTER);
        mainRoot.setId("rightMenuVbox");

        Label topTime = new Label("0:00");
        topTime.setId("timeLabel");
        HBox topHbox = new HBox();
        topHbox.getChildren().add(topTime);

        Pane divider = new Pane();
        divider.setPrefHeight(mainRoot.getHeight());
        System.out.println(mainRoot.getHeight());

        Label bottomTime = new Label("0:00");
        bottomTime.setId("timeLabel");
        HBox bottomHbox = new HBox();
        bottomHbox.getChildren().add(bottomTime);

        mainRoot.getChildren().add(topHbox);
        mainRoot.getChildren().add(divider);
        mainRoot.getChildren().add(bottomHbox);

        return mainRoot;
    }


    public void drawBoard(Piece[][] board) {
        for(int columns = 0; columns < board.length; columns++) {
            for(int rows = 0; rows < board[columns].length; rows++) {
                if(board[columns][rows] != null) {
                    cells[columns][rows].addImageView(board[columns][rows].getImageView());
                }
            }
        }
        setIndicatorsOnTop();
    }

    private void setIndicatorsOnTop() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j].getIndicator().toFront();
            }
        }
    }

    private void addCellsToGrid() { //Adds cells to board
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                Cell cell = new Cell(i, j);
                this.cells[i][j] = cell;
                root.add(cell.getCell(), i, j);
                cell.getCell().toFront();
            }
        }
    }

    private byte[] getGridIndexFromMousePos(double mouseX, double mouseY) {
        byte[] index = new byte[2];
        index[0] =  (byte)(mouseX / 75);
        index[1] = (byte) (mouseY / 75);

        //account for board rotation

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

        private boolean isLight;

        public Cell(byte row, byte column) {
            this.row = row;
            this.column = column;

            this.pane = new Pane();
            this.pane.getStyleClass().add("cell");
            this.isLight = (row + column) % 2 == 0;
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
            circle.setId("movementIndicator");
            circle.setVisible(false);
            circle.setDisable(true);

            return circle;
        }

        public void setCheckIndicator(boolean isCheck) {
            this.pane.setId(isCheck ? "checkedCell" : this.isLight ? "lightCell" : "darkCell");
        }

        public void addImageView(ImageView imageView) {
            this.pane.getChildren().add(imageView);
        }

        public void removeImageView() {
            this.pane.getChildren().removeIf(n -> n.getClass().getSimpleName().equals("ImageView"));
        }

        public Pane getCell() {
        return this.pane;
    }

        public void setMovementIndicatorVisibility(boolean isVisible) {
            this.indicator.setVisible(isVisible);
        }

        public Circle getIndicator() {
            return this.indicator;
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

                // FIXME: 26/04/2023 
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
                if(board[gridIndex[0]][gridIndex[1]] != null) {
                    cells[gridIndex[0]][gridIndex[1]].removeImageView(); //Remove ImageView of Piece about to be eaten
                }
                game.move(selectedPiece, gridIndex, soundEffectsController);
                cells[selectedPiece.getColumn()][selectedPiece.getRow()].addImageView(selectedPiece.getImageView()); //Remove imageview from prev location
                setIndicatorsOnTop();
                isBoardFacingWhite.setValue(!selectedPiece.isWhite());

            }
        }
    }

    private void showMoves(MouseEvent mouseEvent) {
        clearAllIndicators();
        setIndicatorsOnTop();
        byte[] index = getGridIndexFromMousePos(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        Piece[][] board = game.getBasicBoard();
        Piece piece = board[index[0]][index[1]];

        if(piece != null) {

            if(piece.isWhite() && isBoardFacingWhite.getValue() || !piece.isWhite() && !isBoardFacingWhite.getValue()) {
                this.selectedPiece = piece;
                if(game.getCheckingPiece() == null) {
                    this.currentMoves.addAll(piece.getValidMoves(board));
                } else {
                    this.currentMoves.addAll(piece.getCheckMoves(board, game.getCheckingPiece()));
                }
                setMovementIndicatorSquares(this.currentMoves);
            }

        }
    }

    public void clearAllIndicators() {
        for(byte[] move : currentMoves) {
            cells[move[0]][move[1]].setMovementIndicatorVisibility(false);
        }
        currentMoves.clear();
    }

    public void highlightCheck(King king) {
        this.checkedCellIndex = new byte[]{king.getColumn(), king.getRow()};
        this.cells[king.getColumn()][king.getRow()].setCheckIndicator(true);
    }

    public void removeCheckIndicator() {
        this.cells[this.checkedCellIndex[0]][this.checkedCellIndex[1]].setCheckIndicator(false);
    }

}
