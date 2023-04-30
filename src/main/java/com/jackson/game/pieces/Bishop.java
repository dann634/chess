package com.jackson.game.pieces;

import java.util.ArrayList;
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

        //Setup offsets
        List<byte[]> offsetList = new ArrayList<>();
        offsetList.add(new byte[]{1, 1});
        offsetList.add(new byte[]{1, -1});
        offsetList.add(new byte[]{-1, 1});
        offsetList.add(new byte[]{-1, -1});

        List<byte[]> moves = generateLinearMoves(offsetList, board);


        return moves;
    }
}
