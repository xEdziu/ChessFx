package eddy.chessfx.logic;

import eddy.chessfx.pieces.*;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private final Piece[][] board;  // The chessboard as a 8x8 array of pieces
    private final List<Move> moveHistory;  // History of moves
    private boolean whiteTurn;  // true if it is white's turn, false
    private Move lastMove;  // The last move made

    public Board() {
        this.board = new Piece[8][8];
        this.moveHistory = new ArrayList<>();
        setupInitialBoard();
        setWhiteTurn(true);
    }

    public Board(Board board) {
        this.board = new Piece[8][8];
        this.moveHistory = new ArrayList<>(board.moveHistory);
        this.whiteTurn = board.whiteTurn;
        this.lastMove = board.lastMove == null ? null  : new Move(board.lastMove.getStartX(), board.lastMove.getStartY(), board.lastMove.getEndX(), board.lastMove.getEndY(), board.lastMove.getPieceMoved(), board.lastMove.getPieceCaptured());

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    this.board[x][y] = piece.copy();
                }
            }
        }
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    private void setupInitialBoard() {
        // Set up white pieces
        board[0][0] = new eddy.chessfx.pieces.Rook(true);
        board[0][0].setPosition(0, 0);
        board[0][1] = new eddy.chessfx.pieces.Knight(true);
        board[0][1].setPosition(0, 1);
        board[0][2] = new eddy.chessfx.pieces.Bishop(true);
        board[0][2].setPosition(0, 2);
        board[0][3] = new eddy.chessfx.pieces.Queen(true);
        board[0][4] = new eddy.chessfx.pieces.King(true);
        board[0][3].setPosition(0, 3);
        board[0][4].setPosition(0, 4);
        board[0][5] = new eddy.chessfx.pieces.Bishop(true);
        board[0][5].setPosition(0, 5);
        board[0][6] = new eddy.chessfx.pieces.Knight(true);
        board[0][6].setPosition(0, 6);
        board[0][7] = new eddy.chessfx.pieces.Rook(true);
        board[0][7].setPosition(0, 7);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new eddy.chessfx.pieces.Pawn(true);
            board[1][i].setPosition(1, i);
        }

        // Set up black pieces
        board[7][0] = new eddy.chessfx.pieces.Rook(false);
        board[7][0].setPosition(7, 0);
        board[7][1] = new eddy.chessfx.pieces.Knight(false);
        board[7][1].setPosition(7, 1);
        board[7][2] = new eddy.chessfx.pieces.Bishop(false);
        board[7][2].setPosition(7, 2);
        board[7][3] = new eddy.chessfx.pieces.Queen(false);
        board[7][4] = new eddy.chessfx.pieces.King(false);
        board[7][3].setPosition(7, 3);
        board[7][4].setPosition(7, 4);
        board[7][5] = new eddy.chessfx.pieces.Bishop(false);
        board[7][5].setPosition(7, 5);
        board[7][6] = new eddy.chessfx.pieces.Knight(false);
        board[7][6].setPosition(7, 6);
        board[7][7] = new eddy.chessfx.pieces.Rook(false);
        board[7][7].setPosition(7, 7);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new eddy.chessfx.pieces.Pawn(false);
            board[6][i].setPosition(6, i);
        }
    }

    public Move getLastMove() {
        return lastMove;
    }

    public boolean makeMove(Move move) {
        if (validateMove(move)) {
            Piece piece = board[move.getStartX()][move.getStartY()];
            Piece targetPiece = board[move.getEndX()][move.getEndY()];
            board[move.getEndX()][move.getEndY()] = piece;
            board[move.getStartX()][move.getStartY()] = null;
            moveHistory.add(move);
            if (isKingInCheck(piece.isWhite())) {
                // Undo the move if it leaves the king in check
                board[move.getStartX()][move.getStartY()] = piece;
                board[move.getEndX()][move.getEndY()] = targetPiece;
                moveHistory.remove(move);
                return false;
            }
            piece.setHasMoved(true);
            lastMove = move;
            setWhiteTurn(!isWhiteTurn());
            if (piece instanceof eddy.chessfx.pieces.Pawn && Math.abs(move.getStartX() - move.getEndX()) == 1 && !isSquareOccupied(move.getEndX(), move.getEndY())){  // En passant capture
                board[move.getStartX()][move.getEndY()] = null; // Remove the captured pawn
            }
            return true;
        }
        return false;
    }

    public boolean validateMove(Move move) {
        if (!isMoveWithinBoard(move.getStartX(), move.getStartY()) ||
                !isMoveWithinBoard(move.getEndX(), move.getEndY())) {
            return false;
        }
        Piece startPiece = board[move.getStartX()][move.getStartY()];
        Piece endPiece = board[move.getEndX()][move.getEndY()];
        return startPiece != null && (endPiece == null || endPiece.isWhite() != startPiece.isWhite());
    }

    public boolean isMoveWithinBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isSquareOccupied(int x, int y) {
        return board[x][y] != null;
    }

    public Piece getPiece(int x, int y) {
        if (isMoveWithinBoard(x, y)) {
            return board[x][y];
        }
        return null;
    }

    public boolean isKingInCheck(boolean isWhite) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board[x][y];
                if (piece instanceof eddy.chessfx.pieces.King && piece.isWhite() == isWhite) {
                    return isSquareThreatened(x, y, isWhite);
                }
            }
        }
        return false;
    }

    private boolean isSquareThreatened(int x, int y, boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.isWhite() != isWhite) {
                    List<Move> possibleMoves = piece.getPossibleMoves(this, i, j);
                    for (Move move : possibleMoves) {
                        if (move.getEndX() == x && move.getEndY() == y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean castle(boolean isWhite, boolean kingSide) {
        if (isKingInCheck(isWhite)) return false;
        int row = isWhite ? 7 : 0;
        int kingCol = 4;
        int rookCol = kingSide ? 7 : 0;
        int direction = kingSide ? 1 : -1;

        Piece king = board[row][kingCol];
        Piece rook = board[row][rookCol];

        if (!(king instanceof eddy.chessfx.pieces.King) || !(rook instanceof eddy.chessfx.pieces.Rook)) return false;
        if (king.hasMoved() || rook.hasMoved()) return false;

        for (int col = Math.min(kingCol, rookCol) + 1; col < Math.max(kingCol, rookCol); col++) {
            if (board[row][col] != null || isSquareThreatened(row, col, !isWhite)) return false;
        }

        // Move the king and the rook
        board[row][kingCol] = null;
        board[row][rookCol] = null;
        board[row][kingCol + 2 * direction] = king;
        board[row][kingCol + direction] = rook;
        return true;
    }


    public boolean isCheckmate(boolean isWhite) {
        if (!isKingInCheck(isWhite)) return false;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Move> possibleMoves = piece.getPossibleMoves(this, x, y);
                    for (Move move : possibleMoves) {
                        Piece temp = board[move.getEndX()][move.getEndY()];
                        board[move.getEndX()][move.getEndY()] = piece;
                        board[x][y] = null;
                        if (!isKingInCheck(isWhite)) {
                            board[x][y] = piece;
                            board[move.getEndX()][move.getEndY()] = temp;
                            return false;
                        }
                        board[x][y] = piece;
                        board[move.getEndX()][move.getEndY()] = temp;
                    }
                }
            }
        }
        return true;
    }

    public String getGameInChessNotation() {
        StringBuilder notation = new StringBuilder();
        int moveCount = 1;
        for (int i = 0; i < moveHistory.size(); i += 2) {
            notation.append(moveCount).append(". ");
            Move whiteMove = moveHistory.get(i);
            notation.append(getMoveNotation(whiteMove));
            if (isCheckAfterMove(whiteMove)) {
                notation.append(isCheckmateAfterMove(whiteMove) ? "#" : "+");
            }
            notation.append(" ");
            if (i + 1 < moveHistory.size()) {
                Move blackMove = moveHistory.get(i + 1);
                notation.append(getMoveNotation(blackMove));
                if (isCheckAfterMove(blackMove)) {
                    notation.append(isCheckmateAfterMove(blackMove) ? "#" : "+");
                }
                notation.append(" ");
            }
            notation.append("\n");
            moveCount++;
        }

        // Add the game result
        if (isCheckmate(true)) {
            notation.append("0-1"); // Black wins
        } else if (isCheckmate(false)) {
            notation.append("1-0"); // White wins
        } else {
            notation.append("0.5-0.5"); // Draw
        }

        return notation.toString();
    }

    private boolean isCheckAfterMove(Move move) {
        Board tempBoard = new Board();
        for (int i = 0; i < moveHistory.indexOf(move); i++) {
            tempBoard.makeMove(new Move(moveHistory.get(i).getStartX(), moveHistory.get(i).getStartY(), moveHistory.get(i).getEndX(), moveHistory.get(i).getEndY(), moveHistory.get(i).getPieceMoved(), moveHistory.get(i).getPieceCaptured()));
        }
        tempBoard.makeMove(new Move(move.getStartX(), move.getStartY(), move.getEndX(), move.getEndY(), move.getPieceMoved(), move.getPieceCaptured()));
        return tempBoard.isKingInCheck(!move.getPieceMoved().isWhite());
    }

    private boolean isCheckmateAfterMove(Move move) {
        Board tempBoard = new Board();
        for (int i = 0; i < moveHistory.indexOf(move); i++) {
            tempBoard.makeMove(new Move(moveHistory.get(i).getStartX(), moveHistory.get(i).getStartY(), moveHistory.get(i).getEndX(), moveHistory.get(i).getEndY(), moveHistory.get(i).getPieceMoved(), moveHistory.get(i).getPieceCaptured()));
        }
        tempBoard.makeMove(new Move(move.getStartX(), move.getStartY(), move.getEndX(), move.getEndY(), move.getPieceMoved(), move.getPieceCaptured()));
        return tempBoard.isCheckmate(!move.getPieceMoved().isWhite());
    }

    private String getMoveNotation(Move move) {
        Piece pieceMoved = move.getPieceMoved();
        String pieceNotation = getPieceNotation(pieceMoved);
        String captureNotation = move.getPieceCaptured() != null ? "x" : "";
        String destination = getSquareNotation(move.getEndX(), move.getEndY());

        // Check for ambiguity
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getClass().equals(pieceMoved.getClass()) && piece.isWhite() == pieceMoved.isWhite() && !(x == move.getStartX() && y == move.getStartY())) {
                    List<Move> moves = piece.getPossibleMoves(this, x, y);
                    for (Move possibleMove : moves) {
                        if (possibleMove.getEndX() == move.getEndX() && possibleMove.getEndY() == move.getEndY()) {
                            // If the pieces are in the same row
                            if (move.getStartX() == x) {
                                pieceNotation += getSquareNotation(move.getStartX(), move.getStartY()).charAt(0);
                            }
                            // If the pieces are in the same column
                            else if (move.getStartY() == y) {
                                pieceNotation += getSquareNotation(move.getStartX(), move.getStartY()).charAt(1);
                            }
                            // If the pieces are in different rows and columns
                            else {
                                pieceNotation += getSquareNotation(move.getStartX(), move.getStartY()).charAt(0);
                            }
                        }
                    }
                }
            }
        }

        return pieceNotation + captureNotation + destination;
    }

    private String getPieceNotation(Piece piece) {
        return switch (piece) {
            case King ignored -> "K";
            case Queen ignored2 -> "Q";
            case Rook ignored3 -> "R";
            case Bishop ignored4 -> "B";
            case Knight  ignored5 -> "N";
            case null, default -> "";
        };
    }

    private String getSquareNotation(int x, int y) {
        return (char) ('a' + y) + Integer.toString(8 - x);
    }

    public void restartGame() {
        clearPiecesFromBoard();
        moveHistory.clear();
        setupInitialBoard();
        setWhiteTurn(true);
        lastMove = null;
    }

    private void clearPiecesFromBoard(){
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y] = null;
            }
        }
    }
}
