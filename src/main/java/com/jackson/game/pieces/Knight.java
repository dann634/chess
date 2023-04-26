package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected List<byte[]> getAllMoves() {
        List<byte[]> allMoves = new ArrayList<>();

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

        for(byte[] move : allMoves) {
            byte temp = move[0];
            move[0] = move[1];
            move[1] = temp;
        } // FIXME: 25/04/2023 Change this later

        return allMoves;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = getAllMoves();
        areMovesOnBoard(moves);
        return moves;
    }
}
