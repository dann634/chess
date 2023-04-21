package com.jackson.game.pieces;

import com.jackson.game.Game;
import com.jackson.ui.Board;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Set;

public class MoveTask extends Task<Set<byte[]>> {

    private final Piece piece;
    private final List<Piece> allPieces;
    private Board board;

    public MoveTask(Piece piece, List<Piece> allPieces, Board board) {
        this.piece = piece;
        this.allPieces = allPieces;
        this.board = Game.getBoard();
    }

    @Override
    protected Set<byte[]> call() throws Exception {

        //Maps out board
        Piece[][] board = new Piece[8][8];
        for(Piece piece : this.allPieces) {
            board[piece.getRow()][piece.getColumn()] = piece;
        }

        //Call polymorphic move method parsing board
        Set<byte[]> moves = this.piece.getValidMoves(board);


        return moves;
    }



}
