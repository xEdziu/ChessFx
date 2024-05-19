package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super("/images/pieces/pawn-" + (isWhite ? "w" : "b") + ".svg", isWhite);
    }

    @Override
    public List<Move> getPossibleMoves(Board board, int x, int y) {
        List<Move> moves = new ArrayList<>();
        // White pawns move right, black pawns move left
        int direction = isWhite() ? 1 : -1;

        // Move forward by one square if the square is empty
        if (board.isMoveWithinBoard(x + direction, y) && !board.isSquareOccupied(x+ direction, y )) {
            moves.add(new Move(x, y, x + direction, y, this, null));
            // Move forward by two squares if the pawn is on its starting square
            if ((isWhite() && x == 1) || (!isWhite() && x == 6)) {
                if (!board.isSquareOccupied(x + 2 * direction, y )) {
                    moves.add(new Move(x, y, x + 2 * direction, y , this, null));
                }
            }
        }

        // Attack diagonally
        int[] attackDirections = {-1, 1};
        for (int attackDirection : attackDirections) {
            int attackX = x + direction;
            int attackY = y + attackDirection;
            if (board.isMoveWithinBoard(attackX, attackY) && board.isSquareOccupied(attackX, attackY)) {
                Piece potentialCapture = board.getPiece(attackX, attackY);
                if (potentialCapture.isWhite() != this.isWhite()) {  // Possible capture
                    moves.add(new Move(x, y, attackX, attackY, this, potentialCapture));
                }
            }
        }

    // En passant capture
    Move lastMove = board.getLastMove();
    if (lastMove != null) {
        Piece lastMovedPiece = lastMove.getPieceMoved();
        if (lastMovedPiece instanceof Pawn && Math.abs(lastMove.getStartX() - lastMove.getEndX()) == 2) {
            if (lastMove.getStartX() == x && (lastMove.getEndY() == y - 1 || lastMove.getEndY() == y + 1) && lastMovedPiece.isWhite() != this.isWhite()) {  // Pawn is on a neighboring file, is of opposite color and is on the same row
                //TODO: Fix movement of en passant capture
                moves.add(new Move(x, y, x + direction, lastMove.getEndY() - direction, this, lastMovedPiece));
            }
        }
    }

        return moves;
    }
}