package eddy.chessfx.ui;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import eddy.chessfx.pieces.Piece;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class ChessBoard extends GridPane {
    private static final int SIZE = 8;
    private static final int SQUARE_SIZE = 75;
    private final Board chessBoard;
    private final Rectangle[][] squares = new Rectangle[SIZE][SIZE];
    private final Glow glow = new Glow(0.5);
    private Piece selectedPiece = null;  // Add this line

    public ChessBoard(Board chessBoard) {
        this.chessBoard = chessBoard;
        drawBoard();
        placePieces();
        setupSquareClickHandlers();  // Add this line
    }

    private void drawBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.GRAY : Color.WHITE);
                squares[row][col] = square;
                cell.getChildren().add(square);
                this.add(cell, col, row);
            }
        }
        this.setGridLinesVisible(true);
    }

    private void placePieces() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece piece = chessBoard.getPiece(col, row);
                if (piece != null) {
                    StackPane cell = getNodeByRowColumnIndex(row, col);
                    cell.getChildren().add(piece);
                    setupClickHandlers(piece);
                }
            }
        }
    }


    private void setupClickHandlers(Piece piece) {
        piece.setOnMouseClicked(event -> {
            if (piece.isWhite() == chessBoard.isWhiteTurn()) {
                System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
                System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
                resetBoardColors();
                highlightPossibleMoves(piece);
                selectedPiece = piece;
                System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
            } else if (selectedPiece != null) {
                Move proposedMove = new Move(selectedPiece.getPieceX(), selectedPiece.getPieceY(), piece.getPieceX(), piece.getPieceY(), selectedPiece, piece);
                List<Move> possibleMoves = selectedPiece.getPossibleMoves(chessBoard, selectedPiece.getPieceX(), selectedPiece.getPieceY());
                if (possibleMoves.contains(proposedMove)) {
                    System.out.println("Piece captured: " + piece.getClass().getSimpleName());
                    movePiece(selectedPiece, piece.getPieceX(), piece.getPieceY());
                    resetBoardColors();
                    selectedPiece = null;
                }
            }
            event.consume();
        });
    }

private void setupSquareClickHandlers() {
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            Rectangle square = squares[row][col];
            int finalCol = col;
            int finalRow = row;
            square.setOnMouseClicked(event -> {
                System.out.println("Square clicked: " + finalCol + ", " + finalRow); // Log the clicked square
                Piece piece = chessBoard.getPiece(finalCol, finalRow);
                if (selectedPiece != null) {
                    if (piece == selectedPiece) {
                        System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
                        System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
                        resetBoardColors();
                        highlightPossibleMoves(piece);
                        selectedPiece = piece;
                        System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
                    } else if (square.getEffect() != null) {
                        if (chessBoard.isSquareOccupied(finalCol, finalRow)) {
                            System.out.println("Piece captured: " + chessBoard.getPiece(finalCol, finalRow).getClass().getSimpleName());
                        }
                        movePiece(selectedPiece, finalCol, finalRow);
                        resetBoardColors();
                        selectedPiece = null;
                    }
                } else if (piece != null && piece.isWhite() == chessBoard.isWhiteTurn()) {
                    System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
                    System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
                    resetBoardColors();
                    highlightPossibleMoves(piece);
                    selectedPiece = piece;
                    System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
                }
                event.consume();
            });
        }
    }
}

//private void setupClickHandlers(Piece piece) {
//    piece.setOnMouseClicked(event -> {
//        if (piece.isWhite() == chessBoard.isWhiteTurn()) {
//            System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
//            System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
//            resetBoardColors();
//            highlightPossibleMoves(piece);
//            selectedPiece = piece;
//            System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
////            event.consume();
//        } else if (selectedPiece != null) {
//            Move proposedMove = new Move(selectedPiece.getPieceX(), selectedPiece.getPieceY(), piece.getPieceX(), piece.getPieceY(), selectedPiece, piece);
//            List<Move> possibleMoves = selectedPiece.getPossibleMoves(chessBoard, selectedPiece.getPieceX(), selectedPiece.getPieceY());
//            System.out.println("Proposed move: " + proposedMove);
//            System.out.println("Possible moves: " + possibleMoves);
//            if (possibleMoves.contains(proposedMove)) {
//                System.out.println("Piece captured: " + piece.getClass().getSimpleName());
//                movePiece(selectedPiece, piece.getPieceX(), piece.getPieceY());
//                resetBoardColors();
//                selectedPiece = null;
//                System.out.println("Selected piece reset to null after capturing");
//            } else {
//                System.out.println("Proposed move not in possible moves");
//            }
////            event.consume();
//        } else {
//            System.out.println("No piece selected yet");
//        }
//    });
//}
//
//private void setupSquareClickHandlers() {
//    for (int row = 0; row < SIZE; row++) {
//        for (int col = 0; col < SIZE; col++) {
//            Rectangle square = squares[row][col];
//            int finalCol = col;
//            int finalRow = row;
//            square.setOnMouseClicked(event -> {
//                System.out.println("Square clicked: " + finalCol + ", " + finalRow); // Log the clicked square
//                if (selectedPiece != null && square.getEffect() != null) {
//                    if (chessBoard.isSquareOccupied(finalCol, finalRow)) {
//                        System.out.println("Piece captured: " + chessBoard.getPiece(finalCol, finalRow).getClass().getSimpleName());
//                    }
//                    movePiece(selectedPiece, finalCol, finalRow);
//                    resetBoardColors();
//                    selectedPiece = null;
//                }
//                event.consume();
//            });
//        }
//    }
//}

    public void highlightPossibleMoves(Piece piece) {
        List<Move> moves = piece.getPossibleMoves(chessBoard, piece.getPieceX(), piece.getPieceY());
        for (Move move : moves) {
            Rectangle targetSquare = squares[move.getEndY()][move.getEndX()];
            targetSquare.setEffect(glow);
            targetSquare.setFill(chessBoard.isSquareOccupied(move.getEndX(), move.getEndY()) ? Color.rgb(255, 0, 0, 0.15) : Color.rgb(0, 255, 0, 0.15));
        }
    }

private void movePiece(Piece piece, int newX, int newY) {
    Piece targetPiece = chessBoard.getPiece(newX, newY);
    Move proposedMove = new Move(piece.getPieceX(), piece.getPieceY(), newX, newY, piece, targetPiece);

    if (chessBoard.validateMove(proposedMove)) {
        chessBoard.makeMove(proposedMove);
        piece.setPosition(newX, newY);
        updateUIAfterMove(piece, newX, newY, targetPiece);
    }
}

    private void updateUIAfterMove(Piece piece, int newX, int newY, Piece targetPiece) {
        StackPane targetCell = getNodeByRowColumnIndex(newY, newX);
        if (targetPiece != null) {
            targetCell.getChildren().remove(targetPiece);
        }
        targetCell.getChildren().add(piece);
    }

    private void resetBoardColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle square = squares[row][col];
                square.setEffect(null);
                square.setFill((row + col) % 2 != 0 ? Color.WHITE : Color.GRAY);
            }
        }
    }

    private StackPane getNodeByRowColumnIndex(int row, int column) {
        for (Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null; // if not found
    }
}