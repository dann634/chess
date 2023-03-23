package com.jackson.game.pieces;

import java.util.Set;

public class Rook extends Piece {

    public Rook(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected Set<int[]> getAllMoves() {
        return null;
    }

    @Override
    protected Set<int[]> getValidMoves() {
        return null;
    }
}
