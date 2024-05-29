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
        // Set up black pieces
        for (int i = 0; i < 8; i++) {
            board[i][1] = new Pawn(false);
        }
        board[0][0] = new Rook(false);
        board[1][0] = new Knight(false);
        board[2][0] = new Bishop(false);
        board[3][0] = new Queen(false);
        board[4][0] = new King(false);
        board[5][0] = new Bishop(false);
        board[6][0] = new Knight(false);
        board[7][0] = new Rook(false);

        // Set up white pieces
        for (int i = 0; i < 8; i++) {
            board[i][6] = new Pawn(true);
        }
        board[0][7] = new Rook(true);
        board[1][7] = new Knight(true);
        board[2][7] = new Bishop(true);
        board[3][7] = new Queen(true);
        board[4][7] = new King(true);
        board[5][7] = new Bishop(true);
        board[6][7] = new Knight(true);
        board[7][7] = new Rook(true);

        // Set pieces' positions
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board[x][y] != null) {
                    board[x][y].setPosition(x, y);
                }
            }
        }
    }

    public Move getLastMove() {
        return lastMove;
    }

    public boolean makeMove(Move move) {
        if (validateMove(move)) {
            Piece piece = board[move.getStartX()][move.getStartY()];
            Piece targetPiece = board[move.getEndX()][move.getEndY()];

            // Check for castling
            if (piece instanceof King && Math.abs(move.getStartX() - move.getEndX()) == 2) {
                int rookX = move.getEndX() > move.getStartX() ? 7 : 0;
                int rookY = move.getStartY();
                Piece rook = board[rookX][rookY];
                board[move.getEndX() > move.getStartX() ? 5 : 3][rookY] = rook;
                board[rookX][rookY] = null;
                rook.setPosition(move.getEndX() > move.getStartX() ? 5 : 3, rookY);
                rook.setHasMoved(true);
                piece.setHasMoved(true);
                move.setCastlingMove(true);
            }

            board[move.getEndX()][move.getEndY()] = piece;
            board[move.getStartX()][move.getStartY()] = null;
            moveHistory.add(move);
            if (isKingInCheck(piece.isWhite())) {
                // Undo the move if it leaves the king in check
                board[move.getStartX()][move.getStartY()] = piece;
                board[move.getEndX()][move.getEndY()] = targetPiece;
                moveHistory.remove(move);
                piece.setHasMoved(false);
                return false;
            }
            piece.setHasMoved(true);
            lastMove = move;
            setWhiteTurn(!isWhiteTurn());

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
            if (piece instanceof King && piece.isWhite() == isWhite) {
                return isSquareThreatened(x, y, !isWhite);
            }
        }
    }
    return false;
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

    private boolean isSquareThreatened(int x, int y, boolean isWhite) {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Piece piece = getPiece(i, j);
            if (piece != null && piece.isWhite() == isWhite) {
                if (!(piece instanceof King)) {
                    List<Move> moves = piece.getPossibleMoves(this, i, j);
                    for (Move move : moves) {
                        if (move.getEndX() == x && move.getEndY() == y) {
                            return true;
                        }
                    }
                }
            }
        }
    }
    return false;
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

        // Check for castling
        if (pieceMoved instanceof King && Math.abs(move.getStartX() - move.getEndX()) == 2) {
            return move.getEndX() > move.getStartX() ? "O-O" : "O-O-O";
        }

        // Check for ambiguity
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = getPiece(x, y);
                if (piece == null || !piece.getClass().equals(pieceMoved.getClass()) || piece.isWhite() != pieceMoved.isWhite() || x == move.getStartX() && y == move.getStartY()) {
                    continue;
                }
                List<Move> moves = piece.getPossibleMoves(this, x, y);
                for (Move possibleMove : moves) {
                    if (possibleMove.getEndX() != move.getEndX() || possibleMove.getEndY() != move.getEndY()) {
                        continue;
                    }
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

    public void removePiece(int x, int y) {
        board[x][y] = null;
    }

    private void clearPiecesFromBoard(){
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y] = null;
            }
        }
    }
}
