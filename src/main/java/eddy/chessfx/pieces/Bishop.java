package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super("/images/pieces/bishop-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    // Dla klasy Bishop
    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};  // Diagonal directions

        for (int[] dir : directions) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dir[0];
                newY += dir[1];
                if (!board.isMoveWithinBoard(newX, newY)) break;
                if (board.isSquareOccupied(newX, newY)) {
                    if (board.getPiece(newX, newY).isWhite() != this.isWhite()) {
                        moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY), null));
                    }
                    break;
                }
                moves.add(new Move(x, y, newX, newY, this, null, null));
            }
        }
        return moves;
    }

    @Override
    public  Bishop copy(){
        Bishop copy = new Bishop(this.isWhite());
        copy.setPosition(this.getPieceX(), this.getPieceY());
        copy.setHasMoved(this.hasMoved());
        return copy;
    }
}
