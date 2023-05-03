package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    private List<byte[]> offsetList;

    public Rook(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);

        this.offsetList = new ArrayList<>();
        this.offsetList.add(new byte[]{1, 0});
        this.offsetList.add(new byte[]{-1, 0});
        this.offsetList.add(new byte[]{0, 1});
        this.offsetList.add(new byte[]{0, -1});
    }

    @Override
    protected List<byte[]> getAllMoves() {
        return null;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = generateLinearMoves(this.offsetList, board, false);
        removeMovesFromPin(board, moves);
        return moves;
    }

    @Override
    public List<byte[]> getSquaresProtected(Piece[][] board) {
        return generateLinearMoves(this.offsetList, board, true);
    }
}
