package com.jackson.main;

import com.jackson.game.Game;
import com.jackson.ui.Board;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage pStage) {
        stage = pStage;

        stage.setTitle("Chess");
        stage.setResizable(false);

        Game game = new Game();
        game.start(stage);

    }
}
