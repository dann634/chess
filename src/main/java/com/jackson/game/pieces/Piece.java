package com.jackson.game.pieces;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Set;

public abstract class Piece {

    protected byte row;
    protected byte column;
    private boolean isWhite;
    private ImageView imageView;

    public Piece(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;
        this.imageView = initImageView();
    }

    //Moves
    protected abstract Set<byte[]> getAllMoves();

    protected abstract Set<byte[]> getValidMoves(Piece[][] board);


    public byte getRow() {
        return this.row;
    }

    public void setRow(byte row) {
        if(row > 0 && row < 7) {
            this.row = row;
        } else {
            System.err.println("Error: Row out of bounds");
        }
    }

    public byte getColumn() {
        return this.column;
    }

    public void setColumn(byte column) {
        if(column > 0 && column < 7) {
            this.column = column;
        } else {
            System.err.println("Error: Column out of bounds");
        }
    }

    public boolean isWhite() {
        return isWhite;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private ImageView initImageView() {
        String colour = this.isWhite ? "white" : "black";
        String filePath = "file:src/main/resources/images/" + colour + this.getClass().getSimpleName() + ".png";
        ImageView imageView = new ImageView(new Image(filePath));
        imageView.setFitHeight(75);
        imageView.setFitWidth(75);
        imageView.setMouseTransparent(true);
        return imageView;
    }

    protected Set<byte[]> areMovesValid(Set<byte[]> moves, Piece[][] board) { //Basic Range Check and Friendly Check
        moves.removeIf(n -> n[0] < 0 || n[0] > 7 || n[1] < 0 || n[1] > 7); //On board
        moves.removeIf(n -> board[n[0]][n[1]] != null && isPieceSameColour(this, board[n[0]][n[1]]));
        return moves;
    }

    protected boolean isPieceSameColour(Piece piece1, Piece piece2) {

        if(piece1 == null || piece2 == null) {
            return false;
        }

        if(piece1.isWhite == piece2.isWhite) {
            return true;
        }
        return false;
    }


}
