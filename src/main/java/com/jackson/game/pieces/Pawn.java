package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Pawn extends Piece {

    private final byte moveForward;
    private final byte startingRow;

    public Pawn(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
        this.moveForward = (byte) (isWhite ? -1 : 1);
        this.startingRow = row;
    }


    @Override
    protected List<byte[]> getAllMoves() {
        //Move forward two and add two diagonals
        List<byte[]> moves = new ArrayList<>();

        moves.add(new byte[]{this.getColumn(), (byte) (this.getRow() + moveForward)});
        moves.add(new byte[]{this.getColumn(), (byte) (this.getRow() + (moveForward * 2))});
        moves.add(new byte[]{(byte) (this.getColumn()-1), (byte) (this.getRow() + moveForward)});
        moves.add(new byte[]{(byte) (this.getColumn()+1), (byte) (this.getRow() + moveForward)});

        return moves;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = getAllMoves();
        Set<byte[]> invalidMoves = new HashSet<>();

            if(board[this.column][this.row + moveForward] != null) {
                invalidMoves.add(moves.get(0));
                invalidMoves.add(moves.get(1));
            }

            if(this.row != startingRow) {
                invalidMoves.add(moves.get(1)); //Move two forward
            }

            //Diagonals
            for (int i = 2; i < 4; i++) {
                try {
                    byte[] diagonal = moves.get(i);
                    if(board[diagonal[0]][diagonal[1]] == null) { //If square is empty
                        invalidMoves.add(moves.get(i));
                    } else {
                        if(isPieceSameColour(this, board[diagonal[0]][diagonal[1]])) {
                            invalidMoves.add(moves.get(i));
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }

        moves.removeAll(invalidMoves);
        moves.removeIf(n -> n[0] < 0 || n[0] > 7 || n[1] < 0 || n[1] > 7); //On board


        return moves;
    }
}
