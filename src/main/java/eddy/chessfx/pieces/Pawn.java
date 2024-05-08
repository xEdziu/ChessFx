package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super("../resources/eddy/chessfx/pieces/" + (isWhite ? "white-pawn.png" : "black-pawn.png") + ".png", isWhite);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int direction = isWhite() ? 1 : -1;  // White pawns move up, black pawns move down

        // Move forward by one square if the square is empty
        if (board.isMoveWithinBoard(x, y + direction) && !board.isSquareOccupied(x, y + direction)) {
            moves.add(new Move(x, y, x, y + direction, this, null));
            // Move forward by two squares if the pawn is on its starting square
            if ((isWhite() && y == 1) || (!isWhite() && y == 6)) {
                if (!board.isSquareOccupied(x, y + 2 * direction)) {
                    moves.add(new Move(x, y, x, y + 2 * direction, this, null));
                }
            }
        }

        // Attack diagonally
        int[] attackDirections = {-1, 1};
        for (int attackDirection : attackDirections) {
            int attackX = x + attackDirection;
            int attackY = y + direction;
            if (board.isMoveWithinBoard(attackX, attackY) && board.isSquareOccupied(attackX, attackY)) {
                Piece potentialCapture = board.getPiece(attackX, attackY);
                if (potentialCapture.isWhite() != this.isWhite()) {  // Possible capture
                    moves.add(new Move(x, y, attackX, attackY, this, potentialCapture));
                }
            }
        }

        return moves;
    }
}
