package eddy.chessfx.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChessBoard extends GridPane {
    private static final int SIZE = 8;

    public ChessBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle square = new Rectangle(75, 75);
                square.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);
                this.add(square, col, row);
            }
        }
        this.setGridLinesVisible(true);
    }
}
