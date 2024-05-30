package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(boolean isWhite) {
        super("/images/pieces/king-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    // Dla klasy King
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
                            (!board.isSquareOccupied(newX, newY) || board.getPiece(newX, newY).isWhite() != this.isWhite())) {
                        moves.add(new Move(x, y, newX, newY, this, board.getPiece(newX, newY), null));
                    }
                }
            }
        }

        // Check for castling
        if (!this.hasMoved()) {
            //King side
            if (!board.isSquareOccupied(x + 1, y) && !board.isSquareOccupied(x + 2, y) &&
                    board.getPiece(x + 3, y) instanceof Rook && !board.getPiece(x + 3, y).hasMoved()) {
                moves.add(new Move(x, y, x + 2, y, this, board.getPiece(x + 3, y), null));
            }
            //Queen side
            if (!board.isSquareOccupied(x - 1, y) && !board.isSquareOccupied(x - 2, y) && !board.isSquareOccupied(x - 3, y) &&
                    board.getPiece(x - 4, y) instanceof Rook && !board.getPiece(x - 4, y).hasMoved()) {
                moves.add(new Move(x, y, x - 2, y, this, board.getPiece(x - 4, y), null));
            }
        }

        return moves;
    }

    @Override
    public King copy(){
        King copy = new King(isWhite());
        copy.setPosition(this.getPieceX(), this.getPieceY());
        copy.setHasMoved(this.hasMoved());
        return copy;
    }
}
