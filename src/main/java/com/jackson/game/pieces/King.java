package com.jackson.game.pieces;

import com.jackson.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class King extends Piece {
    public King(byte row, byte column, boolean isWhite) {
        super(row, column, isWhite);
    }

    @Override
    protected List<byte[]> getAllMoves() {
        List<byte[]> moves = new ArrayList<>();

        //Create square including king pos, then remove king pos (starting top left square)
        byte topLeftRow = (byte) (this.getRow() - 1); //hmm this might not work
        byte topLeftColumn = (byte) (this.getColumn() - 1);

        for (int i = this.getColumn() - 1; i <= this.getColumn() + 1; i++) {
            for (int j = this.getRow() - 1; j <= this.getRow() + 1; j++) {
                moves.add(new byte[]{(byte) i, (byte) j});
            }
        }
        
        //Remove move onto king pos
        byte[] invalidMove = new byte[0];
        for(byte[] move : moves) {
            if(move[0] == this.getColumn() && move[1] == this.getRow()) {
                invalidMove = move;
            }
        }
        moves.remove(invalidMove);

        return moves;
    }

    @Override
    public List<byte[]> getValidMoves(Piece[][] board) {
        List<byte[]> moves = getAllMoves();
        areMovesOnBoard(moves); //Range Check
        removeCellsOccupiedByFriendly(board, moves); //Friendly Check
        removeProtectedMoves(moves, board); // Can't move into check

        return moves;

    }

    private void removeProtectedMoves(List<byte[]> moves, Piece[][] board) { //Super inefficient
        Set<byte[]> allEnemyMoves = Game.getAllNonKingEnemyMoves(this.isWhite(), board);

        //Pawn Diagonal Attack doesn't exist until there is a piece
        Set<byte[]> pawnDiagonals = Game.getEnemyPawnDiagonals(this.isWhite());
        allEnemyMoves.addAll(pawnDiagonals); // FIXME: 29/04/2023 DOENS;'t work 

        moves.removeAll(allEnemyMoves);
    }

}
