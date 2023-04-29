package com.jackson.game.pieces;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected List<byte[]> getAllMoves() {
        return null;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = new ArrayList<>();

        // FIXME: 27/04/2023 add check before adding move

       //dumb
        //up
        byte offset = 0;
        Piece targetPiece = null;
        do {
            offset++;

            if((this.row + offset) == 8) {
                break;
            }

            targetPiece = board[this.column][this.row + offset];
            if(targetPiece != null && !targetPiece.isPieceSameColour(this, targetPiece)) {
                //add piece
                moves.add(new byte[]{this.column, (byte) (this.row + offset)});
            }
            if(targetPiece == null) {
                moves.add(new byte[]{this.column, (byte)(this.row + offset)});
            }
        } while(board[this.column][this.row + offset] == null);

        offset = 0;
        targetPiece = null;
        do {
            offset--;

            if((this.row + offset) == -1) {
                break;
            }

            targetPiece = board[this.column][this.row + offset];
            if(targetPiece != null && !targetPiece.isPieceSameColour(this, targetPiece)) {
                //add piece
                moves.add(new byte[]{this.column, (byte) (this.row + offset)});
            }
            if(targetPiece == null) {
                moves.add(new byte[]{this.column, (byte)(this.row + offset)});
            }
        } while(board[this.column][this.row + offset] == null);


        return moves;
    }
}
