package com.jackson.game;

import java.util.HashMap;
import java.util.Map;

public class Move { //Template for every move

    private byte[] oldPos;
    private byte[] newPos;
    private boolean wasPieceTaken;
    private boolean didCheck;
    private boolean didCheckMate;
    private String pieceType;

    private Map<String, String> pieceMap;


    public Move(byte[] oldPos, String pieceType) {
        this.oldPos = oldPos;
        this.pieceType = pieceType;
        this.pieceMap = createPieceMap();

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

    private String getPosString() {
        byte column = this.newPos[0];
        String characterColumn = String.valueOf((char) (('h') - column));
        return characterColumn + this.newPos[1];
    }

    public String generateMoveString() {
        StringBuilder moveString = new StringBuilder();
        moveString.append(this.pieceMap.get(this.pieceType)); // Adds piece type
        moveString.append(getPosString());

        return moveString.toString();
    }


    public void addNewPos(byte[] pos) {
        this.newPos = pos;
    }



}
