package com.jackson.game.pieces;

import java.util.List;

public class Bishop extends Piece {
    public Bishop(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected List<byte[]> getAllMoves() {
        return null;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        return null;
    }
}
