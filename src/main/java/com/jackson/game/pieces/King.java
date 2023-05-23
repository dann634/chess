package com.jackson.game.pieces;

import com.jackson.game.Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class King extends Piece {

    private boolean canCastle;

    public King(byte row, byte column, boolean isWhite) { // FIXME: 21/05/2023 King can move back on beam
        super(row, column, isWhite);
        this.canCastle = true;
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
        for (byte[] move : moves) {
            if (move[0] == this.getColumn() && move[1] == this.getRow()) {
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
        removeProtectedMoves(moves, board);// Can't move into check
        addCastlingMoves(moves, board);

        return moves;

    }

    private void removeProtectedMoves(List<byte[]> moves, Piece[][] board) {
        Set<byte[]> allEnemyMoves = Game.getAllEnemyMoves(this.isWhite(), board, true);
        Set<byte[]> invalidMoves = new HashSet<>();
        for (byte[] enemyMove : allEnemyMoves) {
            for (byte[] kingMove : moves) {
                if (enemyMove[0] == kingMove[0] && enemyMove[1] == kingMove[1]) {
                    invalidMoves.add(kingMove);
                }
            }
        }
        moves.removeAll(invalidMoves);
        linearBackTracking(moves, board);
    }

    private void linearBackTracking(List<byte[]> moves, Piece[][] board) {
        //Straight
        List<byte[]> offsets = new ArrayList<>();
        offsets.add(new byte[]{1, 0});
        offsets.add(new byte[]{-1, 0});
        offsets.add(new byte[]{0, 1});
        offsets.add(new byte[]{0, -1});
        for(byte[] offset : offsets) {
            if(checkOffsetForCheck(offset, board, false)) {
                moves.removeIf(n -> n[0] == (this.column + (offset[0] * -1)) && n[1] == this.getRow() + (offset[1] * -1));
            }
        }

        offsets.clear();
        offsets.add(new byte[]{1, 1});
        offsets.add(new byte[]{1, -1});
        offsets.add(new byte[]{-1, 1});
        offsets.add(new byte[]{-1, -1});
        for(byte[] offset : offsets) {
            if(checkOffsetForCheck(offset, board, true)) {
                moves.removeIf(n -> n[0] == (this.column + (offset[0] * -1)) && n[1] == this.getRow() + (offset[1] * -1));
            }
        }
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
        for (Piece piece : pieces) {
            enemyMoves.addAll(piece.getValidMoves(board));
        }

        List<Knight> knights = Game.getKnights(!isWhite());
        for (Knight knight : knights) {
            enemyMoves.addAll(knight.getValidMoves(board));
        }

        for (byte[] move : enemyMoves) {
            if (move[0] == this.getColumn() && move[1] == this.getRow()) {
                return true;
            }
        }

        List<byte[]> offsets = new ArrayList<>(); // FIXME: 09/05/2023 diagonals causing the checking issue
        offsets.add(new byte[]{1, 0});
        offsets.add(new byte[]{-1, 0});
        offsets.add(new byte[]{0, 1});
        offsets.add(new byte[]{0, -1});
        for (byte[] offset : offsets) {
            if (checkOffsetForCheck(offset, board, false)) {
                return true;
            }
        }

        offsets.clear();
        offsets.add(new byte[]{1, 1});
        offsets.add(new byte[]{1, -1});
        offsets.add(new byte[]{-1, 1});
        offsets.add(new byte[]{-1, -1});
        for (byte[] offset : offsets) {
            if (checkOffsetForCheck(offset, board, true)) {
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

        while (newColumn >= 0 && newColumn <= 7 && newRow >= 0 && newRow <= 7) {

            targetPiece = board[newColumn][newRow];

            if (targetPiece != null) {
                String className = targetPiece.getClass().getSimpleName();
                if (!isPieceSameColour(this, targetPiece)) {
                //enemy piece

                    if (className.equals("Queen")) { //Queen can attack from straight and diagonal
                        return true;
                    }

                    if (isDiagonal && className.equals("Bishop")) { //Diagonal
                        return true;
                    }

                    //Straight
                    return className.equals("Rook");

                }
                return false;
            }

            columnMultiplier++;
            newColumn = (byte) (this.getColumn() + (offset[0] * columnMultiplier));
            newRow = (byte) (this.getRow() + (offset[1] * columnMultiplier));
        }
        return false;
    }


    private void addCastlingMoves(List<byte[]> moves, Piece[][] board) {
        boolean[] canCastle = Game.canCastle(this.isWhite(), board, this);


        if(canCastle == null) {
            return;
        }

        if(canCastle[0]) {
            moves.add(new byte[]{0, this.getRow()});
        }
        if(canCastle[1]) {
            moves.add(new byte[]{7, this.getRow()});
        }
    }

    public boolean canCastle() {
        return canCastle;
    }

    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }
}
