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
            moves.add(new Move(x, y, x, y + direction, this, null, null));
            // Ruch o dwa pola do przodu, jeśli pionek jest na swoim początkowym polu
            if (!hasMoved() && !board.isSquareOccupied(x, y + 2 * direction)) {
                moves.add(new Move(x, y, x, y + 2 * direction, this, null, null));
            }
        }

        // Atak na ukos
        int[] attackDirections = {-1, 1};
        for (int attackDirection : attackDirections) {
            if (board.isMoveWithinBoard(x + attackDirection, y + direction)) {
                Piece piece = board.getPiece(x + attackDirection, y + direction);
                if (piece != null && piece.isWhite() != this.isWhite()) {
                    moves.add(new Move(x, y, x + attackDirection, y + direction, this, piece, null));
                }
            }
        }

        // Atak en passant
        Move lastMove = board.getLastMove();
        if (lastMove != null) {
            // Sprawdź, czy ostatni ruch był wykonany przez pionka przeciwnika, który przesunął się o dwa pola
            if (Math.abs(lastMove.getStartY() - lastMove.getEndY()) == 2 && lastMove.getPieceMoved() instanceof Pawn) {
                // Sprawdź, czy pionek jest na odpowiednim polu do wykonania bicia w przelocie
                if (y == (isWhite() ? 3 : 4)) {
                    // Sprawdź, czy pionek jest na polu bezpośrednio obok pionka, który wykonał ostatni ruch
                    if (x - lastMove.getEndX() == 1 || x - lastMove.getEndX() == -1) {
                        moves.add(new Move(x, y, lastMove.getEndX(), y + direction, this, lastMove.getPieceMoved(), null));
                    }
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