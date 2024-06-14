package eddy.chessfx.logic;

import eddy.chessfx.pieces.*;
import java.util.ArrayList;
import java.util.List;

public class AI {

    private static final int MAX_DEPTH = 2;  // Ustaw mniejszą głębokość, np. 2 lub 3
    private static final int CHECKMATE_VALUE = 1000000;

    public static Move findBestMove(Board board, boolean isWhite) {
        long timeStart = System.currentTimeMillis();
        Board boardCopy = new Board(board);  // Pobranie kopii tablicy tylko raz
        Move aiMove = alphaBeta(boardCopy, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhite, isWhite).move;
        long timeEnd = System.currentTimeMillis();
        System.out.println("AI's move: " + aiMove.getPieceMoved().getClass().getSimpleName() + " from "
                + aiMove.getStartX() + ", " + aiMove.getStartY() + " to " + aiMove.getEndX() + ", " + aiMove.getEndY() + " in " + (timeEnd - timeStart) + " ms");
        return aiMove;
    }

    private static MoveEvaluation alphaBeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isWhite) {
        if (depth == 0 || board.isCheckmate(!isWhite) || board.isCheckmate(isWhite)) {
            return new MoveEvaluation(evaluateBoard(board, isWhite), null);
        }

        List<Move> moves = getAllPossibleMoves(board, maximizingPlayer);
        Move bestMove = null;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                int eval = alphaBeta(tempBoard, depth - 1, alpha, beta, false, isWhite).evaluation;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return new MoveEvaluation(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                int eval = alphaBeta(tempBoard, depth - 1, alpha, beta, true, isWhite).evaluation;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return new MoveEvaluation(minEval, bestMove);
        }
    }

    private static List<Move> getAllPossibleMoves(Board board, boolean isWhite) {
        List<Move> captureMoves = new ArrayList<>();
        List<Move> nonCaptureMoves = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.isWhite() == isWhite) {
                    for (Move move : piece.getPossibleMoves(board, x, y)) {
                        if (move.getPieceCaptured() != null) {
                            captureMoves.add(move);
                        } else {
                            nonCaptureMoves.add(move);
                        }
                    }
                }
            }
        }
        // Najpierw zwracamy ruchy bijące, potem pozostałe ruchy
        captureMoves.addAll(nonCaptureMoves);
        return captureMoves;
    }

    private static int evaluateBoard(Board board, boolean isWhite) {
        if (board.isCheckmate(!isWhite)) {
            return CHECKMATE_VALUE;  // Checkmate in favor of the current player
        }
        if (board.isCheckmate(isWhite)) {
            return -CHECKMATE_VALUE;  // Checkmate against the current player
        }

        int evaluation = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    evaluation += pieceValue(piece);
                    evaluation += positionValue(piece, x, y);
                }
            }
        }
        evaluation += controlCenter(board, isWhite);
        evaluation += development(board, isWhite);
        evaluation += kingSafety(board, isWhite);
        return isWhite ? evaluation : -evaluation;
    }

    private static int controlCenter(Board board, boolean isWhite) {
        // Heurystyka kontroli centrum planszy
        int control = 0;
        int[][] centerSquares = {{3, 3}, {3, 4}, {4, 3}, {4, 4}};
        for (int[] square : centerSquares) {
            Piece piece = board.getPiece(square[0], square[1]);
            if (piece != null) {
                control += piece.isWhite() == isWhite ? 10 : -10;
            }
        }
        return control;
    }

    private static int development(Board board, boolean isWhite) {
        // Heurystyka rozwoju figur (na początku gry bardziej wartościowe są figury w grze)
        int development = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.isWhite() == isWhite) {
                    if (piece instanceof Knight || piece instanceof Bishop) {
                        development += 5;
                    }
                }
            }
        }
        return development;
    }

    private static int kingSafety(Board board, boolean isWhite) {
        // Heurystyka bezpieczeństwa króla
        int safety = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    safety += (x > 1 && x < 6) ? -20 : 20;  // Król bezpieczniejszy, gdy jest w rogu
                }
            }
        }
        return safety;
    }

    private static int pieceValue(Piece piece) {
        if (piece instanceof Pawn) return 100;
        if (piece instanceof Knight || piece instanceof Bishop) return 300;
        if (piece instanceof Rook) return 500;
        if (piece instanceof Queen) return 900;
        if (piece instanceof King) return 10000;
        return 0;
    }

    private static int positionValue(Piece piece, int x, int y) {
        int index = y * 8 + x;
        if (piece instanceof Pawn) {
            return piece.isWhite() ? BoardValues.PAWN[63 - index] : BoardValues.PAWN[index];
        }
        if (piece instanceof Knight) {
            return piece.isWhite() ? BoardValues.KNIGHT[63 - index] : BoardValues.KNIGHT[index];
        }
        if (piece instanceof Bishop) {
            return piece.isWhite() ? BoardValues.BISHOP[63 - index] : BoardValues.BISHOP[index];
        }
        if (piece instanceof Rook) {
            return piece.isWhite() ? BoardValues.ROOK[63 - index] : BoardValues.ROOK[index];
        }
        if (piece instanceof Queen) {
            return piece.isWhite() ? BoardValues.QUEEN[63 - index] : BoardValues.QUEEN[index];
        }
        if (piece instanceof King) {
            return piece.isWhite() ? BoardValues.KING[63 - index] : BoardValues.KING[index];
        }
        return 0;
    }

    private static class MoveEvaluation {
        int evaluation;
        Move move;

        MoveEvaluation(int evaluation, Move move) {
            this.evaluation = evaluation;
            this.move = move;
        }
    }
}
