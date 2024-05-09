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
import javafx.scene.input.*;
import java.util.List;

public class ChessBoard extends GridPane {
    private static final int SIZE = 8;
    private static final int SQUARE_SIZE = 75;
    private final Board chessBoard;
    private final Rectangle[][] squares = new Rectangle[SIZE][SIZE];
    private final Glow glow = new Glow(0.5);

    public ChessBoard(Board chessBoard) {
        this.chessBoard = chessBoard;
        drawBoard();
        placePieces();
    }

    private void drawBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
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
                    setupDragHandlers(piece);
                }
            }
        }
    }

    private void setupDragHandlers(Piece piece) {
        piece.setOnDragDetected(event -> {
            highlightPossibleMoves(piece);
            ClipboardContent content = new ClipboardContent();
            content.putString(piece.getPieceX() + "," + piece.getPieceY());
            Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);
            db.setContent(content);
            event.consume();
        });

        piece.setOnDragOver(event -> {
            if (event.getGestureSource() != piece && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        piece.setOnDragDropped(event -> movePiece(event, piece));

        piece.setOnDragDone(event -> {
            resetBoardColors();
            event.consume();
        });
    }

    public void highlightPossibleMoves(Piece piece) {
        List<Move> moves = piece.getPossibleMoves(chessBoard, piece.getPieceX(), piece.getPieceY());
        for (Move move : moves) {
            Rectangle targetSquare = squares[move.getEndY()][move.getEndX()];
            targetSquare.setEffect(glow);
            targetSquare.setFill(chessBoard.isSquareOccupied(move.getEndX(), move.getEndY()) ? Color.rgb(255, 0, 0, 0.15) : Color.rgb(0, 255, 0, 0.15));
        }
    }

    private void movePiece(DragEvent event, Piece piece) {
        Node source = (Node) event.getSource();
        System.out.println("Source: " + source);
        System.out.println("Piece X:  " + piece.getPieceX() + " Piece Y: " + piece.getPieceY());
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        int newX = colIndex != null ? colIndex : 0;  // Set 0 as default value if colIndex is null
        int newY = rowIndex != null ? rowIndex : 0;  // Set 0 as default value if rowIndex is null

        // Reset original square color
        resetSquareColor(piece.getPieceX(), piece.getPieceY());

        Piece targetPiece = chessBoard.getPiece(newX, newY);
        Move proposedMove = new Move(piece.getPieceX(), piece.getPieceY(), newX, newY, piece, targetPiece);

        if (chessBoard.validateMove(proposedMove) && chessBoard.makeMove(proposedMove)) {
            piece.setPosition(newX, newY);
            updateUIAfterMove(piece, newX, newY, targetPiece);
        } else {
            // Reset to original position if move was not valid
            rollbackPiecePosition(piece);
        }
        event.setDropCompleted(true);
        event.consume();
    }


    private void resetSquareColor(int x, int y) {
        Rectangle square = squares[y][x];
        square.setEffect(null);
        square.setFill((y + x) % 2 == 0 ? Color.WHITE : Color.GRAY);
    }

    private void updateUIAfterMove(Piece piece, int newX, int newY, Piece targetPiece) {
        StackPane targetCell = getNodeByRowColumnIndex(newY, newX);
        if (targetPiece != null) {
            targetCell.getChildren().remove(targetPiece);
        }
        targetCell.getChildren().add(piece);
    }

    private void rollbackPiecePosition(Piece piece) {
        piece.setTranslateX(0);
        piece.setTranslateY(0);
    }

    private void resetBoardColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle square = squares[row][col];
                square.setEffect(null);
                square.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
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
