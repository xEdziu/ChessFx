package eddy.chessfx.logic;

import eddy.chessfx.pieces.Piece;

public class Move {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;  // null if no capture
    private Piece promotionPiece;  // null if no promotion
    private boolean isCastlingMove = false;  // True if the move is castling

    public Move(int startX, int startY, int endX, int endY, Piece pieceMoved, Piece pieceCaptured, Piece promotionPiece){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
        this.promotionPiece = promotionPiece;
    }

    // Getters and setters
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public Piece getPieceMoved() { return pieceMoved; }
    public Piece getPieceCaptured() { return pieceCaptured; }
    public Piece getPromotionPiece() { return promotionPiece; }
    public boolean isCastlingMove() { return isCastlingMove; }
    public void setCastlingMove(boolean isCastlingMove) { this.isCastlingMove = isCastlingMove; }
    public void  setPromotionPiece(Piece promotionPiece) { this.promotionPiece = promotionPiece; }
}
