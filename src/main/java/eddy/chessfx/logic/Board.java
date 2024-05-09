package eddy.chessfx.logic;

import eddy.chessfx.pieces.Piece;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private final Piece[][] board;  // The chessboard as a 8x8 array of pieces
    private final List<Move> moveHistory;  // History of moves

    public Board() {
        this.board = new Piece[8][8];
        this.moveHistory = new ArrayList<>();
        setupInitialBoard();
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
        board[0][3].setPosition(0, 3);
        board[0][4] = new eddy.chessfx.pieces.King(true);
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
        board[7][3].setPosition(7, 3);
        board[7][4] = new eddy.chessfx.pieces.King(false);
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

    public boolean makeMove(Move move) {
        if (validateMove(move)) {
            Piece piece = board[move.getStartX()][move.getStartY()];
            board[move.getEndX()][move.getEndY()] = piece;
            board[move.getStartX()][move.getStartY()] = null;
            moveHistory.add(move);
            piece.setHasMoved(true);
            return true;
        }
        return false;
    }

    public boolean validateMove(Move move) {
        if (!isMoveWithinBoard(move.getStartX(), move.getStartY()) ||
                !isMoveWithinBoard(move.getEndX(), move.getEndY())) {
            return false;
        }
        return board[move.getEndX()][move.getEndY()] == null ||
                board[move.getEndX()][move.getEndY()].isWhite() != board[move.getStartX()][move.getStartY()].isWhite();
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
}
