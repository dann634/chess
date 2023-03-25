package com.jackson.game.pieces;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {

    private final byte moveForward;
    private final byte startingRow;

    public Pawn(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
        this.moveForward = (byte) (isWhite ? -1 : 1);
        this.startingRow = row;
    }


    @Override
    protected Set<byte[]> getAllMoves() {
        //Move forward two and add two diagonals
        Set<byte[]> moves = new HashSet<>();

        moves.add(new byte[]{(byte) (this.getRow() + moveForward), this.getColumn()});
        moves.add(new byte[]{(byte) (this.getRow() + (moveForward * 2)), this.getColumn()});
        moves.add(new byte[]{(byte) (this.getRow() + moveForward), (byte) (this.getColumn() - 1)});
        moves.add(new byte[]{(byte) (this.getRow() + moveForward), (byte) (this.getColumn() + 1)});

        return moves;
    }

    @Override
    protected Set<byte[]> getValidMoves(Piece[][] board) {
        Set<byte[]> moves = areMovesValid(getAllMoves(), board);

        //Code diagonal taking
        //If move is not in same column as piece check if empty or if friendly piece, remove it
        moves.removeIf(n -> n[1] != this.getColumn() && ((board[n[0]][n[1]]) == null || isPieceSameColour(this, board[n[0]][n[1]])));
        if(this.getRow() != this.startingRow) { //If moved from starting pos
            moves.removeIf(n -> n[0] == (this.getRow() + (this.moveForward * 2)));
        }
        // FIXME: 24/03/2023 Pawn can take directly
        moves.removeIf(n -> n[1] == this.getColumn() && board[n[0]][n[1]] != null);

        return moves;
    }
}
