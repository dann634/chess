package com.jackson.main;

import com.jackson.game.Game;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage pStage) {
        stage = pStage;

        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getRandomIcon()));

        Game game = new Game();
        game.start();

    }

    private String getRandomIcon() {
        Random rand = new Random();
        String randomColour = ((rand.nextInt(2)) == 1) ? "white" : "black";
        Map<Integer, String> pieceMap = new HashMap<>();

        pieceMap.put(0, "Pawn");
        pieceMap.put(1, "Knight");
        pieceMap.put(2, "Bishop");
        pieceMap.put(3, "Rook");
        pieceMap.put(4, "Queen");
        pieceMap.put(5, "King");

        String randomPiece = pieceMap.get(rand.nextInt(6));

        return "file:src/main/resources/images/" + randomColour + randomPiece + ".png";
    }


    public static Stage getStage() {
        return stage;
    }
}
