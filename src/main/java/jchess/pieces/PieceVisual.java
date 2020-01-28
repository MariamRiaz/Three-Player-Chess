package jchess.pieces;

import jchess.game.chessboard.view.RoundChessboardView;
import jchess.io.ResourceLoader;
import jchess.logging.Log;
import jchess.move.buff.Buff;
import jchess.move.buff.BuffType;

import java.awt.*;
import java.util.List;
import java.util.logging.Level;

/**
 * Class to represent the visual of a Piece.
 */
public class PieceVisual {
    private final Image image;

    private List<Buff> buffList;

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
            int w = width, h = height;

            if (b.getType().equals(BuffType.Confusion))
                graphics.setColor(Color.green);
            else if (b.getType().equals(BuffType.ImminentExplosion)) {
                w *= 2;
                h *= 2;
                graphics.setColor(Color.red);
            }

            drawDots(graphics, x, y, w, h, b.getRemainingTicks());
        });
    }

    private void drawOneDot(Graphics g, int x, int y, int width, int height) {
        int dotHeight = height / 6;
        int dotWidth = width / 6;
        int dotX = x + width / 2 - dotWidth / 2;
        int dotY = y + height / 2 - dotHeight / 2;
        g.fillOval(dotX, dotY, dotWidth, dotHeight);
    }

    private void drawTwoDots(Graphics g, int x, int y, int width, int height) {
        int dotHeight = height / 6;
        int dotWidth = width / 6;
        int dotX = x + width / 2 - dotWidth / 2;
        int dotY = y + height / 2;
        g.fillOval(dotX, dotY, dotWidth, dotHeight);
        int secondDotY = dotY - dotHeight;
        g.fillOval(dotX, secondDotY, dotWidth, dotHeight);
    }

    private void drawThreeDots(Graphics g, int x, int y, int width, int height) {
        int dotHeight = height / 6;
        int dotWidth = width / 6;
        int dotX = x + width / 2 - dotWidth / 2;
        int dotY = y + height / 2;
        g.fillOval(dotX, dotY, dotWidth, dotHeight);
        int otherDotsY = dotY - dotHeight;
        int secondDotX = dotX - dotWidth / 2;
        g.fillOval(secondDotX, otherDotsY, dotWidth, dotHeight);
        int thirdDotX = dotX + dotWidth / 2;
        g.fillOval(thirdDotX, otherDotsY, dotWidth, dotHeight);

    }

    private void drawDots(Graphics g, int x, int y, int width, int height, int number) {
        switch (number) {
            case 1:
                drawOneDot(g, x, y, width, height);
                break;
            case 2:
                drawTwoDots(g, x, y, width, height);
                break;
            case 3:
                drawThreeDots(g, x, y, width, height);
            default:
                break;
        }
    }
}
