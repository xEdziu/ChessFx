package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super("/images/pieces/rook-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    // Dla klasy Rook
    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};  // Ruch w linii prostej

        for (int[] dir : directions) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dir[0];
                newY += dir[1];
                if (!board.isMoveWithinBoard(newX, newY)) break;
                if (board.isSquareOccupied(newX, newY)) {
                    if (board.getPiece(newX, newY).isWhite() != this.isWhite()) {
                        moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY)));
                    }
                    break;
                }
                moves.add(new Move(x, y, newX, newY, this, null));
            }
        }
        return moves;
    }

    @Override
    public Rook copy(){
        Rook copy = new Rook(this.isWhite);
        copy.setPosition(this.getPieceX(), this.getPieceY());
        copy.setHasMoved(this.hasMoved());
        return copy;
    }
}
