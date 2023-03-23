package com.jackson.game.pieces;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Set;

public abstract class Piece {

    private byte row;
    private byte column;
    private boolean isWhite;
    private ImageView imageView;

    public Piece(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;
        this.imageView = initImageView();
    }

    //Moves
    protected abstract Set<int[]> getAllMoves();

    protected abstract Set<int[]> getValidMoves();


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
}
