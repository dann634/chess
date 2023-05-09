package com.jackson.game.pieces;

import com.jackson.game.Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;

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
    public List<byte[]> getValidMoves(Piece[][] board) { // FIXME: 09/05/2023 this doesnt work
        List<byte[]> moves = getAllMoves();
        areMovesOnBoard(moves); //Range Check
        removeCellsOccupiedByFriendly(board, moves); //Friendly Check
        removeProtectedMoves(moves, board); // Can't move into check

        return moves;

    }

    private void removeProtectedMoves(List<byte[]> moves, Piece[][] board) { // FIXME: 09/05/2023 this doenst work
        Set<byte[]> allEnemyMoves = Game.getAllEnemyMoves(this.isWhite(), board, true);
        // TODO: 09/05/2023 King can move backwards in line with linear pieces
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
    public List<byte[]> getLegalMoves(Piece[][] board) {
        return getValidMoves(board);
    }

    @Override
    public List<byte[]> getSquaresProtected(Piece[][] board) {
        List<byte[]> moves = getAllMoves();
        areMovesOnBoard(moves);
        return moves;
    }

    @Override
    public List<byte[]> getCheckMoves(Piece[][] board) {
       return getValidMoves(board);
    }

    public boolean isInCheck(Piece[][] board) {
        //Need to add check for pawn and knight
        List<byte[]> enemyMoves = new ArrayList<>();


        List<Pawn> pieces = Game.getPawns(!isWhite()); //Pawns
        for(Piece piece : pieces) {
            enemyMoves.addAll(piece.getValidMoves(board));
        }
        
        List<Knight> knights = Game.getKnights(!isWhite());
        for(Knight knight : knights) {
            enemyMoves.addAll(knight.getValidMoves(board));
        }
        
        for(byte[] move : enemyMoves) {
            if(move[0] == this.getColumn() && move[1] == this.getRow()) {
                return true;
            }
        }
        
        List<byte[]> offsets = new ArrayList<>(); // FIXME: 09/05/2023 diagonals causing the checking issue
        offsets.add(new byte[]{1, 0});
        offsets.add(new byte[]{-1, 0});
        offsets.add(new byte[]{0, 1});
        offsets.add(new byte[]{0, -1});
        for(byte[] offset : offsets) {
            if(checkOffsetForCheck(offset, board, false)) {
                return true;
            }
        }
        
        offsets.clear();
        offsets.add(new byte[]{1, 1});
        offsets.add(new byte[]{1, -1});
        offsets.add(new byte[]{-1, 1});
        offsets.add(new byte[]{-1, -1});
        for(byte[] offset : offsets) {
            if(checkOffsetForCheck(offset, board, true)) {
                return true;
            }
        }

        return false;
        
    }

    private boolean checkOffsetForCheck(byte[] offset, Piece[][] board, boolean isDiagonal) {
        byte columnMultiplier = 1;
        byte newColumn = (byte) (this.getColumn() + offset[0]);
        byte newRow = (byte) (this.getRow() + offset[1]);
        Piece targetPiece;

        while(newColumn >= 0 && newColumn <= 7 && newRow >= 0 && newRow <= 7) {

            targetPiece = board[newColumn][newRow];

            if(targetPiece != null) {
                String className = targetPiece.getClass().getSimpleName();
                if(isPieceSameColour(this, targetPiece)) { //friendly piece
                    return false;
                } else { //enemy piece

                    if(className.equals("Queen")) { //Queen can attack from straight and diagonal
                        return true;
                    }

                    if(isDiagonal && className.equals("Bishop")) { //Diagonal
                        return true;
                    }

                    if(!isDiagonal && className.equals("Rook")) { //Straight
                        return true;
                    }

                    return false;
                }
            }

            columnMultiplier++;
            newColumn = (byte) (this.getColumn() + (offset[0] * columnMultiplier));
            newRow = (byte) (this.getRow() + (offset[1] * columnMultiplier));
        }
        return false;
    }

    // TODO: 01/05/2023 Add Method for all pieces to get all moves + moves that protect pieces

}
