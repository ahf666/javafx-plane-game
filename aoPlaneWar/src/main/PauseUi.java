package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import util.Tool;

public class PauseUi {
    private Stage stage = new Stage();
    private BorderPane bPane = new BorderPane();
    private Button btContinue = new Button("继续");
    private Button btExit = new Button("退出");
    private RadioButton rbShutBgMusic = new RadioButton("关闭音乐");
    private RadioButton rbShutBgSound = new RadioButton("关闭音效");

    public PauseUi(){
        HBox hBox = new HBox(btContinue,btExit);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(100);
        hBox.setPadding(new Insets(50));
        VBox vBox = new VBox(rbShutBgSound,rbShutBgMusic);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(60);
        btContinue.setPrefSize(100,40);
        btExit.setPrefSize(100,40);
        btContinue.setStyle(" -fx-background-color: cornflowerblue; -fx-background-radius: 15; -fx-text-fill: lavender");
        btExit.setStyle(" -fx-background-color: cornflowerblue; -fx-background-radius: 15; -fx-text-fill: lavender");
        btContinue.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        btExit.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        rbShutBgSound.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 40));
        rbShutBgMusic.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 40));
        bPane.setBottom(hBox);
        bPane.setCenter(vBox);
        stage = new Stage();
        stage.setTitle("暂停");
        stage.getIcons().addAll(Tool.readImg("pause.png"));
        stage.setScene(new Scene(bPane,400,500));
    }

    public Stage getStage() {
        return stage;
    }

    public Button getBtContinue() {
        return btContinue;
    }

    public Button getBtExit() {
        return btExit;
    }

    public RadioButton getRbShutBgMusic() {
        return rbShutBgMusic;
    }

    public RadioButton getRbShutBgSound() {
        return this.rbShutBgSound;
    }
}
