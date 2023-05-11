package com.jackson.game;

import com.jackson.game.pieces.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final boolean isWhite;
    private boolean hasMoved;
    private List<Piece> pieces;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
        this.hasMoved = false;
    }


    public void initializePieces(Piece[][] board) {
        this.pieces = new ArrayList<>();
        byte pawnRow = (byte) (this.isWhite ? 6 : 1);
        byte kingRow = (byte) (this.isWhite ? 7 : 0);
        //Pawns
        for (int i = 0; i < 8; i++) {
            this.pieces.add(new Pawn(pawnRow, (byte) i, this.isWhite));
        }
        //Everything else
        this.pieces.add(new Rook(kingRow, (byte) 0, this.isWhite));
        this.pieces.add(new Rook(kingRow, (byte) 7, this.isWhite));
        this.pieces.add(new Knight(kingRow, (byte) 1, this.isWhite));
        this.pieces.add(new Knight(kingRow, (byte) 6, this.isWhite));
        this.pieces.add(new Bishop(kingRow, (byte) 2, this.isWhite));
        this.pieces.add(new Bishop(kingRow, (byte) 5, this.isWhite));
        this.pieces.add(new Queen(kingRow, (byte) 3, this.isWhite));
        this.pieces.add(new King(kingRow, (byte) 4, this.isWhite));

        for (Piece piece : this.pieces) {
            board[piece.getColumn()][piece.getRow()] = piece;
        }
    }


    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void setPiecesEnabled(boolean isEnabled) {
        for(Piece piece : this.pieces) {
            piece.getImageView().setDisable(!isEnabled);
        }
    }


}
