package com.jackson.game.pieces;

import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {

    public Knight(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected Set<byte[]> getAllMoves() {
        Set<byte[]> allMoves = new HashSet<>();

        //Forward
        allMoves.add(new byte[]{(byte) (this.getRow() - 2), (byte) (this.getColumn() - 1)});
        allMoves.add(new byte[]{(byte) (this.getRow() - 2), (byte) (this.getColumn() + 1)});

        //Right
        allMoves.add(new byte[]{(byte) (this.getRow() + 1), (byte) (this.getColumn() + 2)});
        allMoves.add(new byte[]{(byte) (this.getRow() - 1), (byte) (this.getColumn() + 2)});

        //Down
        allMoves.add(new byte[]{(byte) (this.getRow() + 2), (byte) (this.getColumn() + 1)});
        allMoves.add(new byte[]{(byte) (this.getRow() + 2), (byte) (this.getColumn() - 1)});

        //Left
        allMoves.add(new byte[]{(byte) (this.getRow() + 1), (byte) (this.getColumn() - 2)});
        allMoves.add(new byte[]{(byte) (this.getRow() - 1), (byte) (this.getColumn() - 2)});

        return allMoves;
    }

    @Override
    protected Set<byte[]> getValidMoves(Piece[][] board) {
        return areMovesValid(getAllMoves(), board);
    }
}
