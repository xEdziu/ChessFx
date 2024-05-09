package eddy.chessfx.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import eddy.chessfx.logic.Board;
import eddy.chessfx.pieces.Piece;

public class ChessBoard extends GridPane {
    private static final int SIZE = 8;
    private static final int SQUARE_SIZE = 75;
    private final Board chessBoard;

    public ChessBoard(Board chessBoard) {
        this.chessBoard = chessBoard;
        drawBoard();
        placePieces();
    }

    private void drawBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane cell = new StackPane();

                // Create a chessboard square as a Rectangle
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.rgb(0, 0, 0, 0.5));

                // Add Rectangle to StackPane
                cell.getChildren().add(square);

                // Add StackPane to GridPane
                this.add(cell, col, row);
            }
        }
        this.setGridLinesVisible(true);
    }

    private void placePieces() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece piece = chessBoard.getPiece(col, row);
                if (piece != null) {
                    ImageView imageView = new ImageView(piece.getImage());
                    imageView.setFitWidth(SQUARE_SIZE);
                    imageView.setFitHeight(SQUARE_SIZE);

                    // Retrieve the appropriate StackPane and add ImageView
                    StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col);
                    if (cell != null) {
                        cell.getChildren().add(imageView);
                    }
                }
            }
        }
    }

    private StackPane getNodeByRowColumnIndex(int row, int column) {
        for (javafx.scene.Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null; // if not found
    }
}
