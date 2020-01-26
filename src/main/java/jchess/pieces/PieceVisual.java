package jchess.pieces;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;

import jchess.helper.ResourceLoader;
import jchess.helper.Log;
import jchess.move.buff.Buff;
import jchess.move.buff.BuffType;
import jchess.view.RoundChessboardView;

/**
 * Class to represent the visual of a Piece.
 */
public class PieceVisual {

    private final Image image;

    private List<BuffType> buffList;

    /**
     * Creates a new PieceVisual instance.
     *
     * @param piece The String ID of the image to use when drawing this PieceVisual. Must be non-null and containted in GUI.loadImage().
     */
    public PieceVisual(Piece piece) {
        this.image = ResourceLoader.loadPieceImage(piece);
        this.buffList = piece.getActiveBuffs();
        if (this.image == null)
            throw new NullPointerException("PieceVisual image is null");
    }

    /**
     * Draws this PieceVisual on the given canvas at the given location with the given dimensions.
     *
     * @param g      The canvas. Must be non-null.
     * @param x      The top-left point's x value.
     * @param y      The top-left point's y value.
     * @param width  The width for the PieceVisual image.
     * @param height The height for the PieceVisual image.
     * @param view   The view where the image will be drawn
     */
    public final void draw(Graphics g, int x, int y, int width, int height, RoundChessboardView view) {
        try {
            if (image != null && g != null) {
                g.drawImage(image, x, y, width, height, view);
            	drawBuffs(g, x, y, width, height);
            } else {
                Log.log(Level.SEVERE, "image is null!");
            }

        } catch (java.lang.NullPointerException exc) {
            Log.log(Level.SEVERE, "Something wrong when painting piece: " + exc.getMessage());
        }
    }

    private void drawBuffs(Graphics graphics, int x, int y, int width, int height) {
        buffList.forEach(b -> {
            if (b.equals(BuffType.Confusion)) {
                graphics.setColor(Color.green);
                graphics.fillOval(x + width / 2, y + width / 2, width / 5, height / 5);
            }
        });
    }

}
