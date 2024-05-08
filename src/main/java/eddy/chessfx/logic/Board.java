package eddy.chessfx.logic;

import eddy.chessfx.pieces.*;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private Piece[][] board;  // The chessboard as a 8x8 array of pieces
    private List<Move> moveHistory;  // History of moves

    public Board() {
        this.board = new Piece[8][8];
        this.moveHistory = new ArrayList<>();
        setupInitialBoard();
    }

    // Set up initial pieces on the board
    private void setupInitialBoard() {
        // Empty initialization for now
    }

    // Perform a move
    public boolean makeMove(Move move) {
        if (validateMove(move)) {
            Piece piece = board[move.getStartX()][move.getStartY()];
            board[move.getEndX()][move.getEndY()] = piece;
            board[move.getStartX()][move.getStartY()] = null;
            moveHistory.add(move);
            return true;
        }
        return false;
    }

    // Validate a move
    private boolean validateMove(Move move) {
        if (!isMoveWithinBoard(move.getStartX(), move.getStartY()) ||
                !isMoveWithinBoard(move.getEndX(), move.getEndY())) {
            return false;
        }
        if (board[move.getEndX()][move.getEndY()] != null &&
                board[move.getEndX()][move.getEndY()].isWhite() == board[move.getStartX()][move.getStartY()].isWhite()) {
            return false;
        }
        return true;
    }

    // Check if coordinates are within the board
    public boolean isMoveWithinBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    // Check if the king is in check
    public boolean isKingInCheck(boolean isWhite) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board[x][y] instanceof King && board[x][y].isWhite() == isWhite) {
                    return isSquareThreatened(x, y, isWhite);
                }
            }
        }
        return false;
    }

    // Check if a square is threatened by an opponent's piece
    private boolean isSquareThreatened(int x, int y, boolean isWhite) {
        // Logic to check if any opponent's pieces can move to (x, y)
        return false;  // Placeholder
    }

    // Method to check if a square is occupied
    public boolean isSquareOccupied(int x, int y) {
        return board[x][y] != null;
    }

    // Method to get a piece on the board
    public Piece getPiece(int x, int y) {
        if (isMoveWithinBoard(x, y)) {
            return board[x][y];
        }
        return null;
    }


}
