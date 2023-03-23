package com.jackson.game;

import com.jackson.game.pieces.Piece;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Set;

public class MoveTask extends Task<Set<int[]>> {

    private Piece piece;
    private List<Piece> allPieces;

    public MoveTask(Piece piece, List<Piece> allPieces) {
        this.piece = piece;
        this.allPieces = allPieces;
    }

    @Override
    protected Set<int[]> call() throws Exception {
        //Get all moves
        return null;
    }
}
