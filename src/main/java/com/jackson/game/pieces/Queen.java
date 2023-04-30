package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(byte row, byte column, boolean isWhite) {
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
        //Diagonals
        offsetList.add(new byte[]{1, 1});
        offsetList.add(new byte[]{1, -1});
        offsetList.add(new byte[]{-1, 1});
        offsetList.add(new byte[]{-1, -1});
        //Straight Lines
        offsetList.add(new byte[]{1, 0});
        offsetList.add(new byte[]{-1, 0});
        offsetList.add(new byte[]{0, 1});
        offsetList.add(new byte[]{0, -1});

        List<byte[]> moves = generateLinearMoves(offsetList, board); //already validated
        return moves;
    }
}
