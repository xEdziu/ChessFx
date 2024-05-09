package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public abstract class Piece extends ImageView {
    protected boolean isWhite;  // true for white piece, false for black piece
    protected boolean hasMoved;  // true if the piece has moved

    public Piece(String imagePath, boolean isWhite) {
        super(new Image(imagePath, 64, 64, false, false));  // Load the image of the piece
        this.isWhite = isWhite;
        this.hasMoved = false;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean isWhite() {
        return isWhite;
    }

    // Abstract method to get all possible moves for this piece
    public abstract List<Move> getPossibleMoves(Board board, int x, int y);
}
