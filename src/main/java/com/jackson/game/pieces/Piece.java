package com.jackson.game.pieces;

import com.jackson.game.Game;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Piece {

    protected byte row;
    protected byte column;
    private final boolean isWhite;
    private final ImageView imageView;

    public Piece(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;
        this.imageView = initImageView();
    }

    //Moves
    protected abstract List<byte[]> getAllMoves();

    public abstract List<byte[]> getValidMoves(Piece[][] board);

    public abstract List<byte[]> getSquaresProtected(Piece[][] board);

    public List<byte[]> getCheckMoves(Piece[][] board, Piece checkingPiece) {
        //Implementation is the same for all pieces
        List<byte[]> moves = getValidMoves(board);

        //Update pos and look for check
        //Maybe make looking for check a separate function in game (pass the colour)
        Piece targetPos;
        byte originalColumn = this.getColumn();
        byte originalRow = this.getRow();
        King enemyKing = Game.getKing(isWhite());
        boolean checkBlocked;

        List<byte[]> validMoves = new ArrayList<>();

        for (byte[] move : moves) {
            targetPos = board[move[0]][move[1]]; //Makes sure piece isn't deleted
            board[move[0]][move[1]] = this; //Moves checkingPiece temporarily
            this.setColumn(move[0]);
            this.setRow(move[1]);
            checkBlocked = true;

            if(move[0] == checkingPiece.getColumn() && move[1] == checkingPiece.getRow()) {
                validMoves.add(move);
            }

            //Check for check
            List<byte[]> checkMoves = checkingPiece.getValidMoves(board);
            for (byte[] checkMove : checkMoves) {
                if (checkMove[0] == enemyKing.getColumn() && checkMove[1] == enemyKing.getRow()) {
                    checkBlocked = false;
                }
            }

            if (checkBlocked) {
                validMoves.add(move);
            }
            board[originalColumn][originalRow] = this;
            board[move[0]][move[1]] = targetPos;
        }

        this.setColumn(originalColumn);
        this.setRow(originalRow);

        return validMoves;

    }

    public void removeMovesFromPin(Piece[][] board, List<byte[]> moves) {

        List<byte[]> invalidMoves = new ArrayList<>();

        byte originalColumn = this.getColumn();
        byte originalRow = this.getRow();
        Piece targetPos;

        for(byte[] move : moves) { //Very inefficient
            this.setColumn(move[0]);
            this.setRow(move[1]);
            targetPos = board[move[0]][move[1]];
            board[move[0]][move[1]] = this;

            King king = Game.getKing(this.isWhite);

            Set<byte[]> enemyMoves = Game.getAllEnemyMoves(this.isWhite, board);
            for(byte[] enemyMove : enemyMoves) {
                if(enemyMove[0] == king.getColumn() && enemyMove[1] == king.getRow()) {
                    invalidMoves.add(move);
                }
            }


            board[originalColumn][originalRow] = this;
            board[move[0]][move[1]] = targetPos;

        }

        this.setRow(originalRow);
        this.setColumn(originalColumn);

        moves.removeAll(invalidMoves);
    }

    protected List<byte[]> generateLinearMoves(List<byte[]> offsets, Piece[][] board, boolean includeProtected) {
        List<byte[]> moves = new ArrayList<>();

        byte rowOffset = 0;
        byte columnOffset = 0;
        Piece targetPiece = null;

        for(byte[] offset : offsets) {
            columnOffset = offset[0];
            rowOffset = offset[1];
            byte newColumn;
            byte newRow;
            boolean isLineValid = true;
            byte counter = 1;
            do {

                newColumn = (byte) (this.column + (columnOffset * counter));
                newRow = (byte) (this.row + (rowOffset * counter));

                if(newColumn < 0 || newColumn > 7 || newRow < 0 || newRow > 7) {
                    isLineValid = false;
                } else {
                    targetPiece = board[newColumn][newRow];
                    if (targetPiece != null) {
                        if(includeProtected) {
                            moves.add(new byte[]{newColumn, newRow});
                        } else if(!isPieceSameColour(this, targetPiece)) {
                            moves.add(new byte[]{newColumn, newRow});
                        }
                    } else {
                        moves.add(new byte[]{newColumn, newRow});
                    }
                }

                counter++;

            } while (isLineValid && board[newColumn][newRow] == null);
        }
        return moves;
    }

    protected void areMovesOnBoard(List<byte[]> moves) {
        moves.removeIf(n -> n[0] < 0 || n[0] > 7 || n[1] < 0 || n[1] > 7);
    }

    protected void removeCellsOccupiedByFriendly(Piece[][] board, List<byte[]> moves) {
        List<byte[]> invalidMoves = new ArrayList<>();
        for(byte[] move : moves) {
            if(board[move[0]][move[1]] != null && isPieceSameColour(this, board[move[0]][move[1]])) {
                invalidMoves.add(move);
            }
        }
        moves.removeAll(invalidMoves);
    }

    //State

    public byte getRow() {
        return this.row;
    }

    public void setRow(byte row) {
        if(row >= 0 && row <= 7) {
            this.row = row;
        }
    }

    public byte getColumn() {
        return this.column;
    }

    public void setColumn(byte column) {
        if(column >= 0 && column <= 7) {
            this.column = column;
        }
    }

    public boolean isWhite() {
        return isWhite;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private ImageView initImageView() {
        String colour = this.isWhite ? "white" : "black";
        String filePath = "file:src/main/resources/images/" + colour + this.getClass().getSimpleName() + ".png";
        ImageView imageView = new ImageView(new Image(filePath));
        imageView.setFitHeight(75);
        imageView.setFitWidth(75);
        imageView.toBack();
        imageView.setMouseTransparent(true);
        return imageView;
    }

    protected boolean isPieceSameColour(Piece piece1, Piece piece2) {

        if(piece1 == null || piece2 == null) {
            return false;
        }

        if(piece1.isWhite == piece2.isWhite) {
            return true;
        }
        return false;
    }


}
