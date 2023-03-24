package com.jackson.game.pieces;

import java.util.Set;

public class Bishop extends Piece {
    public Bishop(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected Set<byte[]> getAllMoves() {
        return null;
    }

    @Override
    protected Set<byte[]> getValidMoves(Piece[][] board) {
        return null;
    }
}
