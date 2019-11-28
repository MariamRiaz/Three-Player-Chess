package jchess.pieces;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import jchess.GUI;
import jchess.Log;

public class PieceVisual {
	private final Image image;
	
	/**
	 * Creates a new PieceVisual instnace.
	 * @param imageID The String ID of the image to use when drawing this PieceVisual. Must be non-null and containted in GUI.loadImage().
	 */
	public PieceVisual(String imageID) {
		this.image = GUI.loadImage(imageID);
		if (this.image == null)
			throw new NullPointerException("PieceVisual image is null. Argument 'imageID' was " + new String(imageID));
	}
	
	/**
	 * Draws this PieceVisual on the given canvas at the given location with the given dimensions.
	 * @param g The canvas. Must be non-null.
	 * @param x The top-left point's x value.
	 * @param y The top-left point's y value.
	 * @param width The width for the PieceVisual image.
	 * @param height The height for the PieceVisual image.
	 */
	public final void draw(Graphics g, int x, int y, int width, int height) {
		try {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (image != null && g != null) {
				BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics2D imageGr = (Graphics2D) resized.createGraphics();
				imageGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				imageGr.drawImage(image, 0, 0, width, height, null);
				imageGr.dispose();
				g2d.drawImage(resized.getScaledInstance( width, height, 0), x, y, null);
			} else {
				Log.log(Level.SEVERE, "image is null!");
			}

		} catch (java.lang.NullPointerException exc) {
			Log.log(Level.SEVERE, "Something wrong when painting piece: " + exc.getMessage());
		}
	}
}
