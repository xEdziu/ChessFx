package eddy.chessfx.pieces;

import eddy.chessfx.logic.Board;
import eddy.chessfx.logic.Move;
import eddy.chessfx.utils.BufferedImageTranscoder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import javafx.embed.swing.SwingFXUtils;

import java.io.InputStream;
import java.util.List;

public abstract class Piece extends ImageView {
    protected boolean isWhite;  // true for white piece, false for black piece
    protected boolean hasMoved;  // true if the piece has moved

    public Piece(String imagePath, boolean isWhite) {
        super();  // Load the image of the piece
        try {
            this.setImage(getConstructorImage(imagePath));
        }
        catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        this.setFitWidth(64);
        this.setFitHeight(64);
        this.setPreserveRatio(true);
        this.isWhite = isWhite;
        this.hasMoved = false;
    }

    private Image getConstructorImage(String imagePath) throws TranscoderException {
            InputStream resource = getClass().getResourceAsStream(imagePath);
            BufferedImageTranscoder trans = new BufferedImageTranscoder();
            TranscoderInput transIn = new TranscoderInput(resource);
            trans.transcode(transIn, null);
            return SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
//        return new Image(img, 64, 64, false, false);
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

    // Abstract method to get all possible moves for this piece
    public abstract List<Move> getPossibleMoves(Board board, int x, int y);
}
