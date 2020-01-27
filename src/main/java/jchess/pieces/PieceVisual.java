package jchess.pieces;

import jchess.io.ResourceLoader;
import jchess.logging.Log;
import jchess.game.chessboard.view.AbstractChessboardView;

import java.awt.*;
import java.util.logging.Level;

/**
 * Class to represent the visual of a Piece.
 */
public class PieceVisual {
	private final Image image;
	
	/**
	 * Creates a new PieceVisual instance.
	 * @param piece The String ID of the image to use when drawing this PieceVisual. Must be non-null and containted in GUI.loadImage().
	 */
	public PieceVisual(Piece piece) {
		this.image = ResourceLoader.loadPieceImage(piece);
		if (this.image == null)
			throw new NullPointerException("PieceVisual image is null");
	}
	
	/**
	 * Draws this PieceVisual on the given canvas at the given location with the given dimensions.
	 * @param g The canvas. Must be non-null.
	 * @param x The top-left point's x value.
	 * @param y The top-left point's y value.
	 * @param width The width for the PieceVisual image.
	 * @param height The height for the PieceVisual image.
	 * @param view The view where the image will be drawn
	 */
	public final void draw(Graphics g, int x, int y, int width, int height, AbstractChessboardView view) {
		try {
			if (image != null && g != null) {
				g.drawImage(image, x, y, width, height, view);
			} else {
				Log.log(Level.SEVERE, "image is null!");
			}

		} catch (java.lang.NullPointerException exc) {
			Log.log(Level.SEVERE, "Something wrong when painting piece: " + exc.getMessage());
		}
	}
}
