package eddy.chessfx.ui;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChessApplication extends Application {
    private boolean isPlayerWhite;
    private ChessBoard chessBoard;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ChessFX");
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/king-w.png")));

        StackPane root = new StackPane();

        Board board = new Board();

        List<String> gameModeChoices = Arrays.asList("Player vs Player", "Player vs AI");
        ChoiceDialog<String> gameModeDialog = new ChoiceDialog<>("Player vs Player", gameModeChoices);
        gameModeDialog.setTitle("ChessFX");
        gameModeDialog.setHeaderText("Choose a game mode:");
        gameModeDialog.setContentText("Choose your game mode:");
        Stage gameModeStage = (Stage) gameModeDialog.getDialogPane().getScene().getWindow();
        gameModeStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/king-w.png")));


        Optional<String> gameModeResult = gameModeDialog.showAndWait();
        String gameMode = gameModeResult.orElse("Player vs Player");

        if (gameMode.equals("Player vs AI")) {
            List<String> colorChoices = Arrays.asList("White", "Black");
            ChoiceDialog<String> colorDialog = new ChoiceDialog<>("White", colorChoices);
            colorDialog.setTitle("ChessFX");
            colorDialog.setHeaderText("Choose your color:");
            colorDialog.setContentText("Choose your color:");
            Stage colorStage = (Stage) colorDialog.getDialogPane().getScene().getWindow();
            colorStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/king-w.png")));

            Optional<String> colorResult = colorDialog.showAndWait();
            String color = colorResult.orElse("White");

            isPlayerWhite = color.equals("White");
        }

        chessBoard = new ChessBoard(board, isPlayerWhite, gameMode.equals("Player vs Player"));
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (chessBoard.getIsCheckmate()) {
                    System.out.println("Checkmate");
                    stop();
                    Platform.runLater(() -> {
                        chessBoard.showEndGamePopup(chessBoard.winnerString);
                    });
                    return;
                }
                if (!chessBoard.isPlayerMove() && gameMode.equals("Player vs AI")) {
                    //ai logic here
                    System.out.println("AI move");
                    chessBoard.setPlayerMove(true);
                }
            }
        };
        chessBoard.setAlignment(Pos.CENTER);

        root.getChildren().add(chessBoard);
        root.setPadding(new Insets(10));
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        primaryStage.setScene(new Scene(root));

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(700);

        primaryStage.show();
        if (gameMode.equals("Player vs AI")) {
            gameLoop.start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}