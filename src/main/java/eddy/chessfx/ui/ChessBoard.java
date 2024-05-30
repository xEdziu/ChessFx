package eddy.chessfx.ui;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import eddy.chessfx.pieces.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ChessBoard extends GridPane {
    private static final int SIZE = 8;
    private static final int SQUARE_SIZE = 75;
    private final Board chessBoard;
    private final Rectangle[][] squares = new Rectangle[SIZE][SIZE];
    private final Glow glow = new Glow(0.5);
    private Piece selectedPiece = null;  // Add this line
    private boolean isPlayerWhite;
    private boolean isPlayerVsPlayer;
    private boolean isCheckmate = false;
    private boolean isPlayerMove = true;
    public String winnerString = "";

    public ChessBoard(Board chessBoard, boolean isPlayerWhite, boolean isPlayerVsPlayer) {
        this.chessBoard = chessBoard;
        drawBoard();
        placePieces();
        setupSquareClickHandlers();  // Add this line
        this.isPlayerWhite = isPlayerWhite;
        this.isPlayerVsPlayer = isPlayerVsPlayer;
        if (!isPlayerVsPlayer && !isPlayerWhite) {
            isPlayerMove = false;
        }
    }

    public boolean isPlayerMove() {
        return isPlayerMove;
    }

    private void drawBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                square.setStroke(Color.rgb(0, 0, 0, 0.5));
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
            if ((piece.isWhite() == chessBoard.isWhiteTurn() && isPlayerVsPlayer) || (piece.isWhite() == isPlayerWhite && !isPlayerVsPlayer)) {
                System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
                System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
                resetBoardColors();
                highlightPossibleMoves(piece);
                selectedPiece = piece;
                System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
                squares[piece.getPieceY()][piece.getPieceX()].setEffect(glow);
                squares[piece.getPieceY()][piece.getPieceX()].setFill(Color.rgb(128, 0, 128, 0.10));
            } else if (selectedPiece != null) {
                Move proposedMove = new Move(selectedPiece.getPieceX(), selectedPiece.getPieceY(), piece.getPieceX(), piece.getPieceY(), selectedPiece, piece, null);
                List<Move> possibleMoves = selectedPiece.getPossibleMoves(chessBoard, selectedPiece.getPieceX(), selectedPiece.getPieceY());
                if (possibleMoves.contains(proposedMove)) {
                    System.out.println("Piece captured: " + piece.getClass().getSimpleName());
                    movePiece(selectedPiece, piece.getPieceX(), piece.getPieceY());
                    selectedPiece = null;
                    if (!isPlayerVsPlayer) {
                        isPlayerMove = !isPlayerMove;
                    }
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
                            selectedPiece = null;
                            resetBoardColors();
                            if (!isPlayerVsPlayer) {
                                isPlayerMove = !isPlayerMove;
                            }
                        }
                    } else if (piece != null && ((piece.isWhite() == chessBoard.isWhiteTurn() && isPlayerVsPlayer) || (piece.isWhite() == isPlayerWhite && !isPlayerVsPlayer))) {
                        System.out.println("Piece clicked: " + piece.getClass().getSimpleName());
                        System.out.println("Piece position: " + piece.getPieceX() + ", " + piece.getPieceY());
                        resetBoardColors();
                        highlightPossibleMoves(piece);
                        selectedPiece = piece;
                        System.out.println("Selected piece: " + selectedPiece.getClass().getSimpleName());
                        squares[piece.getPieceY()][piece.getPieceX()].setEffect(glow);
                        squares[piece.getPieceY()][piece.getPieceX()].setFill(Color.rgb(128, 0, 128, 0.10));
                    }
                    event.consume();
                });
            }
        }
    }

    public void highlightPossibleMoves(Piece piece) {
        List<Move> moves = piece.getPossibleMoves(chessBoard, piece.getPieceX(), piece.getPieceY());

        moves = moves.stream()
                .filter(move -> {
                    Board tempBoard = new Board(chessBoard);  // Create a temporary copy of the board
                    if (tempBoard.validateMove(move)) {  // Check if the move is valid
                        if (tempBoard.makeMove(move)) {
                            return !tempBoard.isKingInCheck(piece.isWhite());  // Check if the king is still in check
                        }
                    }
                    return false;
                })
                .toList();

        for (Move move : moves) {
            Rectangle targetSquare = squares[move.getEndY()][move.getEndX()];
            targetSquare.setEffect(glow);
            if (piece instanceof Pawn && Math.abs(move.getStartX() - move.getEndX()) == 1) {
                targetSquare.setFill(Color.rgb(255, 0, 0, 0.10));  // Red highlight
            } else if (piece instanceof Pawn &&
                    (move.getEndY() == 0 || move.getEndY() == 7)) {
                targetSquare.setFill(Color.rgb(0, 0, 255, 0.10));  // Blue highlight
            } else if (piece instanceof King && move.isCastlingMove()) {
                targetSquare.setFill(Color.rgb(0, 255, 255, 0.10));  // Cyan highlight
            } else if (piece instanceof King && Math.abs(move.getEndX() - piece.getPieceX()) == 2) {
                targetSquare.setFill(Color.rgb(255, 255, 0, 0.10));  // Yellow highlight
            } else if (chessBoard.isSquareOccupied(move.getEndX(), move.getEndY())) {
                targetSquare.setFill(Color.rgb(255, 0, 0, 0.10));  // Red highlight
            } else {
                targetSquare.setFill(Color.rgb(0, 255, 0, 0.10));  // Green highlight
            }
            if (piece instanceof Pawn && Math.abs(move.getStartX() - move.getEndX()) == 1 && Math.abs(move.getStartY() - move.getEndY()) == 1) {
                if (move.getEndY() > 0 && !chessBoard.isSquareOccupied(move.getEndX(), move.getEndY() - 1)) {
                    targetSquare.setFill(Color.rgb(255, 0, 0, 0.10));  // Red highlight
                } else if (move.getEndY() < 7 && !chessBoard.isSquareOccupied(move.getEndX(), move.getEndY() + 1)) {
                    targetSquare.setFill(Color.rgb(255, 0, 0, 0.10));  // Red highlight
                }
            }
        }

    }

    private void movePiece(Piece piece, int newX, int newY) {
        Piece targetPiece = chessBoard.getPiece(newX, newY);
        Move proposedMove = new Move(piece.getPieceX(), piece.getPieceY(), newX, newY, piece, targetPiece, null);

        System.out.println("Move: " + piece.getClass().getSimpleName() + " from " + piece.getPieceX() + ", " + piece.getPieceY() + " to " + newX + ", " + newY);
        System.out.println("Where actual X and Y are: " + piece.getPieceX() + ", " + piece.getPieceY() + " and new are " + newX + ", " + newY);

        if (chessBoard.validateMove(proposedMove) && chessBoard.makeMove(proposedMove)) {
            // Check if the move was a castling move

            if (proposedMove.isCastlingMove()) {
                int rookNewX = newX > piece.getPieceX() ? newX - 1 : newX + 1;  // Rook's new position
                Piece rook = chessBoard.getPiece(rookNewX, newY);
                System.out.println("Rook new position: " + rookNewX + ", " + newY);
                rook.setPosition(rookNewX, newY);
                updateUIAfterMove(rook, rookNewX, newY, null);
                System.out.println("Castling move!");
                System.out.println("Rook pos from object: " + rook.getPieceX() + ", " + rook.getPieceY());
            }

            if (piece instanceof Pawn && Math.abs(newX - piece.getPieceX()) == 1 ) {
                System.out.println("En passant move!");
                int capturedPawnY = !piece.isWhite() ? newY - 1 : newY + 1;  // Y position of the captured pawn
                Piece capturedPawn = chessBoard.getPiece(newX, capturedPawnY);
                System.out.println("Captured pawn position: " + newX + ", " + capturedPawnY);
                if (capturedPawn != null) {
                    // Remove the captured pawn from the UI
                    System.out.println("Removing captured pawn from the UI");
                    StackPane capturedPawnCell = getNodeByRowColumnIndex(capturedPawnY, newX);
                    capturedPawnCell.getChildren().remove(capturedPawn);
                    chessBoard.removePiece(newX, capturedPawnY);
                }
            }
        }

            piece.setPosition(newX, newY);
            updateUIAfterMove(piece, newX, newY, targetPiece);

            if (piece instanceof Pawn && (newY == 0 || newY == 7)) {
                System.out.println("Pawn promotion!");
                String pieceType = showPromotionDialog();
                chessBoard.removePiece(newX, newY);
                StackPane cell = getNodeByRowColumnIndex(newY, newX);
                cell.getChildren().remove(piece);
                Piece newPiece = createNewPiece(pieceType, piece.isWhite());
                chessBoard.placePiece(newPiece, newX, newY);
                updateUIAfterMove(newPiece, newX, newY, null);
                proposedMove.setPromotionPiece(newPiece);
            }

            resetBoardColors();
            checkForCheckmate();
    }

    private String showPromotionDialog() {
        List<String> choices = Arrays.asList("Queen", "Rook", "Knight", "Bishop");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", choices);
        dialog.setTitle("Pawn Promotion");
        dialog.setHeaderText("Choose a piece to promote your pawn to:");
        dialog.setContentText("Choose your piece:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("Queen");
    }

    private Piece createNewPiece(String pieceType, boolean isWhite) {
        return switch (pieceType) {
            case "Rook" -> new Rook(isWhite);
            case "Knight" -> new Knight(isWhite);
            case "Bishop" -> new Bishop(isWhite);
            case "Queen" -> new Queen(isWhite);
            default -> null;
        };
    }

    public void highlightKingInCheck(boolean isWhite) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = chessBoard.getPiece(x, y);
                if (piece instanceof King && piece.isWhite() == isWhite && chessBoard.isKingInCheck(isWhite)) {
                    squares[y][x].setFill(Color.rgb(255, 255, 0, 0.2)); // Yellow highlight
                    squares[y][x].setEffect(glow);
                }
            }
        }
    }

    private boolean checkForCheckmate(){
        boolean isWhiteTurn = chessBoard.isWhiteTurn();
        if (chessBoard.isCheckmate(isWhiteTurn) || chessBoard.isCheckmate(!isWhiteTurn)) {
            System.out.println("Checkmate!");
            if (!isPlayerVsPlayer) {
                winnerString = isWhiteTurn ?  "White" : "Black";
            } else {
                winnerString = isWhiteTurn ? "Black" : "White";
            }
            System.out.println(winnerString + " wins!");
            this.isCheckmate = true;
            if (isPlayerVsPlayer) {
                showEndGamePopup(winnerString);
            }
        }
        return this.isCheckmate;
    }

    public boolean getIsCheckmate(){
        return checkForCheckmate();
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
                square.setFill((row + col) % 2 != 0 ? Color.GRAY : Color.WHITE);
            }
        }
        highlightKingInCheck(true);
        highlightKingInCheck(false);
    }

    private StackPane getNodeByRowColumnIndex(int row, int column) {
        for (Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null; // if not found
    }

    private void downloadGame(String winner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        String userDirectoryString = System.getProperty("user.home") + "/Downloads";
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        fileChooser.setInitialDirectory(userDirectory);
        String defaultFileName = "ChessGame_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm")) + ".txt";
        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm")) + " | Winner: " + winner + "\n");
                writer.write(chessBoard.getGameInChessNotation());
            } catch (IOException e) {
                System.out.println("Error saving game: " + e.getMessage());
            }
        }
    }

    public void showEndGamePopup(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Checkmate!");
        alert.setContentText(winner + " wins!");

        ButtonType newGameButton = new ButtonType("New Game");
        ButtonType exitButton = new ButtonType("Exit");
        ButtonType downloadGameButton = new ButtonType("Download Game");

        if (isPlayerVsPlayer) {
            alert.getButtonTypes().setAll(newGameButton, exitButton, downloadGameButton );
        } else {
            alert.getButtonTypes().setAll(exitButton, downloadGameButton);
        }

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == newGameButton) {
                chessBoard.restartGame();
                this.getChildren().clear();
                drawBoard();
                placePieces();
                setupSquareClickHandlers();
            } else if (buttonType == exitButton) {
                System.exit(0);
            } else if (buttonType == downloadGameButton) {
                downloadGame(winner);
            }
        });
    }

    public void setPlayerMove(boolean b) {
        isPlayerMove = b;
    }
}