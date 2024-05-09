package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import eddy.chessfx.utils.BufferedImageTranscoder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import javafx.embed.swing.SwingFXUtils;

import java.io.InputStream;
import java.util.List;

public abstract class Piece extends ImageView {
    protected boolean isWhite;  // true for white piece, false for black piece
    protected boolean hasMoved;  // true if the piece has moved
    protected int x; // Current x position
    protected int y; // Current y position

    public Piece(String imagePath, boolean isWhite) {
        super();  // Load the image of the piece
        try {
            this.setImage(getConstructorImage(imagePath));
        }
        catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        this.isWhite = isWhite;
        this.hasMoved = false;
        this.setFitWidth(64);
        this.setFitHeight(64);
        this.setPreserveRatio(true);
        initDragAndDrop();
    }

    private Image getConstructorImage(String imagePath) throws TranscoderException {
        InputStream resource = getClass().getResourceAsStream(imagePath);
        BufferedImageTranscoder trans = new BufferedImageTranscoder();
        TranscoderInput transIn = new TranscoderInput(resource);
        trans.transcode(transIn, null);
        return SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
    }

    private void initDragAndDrop() {
        this.setPickOnBounds(true);
        this.setOnDragDetected(event -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(this.x + "," + this.y);  // Store the current position
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            db.setContent(content);
            event.consume();
        });

        this.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                this.setTranslateX(0);
                this.setTranslateY(0);
            }
            event.consume();
        });
    }


    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getPieceX() {
        return x;
    }

    public int getPieceY() {
        return y;
    }

    // Abstract method to get all possible moves for this piece
    public abstract List<Move> getPossibleMoves(Board board, int x, int y);
}