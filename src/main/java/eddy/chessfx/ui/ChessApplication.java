package eddy.chessfx.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class ChessApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ChessFx");

        StackPane root = new StackPane();  // StackPane automatically centers its children
        root.setPrefSize(700, 700);
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.setAlignment(Pos.CENTER);  // Center chess board

        root.getChildren().add(chessBoard);  // Add chess board directly to the StackPane
        root.setPadding(new Insets(10));  // Set padding
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));  // Set background color
        primaryStage.setScene(new Scene(root));

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(700);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
