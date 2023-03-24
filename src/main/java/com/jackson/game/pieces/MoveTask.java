package com.jackson.game.pieces;

import javafx.concurrent.Task;

import java.util.List;
import java.util.Set;

public class MoveTask extends Task<Set<byte[]>> {

    private final Piece piece;
    private final List<Piece> allPieces;

    public MoveTask(Piece piece, List<Piece> allPieces) {
        this.piece = piece;
        this.allPieces = allPieces;
    }

    @Override
    protected Set<byte[]> call() throws Exception {

        //Maps out board
        Piece[][] board = new Piece[8][8];
        for(Piece piece : this.allPieces) {
            board[piece.getRow()][piece.getColumn()] = piece;
        }

        //Call polymorphic move method parsing board
        return this.piece.getValidMoves(board);


    }
}
