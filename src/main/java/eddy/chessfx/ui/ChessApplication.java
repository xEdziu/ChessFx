package eddy.chessfx.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class ChessApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ChessFx");

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}