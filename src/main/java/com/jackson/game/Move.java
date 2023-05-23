package com.jackson.game;

import java.util.HashMap;
import java.util.Map;

public class Move { //Template for every move

    //💀💀💀
    private final byte[] oldPos;
    private final byte[] newPos;
    private final boolean wasPieceTaken;
    private final boolean didCheck;
    private final boolean didCheckMate;
    private final boolean didCastle;
    private final boolean didKingSide;
    private final String pieceType;
    private final Map<String, String> pieceMap;


    public Move(byte[] oldPos, byte[] newPos, String pieceType, boolean wasPieceTaken, boolean wasCastle, boolean isKingSide, boolean inCheck, boolean isCheckmate) {
        this.oldPos = oldPos;
        this.pieceType = pieceType;
        this.pieceMap = createPieceMap();
        this.didCheck = inCheck;
        this.didCheckMate = isCheckmate;
        this.wasPieceTaken = wasPieceTaken;
        this.newPos = newPos;
        this.didCastle = wasCastle;
        this.didKingSide = isKingSide;
    }

    private HashMap<String, String> createPieceMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Pawn", "");
        map.put("Bishop", "B");
        map.put("Queen", "Q");
        map.put("King", "K");
        map.put("Rook", "R");
        map.put("Knight", "N");

        return map;
    }

    private String getColumnCharacter(byte column) {
           return String.valueOf((char)('a' + column));
    }
    private String getRowCharacter() {

        return String.valueOf((-this.newPos[1] + 8));
    }

    public String generateMoveString() {
        StringBuilder moveString = new StringBuilder();

        if(this.didCastle) {
            if(this.didKingSide) {
                return "O-O";
            } else {
                return "O-O-O";
            }
        }

        moveString.append(this.pieceMap.get(this.pieceType)); // Adds piece type

        if(this.wasPieceTaken) {
            moveString.append(getColumnCharacter(this.oldPos[0]));
            moveString.append("x");
        } //was piece taken

        moveString.append(getColumnCharacter(this.newPos[0])).append(getRowCharacter()); //Pos

        if(this.didCheck) {moveString.append("+");} //Did move check
        if(this.didCheckMate) {moveString.append("+");} //Did move checkmate

        return moveString.toString();
    }



}
