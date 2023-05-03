package com.jackson.game.pieces;

import com.jackson.game.Game;

import java.util.ArrayList;
import java.util.HashSet;
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

    private void removeProtectedMoves(List<byte[]> moves, Piece[][] board) {
        Set<byte[]> allEnemyMoves = Game.getAllEnemyMoves(this.isWhite(), board);

        Set<byte[]> invalidMoves = new HashSet<>();
        for(byte[] enemyMove : allEnemyMoves) {
            for(byte[] kingMove : moves) {
                if(enemyMove[0] == kingMove[0] && enemyMove[1] == kingMove[1]) {
                    invalidMoves.add(kingMove);
                }
            }
        }
        moves.removeAll(invalidMoves);
    }

    @Override
    public List<byte[]> getSquaresProtected(Piece[][] board) {
        return getAllMoves();
    }

    @Override
    public List<byte[]> getCheckMoves(Piece[][] board, Piece checkingPiece) {
       return getValidMoves(board);
    }

    // TODO: 01/05/2023 Add Method for all pieces to get all moves + moves that protect pieces

}
