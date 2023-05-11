package com.jackson.game.pieces;

import com.jackson.game.Game;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    private boolean canCastle;
    private boolean isKingSide;

    private List<byte[]> offsetList;

    public Rook(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);

        this.canCastle = true;
        this.isKingSide = (this.column == 7);

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

    public boolean canCastle() {
        return canCastle;
    }

    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }

    public boolean isKingSide() {
        return isKingSide;
    }
}
