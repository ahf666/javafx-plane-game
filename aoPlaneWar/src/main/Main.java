package main;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Tool;

public class Main extends Application {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Game game = new Game();
        game.start();
        game.setCursor(Cursor.HAND);
        Scene scene = new Scene(game, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("aoPlaneWar优化版");
        primaryStage.getIcons().add(Tool.readImg("planeHero.png"));
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event1 -> {
            game.close();
            game.pauseUi.getStage().close();
        });
    }
}
