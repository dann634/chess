package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    private List<byte[]> offsetList;
    public Bishop(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
        this.offsetList = new ArrayList<>();
        this.offsetList.add(new byte[]{1, 1});
        this.offsetList.add(new byte[]{1, -1});
        this.offsetList.add(new byte[]{-1, 1});
        this.offsetList.add(new byte[]{-1, -1});
    }


    @Override
    protected List<byte[]> getAllMoves() {
        return null;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = generateLinearMoves(this.offsetList, board, false);
        return moves;
    }

    @Override
    public List<byte[]> getLegalMoves(Piece[][] board) {
        List<byte[]> moves = generateLinearMoves(this.offsetList, board, false);
        removePinnedMoves(moves, board);
        return moves;
    }

    @Override
    public List<byte[]> getSquaresProtected(Piece[][] board) {
        return generateLinearMoves(this.offsetList, board, true);
    }

}
