package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(boolean isWhite) {
        super("/images/pieces/king-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int[] possibleMoves = {-1, 0, 1};

        for (int dx : possibleMoves) {
            for (int dy : possibleMoves) {
                if (dx != 0 || dy != 0) {
                    int newX = x + dx;
                    int newY = y + dy;
                    if (board.isMoveWithinBoard(newX, newY) &&
                            (!board.isSquareOccupied(newX, newY) || board.getPiece(newX, newY).isWhite() != this.isWhite)) {
                        moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY)));
                    }
                }
            }
        }
        return moves;
    }
}
