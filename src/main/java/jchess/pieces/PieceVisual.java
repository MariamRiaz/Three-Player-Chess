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
	
	public final int pieceID;
	
	/**
	 * @param pieceID The ID of the drawn Piece.
	 * @param imageID Must be non-null.
	 */
	public PieceVisual(int pieceID, String imageID) {
		this.pieceID = pieceID;
		this.image = GUI.loadImage(imageID);
		if (this.image == null)
			throw new NullPointerException("PieceVisual image is null. Argument 'imageID' was " + new String(imageID));
	}
	
	/*
	 * Method to draw PieceVisual.
	 * @graph Canvas to draw.
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
