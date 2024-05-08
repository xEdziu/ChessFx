package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(boolean isWhite) {
        super("../resources/eddy/chessfx/pieces/" + (isWhite ? "white-queen.png" : "black-queen.png") + ".png", isWhite);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};  // All 8 possible directions

        for (int[] dir : directions) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dir[0];
                newY += dir[1];
                if (!board.isMoveWithinBoard(newX, newY)) break;
                if (board.isSquareOccupied(newX, newY)) {
                    if (board.getPiece(newX, newY).isWhite() != this.isWhite) {
                        moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY)));
                    }
                    break;
                }
                moves.add(new Move(x, y, newX, newY, this, null));
            }
        }
        return moves;
    }
}
