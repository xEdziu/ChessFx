package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super("/images/pieces/pawn-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    // Dla klasy Pawn
    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        // Pionki białe poruszają się w górę, czarne w dół
        int direction = isWhite() ? -1 : 1;

        // Ruch o jedno pole do przodu, jeśli pole jest puste
        if (board.isMoveWithinBoard(x, y + direction) && !board.isSquareOccupied(x, y + direction)) {
            moves.add(new Move(x, y, x, y + direction, this, null));
            // Ruch o dwa pola do przodu, jeśli pionek jest na swoim początkowym polu
            if (!hasMoved() && !board.isSquareOccupied(x, y + 2 * direction)) {
                moves.add(new Move(x, y, x, y + 2 * direction, this, null));
            }
        }

        // Atak na ukos
        int[] attackDirections = {-1, 1};
        for (int attackDirection : attackDirections) {
            if (board.isMoveWithinBoard(x + attackDirection, y + direction)) {
                Piece piece = board.getPiece(x + attackDirection, y + direction);
                if (piece != null && piece.isWhite() != this.isWhite()) {
                    moves.add(new Move(x, y, x + attackDirection, y + direction, this, piece));
                }
            }
        }

        // Atak en passant
        Move lastMove = board.getLastMove();
        if (lastMove != null) {
            Piece lastMovedPiece = lastMove.getPieceMoved();
            if (lastMovedPiece instanceof Pawn && Math.abs(lastMove.getStartX() - lastMove.getEndX()) == 2) {
                if (lastMove.getStartX() == x && (lastMove.getEndY() == y - 1 || lastMove.getEndY() == y + 1) && lastMovedPiece.isWhite() != this.isWhite()) {  // Pionek jest na sąsiednim polu, jest przeciwnego koloru i jest na tym samym rzędzie
                    //TODO: Popraw ruch ataku en passant
                    moves.add(new Move(x, y, x + direction, lastMove.getEndY() - direction, this, lastMovedPiece));
                }
            }
        }
        return moves;
    }

    @Override
     public Pawn copy() {
        Pawn copy = new Pawn(this.isWhite());
        copy.setPosition(this.getPieceX(), this.getPieceY());
        copy.setHasMoved(this.hasMoved());
        return copy;
    }
}