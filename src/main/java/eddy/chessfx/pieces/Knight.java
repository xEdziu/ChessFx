package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(boolean isWhite) {
        super("../resources/eddy/chessfx/pieces/" + (isWhite ? "white-knight.png" : "black-knight.png") + ".png", isWhite);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int[][] movesPattern = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for (int[] move : movesPattern) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (board.isMoveWithinBoard(newX, newY) &&
                    (!board.isSquareOccupied(newX, newY) || board.getPiece(newX, newY).isWhite() != this.isWhite)) {
                moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY)));
            }
        }
        return moves;
    }
}
