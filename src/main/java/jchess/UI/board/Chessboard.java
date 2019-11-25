/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.UI.board;

import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JPanel;

import jchess.GUI;
import jchess.JChessApp;
import jchess.Log;
import jchess.Player;
import jchess.Settings;
import jchess.pieces.Move;
import jchess.pieces.Moves;
import jchess.pieces.Piece;
import jchess.pieces.Piece.Move.MoveType;
import jchess.pieces.PieceFactory;
import jchess.pieces.PieceVisual;
import jchess.pieces.Moves.castling;

/**
 * Class to represent chessboard. Chessboard is made from squares. It is setting
 * the squers of chessboard and sets the pieces(pawns) witch the owner is
 * current player on it.
 */
public class Chessboard extends JPanel {
	public static final int top = 0;
	public static final int bottom = 7;
	public Square squares[][];// squares of chessboard
	public HashMap<Piece, PieceVisual> pieceVisuals = new HashMap<Piece, PieceVisual>();// squares of chessboard
	private static final Image orgImage = GUI.loadImage("chessboard.png");// image of chessboard
	private static Image image = Chessboard.orgImage;// image of chessboard
	private static final Image org_sel_square = GUI.loadImage("sel_square.png");// image of highlited square
	private static Image sel_square = org_sel_square;// image of highlited square
	private static final Image org_able_square = GUI.loadImage("able_square.png");// image of square where piece can go
	private static Image able_square = org_able_square;// image of square where piece can go
	public Square activeSquare;
	private Image upDownLabel = null;
	private Image LeftRightLabel = null;
	private Point topLeft = new Point(0, 0);
	private int active_x_square;
	private int active_y_square;
	private float square_height;// height of square
	// public Graphics graph;
	public static final int img_x = 5;// image x position (used in JChessView class!)
	public static final int img_y = img_x;// image y position (used in JChessView class!)
	public static final int img_widht = 480;// image width
	public static final int img_height = img_widht;// image height
	private HashSet<Square> moves;
	private Settings settings;
	public Piece kingWhite;
	public Piece kingBlack;
	// -------- for undo ----------
	private Square undo1_sq_begin = null;
	private Square undo1_sq_end = null;
	private Piece undo1_piece_begin = null;
	private Piece undo1_piece_end = null;
	private Piece ifWasEnPassant = null;
	private Piece ifWasCastling = null;
	private boolean breakCastling = false; // if last move break castling
	// ----------------------------
	// For En passant:
	// |-> Pawn whose in last turn moved two square
	public Piece twoSquareMovedPawn = null;
	public Piece twoSquareMovedPawn2 = null;
	private Moves moves_history;
	
	private HashMap<Piece, Square> pieceToSquare = new HashMap<Piece, Square>();

	/**
	 * Chessboard class constructor
	 * 
	 * @param settings      reference to Settings class object for this chessboard
	 * @param moves_history reference to Moves class object for this chessboard
	 */
	public Chessboard(Settings settings, Moves moves_history) {
		this.settings = settings;
		this.activeSquare = null;
		this.square_height = img_height / 8;// we need to devide to know height of field
		this.squares = new Square[8][8];// initalization of 8x8 chessboard
		this.active_x_square = 0;
		this.active_y_square = 0;
		for (int i = 0; i < 8; i++) {// create object for each square
			for (int y = 0; y < 8; y++) {
				this.squares[i][y] = new Square(i, y, null);
			}
		} // --endOf--create object for each square
		this.moves_history = moves_history;
		this.setDoubleBuffered(true);
		this.drawLabels((int) this.square_height);
	}/*--endOf-Chessboard--*/
	
	public Square getSquare(Piece piece) {
		return piece != null && pieceToSquare.containsKey(piece) ? pieceToSquare.get(piece) : null;
	}
	
	public Square getSquare(int x, int y) { //duplicate method with GUI-related getSquare
		return x < 0 || y < 0 || x >= this.squares.length || y >= this.squares[x].length ? null : this.squares[x][y];
	}
	
	public Piece getPiece(int x, int y) {
		return getPiece(getSquare(x, y));
	}
	
	public Piece getPiece(Square square) {
		return square.piece;
	}
	
	public Piece setPieceOnSquare(Piece piece, Square square) {
		if (piece == null)
			return null;
		
		if (pieceToSquare.containsKey(piece)) {
			pieceToSquare.get(piece).piece = null;
			pieceToSquare.remove(piece);
		}
		
		if (square != null) {
			setPieceOnSquare(square.piece, null);
			square.piece = piece;
			pieceToSquare.put(piece, square);
		}
		
		return piece;
	}
	
	/**
	 * Method setPieces on begin of new game or loaded game
	 * 
	 * @param places  string with pieces to set on chessboard
	 * @param plWhite reference to white player
	 * @param plBlack reference to black player
	 */
	public void setPieces(String places, Player plWhite, Player plBlack) {

		if (places.equals("")) // if newGame
		{
			if (this.settings.upsideDown) {
				this.setPieces4NewGame(true, plWhite, plBlack);
			} else {
				this.setPieces4NewGame(false, plWhite, plBlack);
			}

		} else // if loadedGame
		{
			return;
		}
	}/*--endOf-setPieces--*/

	/**
	 *
	 */
	private void setPieces4NewGame(boolean upsideDown, Player plWhite, Player plBlack) {

		/* WHITE PIECES */
		Player player = plBlack;
		Player player1 = plWhite;
		if (upsideDown) // if white on Top
		{
			player = plWhite;
			player1 = plBlack;
		}
		this.setFigures4NewGame(0, player, upsideDown);
		this.setPawns4NewGame(1, player);
		this.setFigures4NewGame(7, player1, upsideDown);
		this.setPawns4NewGame(6, player1);
	}/*--endOf-setPieces(boolean upsideDown)--*/

	/**
	 * method set Figures in row (and set Queen and King to right position)
	 * 
	 * @param i          row where to set figures (Rook, Knight etc.)
	 * @param player     which is owner of pawns
	 * @param upsideDown if true white pieces will be on top of chessboard
	 */
	private void setFigures4NewGame(int i, Player player, boolean upsideDown) {

		if (i != 0 && i != 7) {
			Log.log(Level.SEVERE, "error setting figures like rook etc.");
			return;
		} else if (i == 0) {
			player.goDown = true;
		}

		setPieceOnSquare(PieceFactory.createRook(player), getSquare(0, i));
		setPieceOnSquare(PieceFactory.createRook(player), getSquare(7, i));
		setPieceOnSquare(PieceFactory.createKnight(player), getSquare(1, i));
		setPieceOnSquare(PieceFactory.createKnight(player), getSquare(6, i));
		setPieceOnSquare(PieceFactory.createBishop(player), getSquare(2, i));
		setPieceOnSquare(PieceFactory.createBishop(player), getSquare(5, i));
		
		if (upsideDown) {
			setPieceOnSquare(PieceFactory.createQueen(player), getSquare(4, i));
			
			if (player.color == Player.colors.white)
				kingWhite = setPieceOnSquare(PieceFactory.createKing(player), getSquare(3, i));
			else
				kingBlack = setPieceOnSquare(PieceFactory.createKing(player), getSquare(3, i));
		} else {
			setPieceOnSquare(PieceFactory.createQueen(player), getSquare(3, i));

			if (player.color == Player.colors.white)
				kingWhite = setPieceOnSquare(PieceFactory.createKing(player), getSquare(4, i));
			else
				kingBlack = setPieceOnSquare(PieceFactory.createKing(player), getSquare(4, i));
		}

		setVisual(getSquare(0, i).piece);
		setVisual(getSquare(1, i).piece);
		setVisual(getSquare(2, i).piece);
		setVisual(getSquare(3, i).piece);
		setVisual(getSquare(4, i).piece);
		setVisual(getSquare(5, i).piece);
		setVisual(getSquare(6, i).piece);
		setVisual(getSquare(7, i).piece);
	}

	/**
	 * method set Pawns in row
	 * 
	 * @param i      row where to set pawns
	 * @param player player which is owner of pawns
	 */
	private void setPawns4NewGame(int i, Player player) {
		if (i != 1 && i != 6) {
			Log.log(Level.SEVERE, "error setting pawns etc.");
			return;
		}
		for (int x = 0; x < 8; x++) {
			setPieceOnSquare(PieceFactory.createPawn(player, !player.goDown), getSquare(x, i));
			setVisual(getSquare(x, i).piece);
		}
	}

	/**
	 * method to get reference to square from given x and y integeres
	 * 
	 * @param x x position on chessboard
	 * @param y y position on chessboard
	 * @return reference to searched square
	 */
	public Square getSquareFromClick(int x, int y) {
		if ((x > this.get_height()) || (y > this.get_widht())) // test if click is out of chessboard
		{
			Log.log("click out of chessboard.");
			return null;
		}
		if (this.settings.renderLabels) {
			x -= this.upDownLabel.getHeight(null);
			y -= this.upDownLabel.getHeight(null);
		}
		double square_x = x / square_height;// count which field in X was clicked
		double square_y = y / square_height;// count which field in Y was clicked

		if (square_x > (int) square_x) // if X is more than X parsed to Integer
		{
			square_x = (int) square_x + 1;// parse to integer and increment
		}
		if (square_y > (int) square_y) // if X is more than X parsed to Integer
		{
			square_y = (int) square_y + 1;// parse to integer and increment
		}
		// Square newActiveSquare =
		// getSquare((int)square_x-1][(int)square_y-1];//4test
		Log.log("square_x: " + square_x + " square_y: " + square_y + " \n"); // 4tests
		Square result;
		try {
			result = getSquare((int) square_x - 1, (int) square_y - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
			Log.log(Level.SEVERE,
					"!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc);
			return null;
		}
		return getSquare((int) square_x - 1, (int) square_y - 1);
	}

	/**
	 * Method selecting piece in chessboard
	 * 
	 * @param sq square to select (when clicked))
	 */
	public void select(Square sq) {
		this.activeSquare = sq;
		this.active_x_square = sq.pozX + 1;
		this.active_y_square = sq.pozY + 1;

		// this.draw();//redraw
		Log.log("active_x: " + this.active_x_square + " active_y: " + this.active_y_square);// 4tests
		repaint();

	}/*--endOf-select--*/

	/**
	 * Method set variables active_x_square & active_y_square to 0 values.
	 */
	public void unselect() {
		this.active_x_square = 0;
		this.active_y_square = 0;
		this.activeSquare = null;
		// this.draw();//redraw
		repaint();
	}/*--endOf-unselect--*/

	public int get_widht() {
		return this.get_widht(false);
	}

	public int get_height() {
		return this.get_height(false);
	}

	public int get_widht(boolean includeLables) {
		return this.getHeight();
	}/*--endOf-get_widht--*/

	public int get_height(boolean includeLabels) {
		if (this.settings.renderLabels) {
			return Chessboard.image.getHeight(null) + upDownLabel.getHeight(null);
		}
		return Chessboard.image.getHeight(null);
	}/*--endOf-get_height--*/

	public int get_square_height() {
		int result = (int) this.square_height;
		return result;
	}

	public void move(Square begin, Square end) {
		move(begin, end, true);
	}

	/**
	 * Method to move piece over chessboard
	 * 
	 * @param xFrom from which x move piece
	 * @param yFrom from which y move piece
	 * @param xTo   to which x move piece
	 * @param yTo   to which y move piece
	 */
	public void move(int xFrom, int yFrom, int xTo, int yTo) {
		Square fromSQ = null;
		Square toSQ = null;
		try {
			fromSQ = getSquare(xFrom, yFrom);
			toSQ = getSquare(xTo, yTo);
		} catch (java.lang.IndexOutOfBoundsException exc) {
			Log.log(Level.SEVERE, "error moving piece: " + exc);
			return;
		}
		this.move(getSquare(xFrom, yFrom), getSquare(xTo, yTo), true);
	}

	public void move(Square begin, Square end, boolean refresh) {
		this.move(begin, end, refresh, true);
	}

	/**
	 * Method move piece from square to square
	 * 
	 * @param begin   square from which move piece
	 * @param end     square where we want to move piece *
	 * @param refresh chessboard, default: true
	 */
	public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {

		castling wasCastling = Moves.castling.none;
		Piece promotedPiece = null;
		boolean wasEnPassant = false;
		/*if (end.piece != null) {
			end.piece.setSquare(null);
		}*/

		Square tempBegin = new Square(begin);// 4 moves history
		Square tempEnd = new Square(end); // 4 moves history
		// for undo
		undo1_piece_begin = begin.piece;
		undo1_sq_begin = begin;
		undo1_piece_end = end.piece;
		undo1_sq_end = end;
		ifWasEnPassant = null;
		ifWasCastling = null;
		breakCastling = false;
		// ---

		twoSquareMovedPawn2 = twoSquareMovedPawn;
		
		setPieceOnSquare(begin.piece, end);

		if (end.piece.type.equals("King")) {
			
			if (!end.piece.hasMoved())
				breakCastling = true;
			
			end.piece.setHasMoved(true);// set square of piece to ending

			// Castling
			if (begin.pozX + 2 == end.pozX) {
				move(squares[7][begin.pozY], squares[end.pozX - 1][begin.pozY], false, false);
				ifWasCastling = end.piece; // for undo
				wasCastling = Moves.castling.shortCastling;
				// this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
				// wasCastling, wasEnPassant);
				// return;
			} else if (begin.pozX - 2 == end.pozX) {
				move(squares[0][begin.pozY], squares[end.pozX + 1][begin.pozY], false, false);
				ifWasCastling = end.piece; // for undo
				wasCastling = Moves.castling.longCastling;
				// this.moves_history.addMove(tempBegin, tempEnd, clearForwardHistory,
				// wasCastling, wasEnPassant);
				// return;
			}
			// endOf Castling
		} else if (end.piece.type.equals("Rook")) {
			if (!end.piece.hasMoved())
				breakCastling = true;
			end.piece.setHasMoved(true);// set square of piece to ending
		} else if (end.piece.type.equals("Pawn")) {
			if (twoSquareMovedPawn != null && squares[end.pozX][begin.pozY] == getSquare(twoSquareMovedPawn)) // en passant
			{
				ifWasEnPassant = squares[end.pozX][begin.pozY].piece; // for undo

				tempEnd.piece = squares[end.pozX][begin.pozY].piece; // ugly hack - put taken pawn in en passant plasty
																		// do end square

				squares[end.pozX][begin.pozY].piece = null;
				wasEnPassant = true;
			}

			if (begin.pozY - end.pozY == 2 || end.pozY - begin.pozY == 2) // moved two square
			{
				breakCastling = true;
				twoSquareMovedPawn = end.piece;
			} else {
				twoSquareMovedPawn = null; // erase last saved move (for En passant)
			}

			end.piece.setHasMoved(true);// set square of piece to ending
			
			if (end.pozY == 0 || end.pozY == 7) // promote Pawn
			{
				if (clearForwardHistory) {
					String color;
					if (end.piece.player.color == Player.colors.white) {
						color = "W"; // promotionWindow was show with pieces in this color
					} else {
						color = "B";
					}

					String newPiece = JChessApp.jcv.showPawnPromotionBox(color); // return name of new piece

					if (newPiece.equals("Queen")) // transform pawn to queen
					{
						Piece queen = PieceFactory.createQueen(end.piece.player);
						setPieceOnSquare(queen, end);
						setVisual(queen);
					} else if (newPiece.equals("Rook")) // transform pawn to rook
					{
						Piece rook = PieceFactory.createRook(end.piece.player);
						setPieceOnSquare(rook, end);
						setVisual(rook);
					} else if (newPiece.equals("Bishop")) // transform pawn to bishop
					{
						Piece bishop = PieceFactory.createBishop(end.piece.player);
						setPieceOnSquare(bishop, end);
						setVisual(bishop);
					} else // transform pawn to knight
					{
						Piece knight = PieceFactory.createKing(end.piece.player);
						setPieceOnSquare(knight, end);
						setVisual(knight);
					}
					promotedPiece = end.piece;
				}
			}
		} else if (!end.piece.type.equals("Pawn")) {
			end.piece.setHasMoved(true);
			twoSquareMovedPawn = null; // erase last saved move (for En passant)
		}
		// }

		if (refresh) {
			this.unselect();// unselect square
			repaint();
		}

		if (clearForwardHistory) {
			this.moves_history.clearMoveForwardStack();
			this.moves_history.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
		} else {
			this.moves_history.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
		}
	}/* endOf-move()- */

	public boolean redo() {
		return redo(true);
	}

	public boolean redo(boolean refresh) {
		if (this.settings.gameType == Settings.gameTypes.local) // redo only for local game
		{
			Move first = this.moves_history.redo();

			Square from = null;
			Square to = null;

			if (first != null) {
				from = first.getFrom();
				to = first.getTo();

				this.move(from, to, true, false);
				if (first.getPromotedPiece() != null) {
					Piece promoted = setPieceOnSquare(first.getPromotedPiece(), to);
					setVisual(promoted);
				}
				return true;
			}

		}
		return false;
	}

	public boolean undo() {
		return undo(true);
	}
	
	public void setVisual(Piece piece) {
		if (piece == null)
			return;
		this.pieceVisuals.put(piece, new PieceVisual(piece, piece.player.color == piece.player.color.black ? piece.type + "-B.png" : piece.type + "-W.png"));
	}
	
	public void removeVisual(Piece piece) {
		if (piece != null && this.pieceVisuals.containsKey(piece))
			this.pieceVisuals.remove(piece);
	}
	
	public synchronized boolean undo(boolean refresh) // undo last move
	{
		Move last = this.moves_history.undo();

		if (last != null && last.getFrom() != null) {
			Square begin = last.getFrom();
			Square end = last.getTo();
			try {
				Piece moved = last.getMovedPiece();
				setPieceOnSquare(moved, begin);
				setVisual(moved);

				Piece taken = last.getTakenPiece();
				if (last.getCastlingMove() != castling.none) {
					Piece rook = null;
					if (last.getCastlingMove() == castling.shortCastling) {
						rook = getSquare(end.pozX - 1, end.pozY).piece;
						setPieceOnSquare(rook, getSquare(7, begin.pozY));
						
						setVisual(rook);
					} else {
						rook = getSquare(end.pozX + 1, end.pozY).piece;
						setPieceOnSquare(rook, getSquare(0, begin.pozY));
						setVisual(rook);
					}
					moved.setHasMoved(false);
					rook.setHasMoved(false);
					this.breakCastling = false;
				} else if (moved.type.equals("Rook")) {
					moved.setHasMoved(false);
				} else if (moved.type.equals("Pawn") && last.wasEnPassant()) {
					Piece pawn = last.getTakenPiece();
					setPieceOnSquare(pawn, getSquare(end.pozX, begin.pozY));
					setVisual(pawn);

				} else if (moved.type.equals("Pawn") && last.getPromotedPiece() != null) {
					Piece promoted = getSquare(end.pozX, end.pozY).piece;
					setPieceOnSquare(promoted, null);
					removeVisual(promoted);
				}

				// check one more move back for en passant
				Move oneMoveEarlier = this.moves_history.getLastMoveFromHistory();
				if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
					Piece canBeTakenEnPassant = getSquare(oneMoveEarlier.getTo().pozX, oneMoveEarlier
							.getTo().pozY).piece;
					if (canBeTakenEnPassant.type.equals("Pawn")) {
						this.twoSquareMovedPawn = canBeTakenEnPassant;
					}
				}

				if (taken != null && !last.wasEnPassant()) {
					setPieceOnSquare(taken, end);
					setVisual(taken);
				} else {
					removeVisual(end.piece);
					setPieceOnSquare(end.piece, null);
				}

				if (refresh) {
					this.unselect();// unselect square
					repaint();
				}

			} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
				return false;
			} catch (java.lang.NullPointerException exc) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to draw Chessboard and their elements (pieces etc.)
	 * 
	 * @deprecated
	 */
	public void draw() {
		this.getGraphics().drawImage(image, this.getTopLeftPoint().x, this.getTopLeftPoint().y, null);// draw an Image
																										// of chessboard
		this.drawLabels();
		this.repaint();
	}/*--endOf-draw--*/

	/**
	 * Annotations to superclass Game updateing and painting the crossboard
	 */
	@Override
	public void update(Graphics g) {
		repaint();
	}

	public Point getTopLeftPoint() {
		if (this.settings.renderLabels) {
			return new Point(this.topLeft.x + this.upDownLabel.getHeight(null),
					this.topLeft.y + this.upDownLabel.getHeight(null));
		}
		return this.topLeft;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Point topLeftPoint = this.getTopLeftPoint();
		if (this.settings.renderLabels) {
			if (topLeftPoint.x <= 0 && topLeftPoint.y <= 0) // if renderLabels and (0,0), than draw it! (for first run)
			{
				this.drawLabels();
			}
			g2d.drawImage(this.upDownLabel, 0, 0, null);
			g2d.drawImage(this.upDownLabel, 0, Chessboard.image.getHeight(null) + topLeftPoint.y, null);
			g2d.drawImage(this.LeftRightLabel, 0, 0, null);
			g2d.drawImage(this.LeftRightLabel, Chessboard.image.getHeight(null) + topLeftPoint.x, 0, null);
		}
		g2d.drawImage(image, topLeftPoint.x, topLeftPoint.y, null);// draw an Image of chessboard

		for (Iterator<Entry<Piece, PieceVisual>> it = this.pieceVisuals.entrySet().iterator(); it.hasNext(); ) {
			Point p = new Point();
			Entry<Piece, PieceVisual> ent = it.next();
			Square sq = getSquare(ent.getKey());
			
			if (sq != null && ent.getValue() != null) {
				p.x = (int) (getTopLeftPoint().x + sq.pozX * square_height);
				p.y = (int) (getTopLeftPoint().y + sq.pozY * square_height);
				ent.getValue().draw(g, p.x, p.y, (int) square_height, (int) square_height);// draw image of Piece
			}
		}
		 // --endOf--drawPiecesOnSquares
		if ((this.active_x_square != 0) && (this.active_y_square != 0)) // if some square is active
		{
			g2d.drawImage(sel_square, ((this.active_x_square - 1) * (int) square_height) + topLeftPoint.x,
					((this.active_y_square - 1) * (int) square_height) + topLeftPoint.y, null);// draw image of selected
																								// square
			Square tmpSquare = getSquare((int) (this.active_x_square - 1), (int) (this.active_y_square - 1));
			if (tmpSquare.piece != null) {
				if (!kingThreatened(tmpSquare.piece.player))
					this.moves = getValidTargetSquares(tmpSquare.piece);
				else this.moves = new HashSet<Square>();
			}

			for (Iterator it = moves.iterator(); moves != null && it.hasNext();) {
				Square sq = (Square) it.next();
				g2d.drawImage(able_square, (sq.pozX * (int) square_height) + topLeftPoint.x,
						(sq.pozY * (int) square_height) + topLeftPoint.y, null);
			}
		}
	}/*--endOf-paint--*/

	public boolean kingThreatened(Player player) {
		if (player == null)
			return false;
		if (player.color == player.color.black)
			return pieceThreatened(kingBlack);
		return pieceThreatened(kingWhite);
	}
	
	public boolean pieceThreatened(Piece piece) {
		if (piece == null)
			return false;
		
		for (int i = 0; i < this.squares.length; i++)
			for (int j = 0; j < this.squares[i].length; j++) {
				if (getSquare(i, j).piece == null)
					continue;
				else if (getSquare(i, j).piece.player == piece.player)
					continue;
				
				HashSet<Square> validMoveSquares = getValidTargetSquares(getSquare(i, j).piece);
				for (Iterator<Square> it = validMoveSquares.iterator(); it.hasNext();)
					if (it.next() == getSquare(piece))
						return true;
			}
		
		return false;
	}
	
	/*public boolean pieceUnsavable(Piece piece) { //TODO
		if (piece == null)
			return false;
		
		HashSet<Square> potential = getValidTargetSquares(piece);
		for (Iterator<Square> it = potential.iterator(); it.hasNext(); ) {
			this.move(piece.getSquare(), it.next());
			if (!pieceThreatened(piece)) {
				this.undo();
				return false;
			}
			this.undo();
		}
		
		return true;
	}*/
	
	public HashSet<Square> getValidTargetSquares(Piece piece) {
		HashSet<Square> ret = new HashSet<Square>();
		
		if (piece == null)
			return ret;
		
		HashSet<Piece.Move> moves = piece.getMoves();
		for (Iterator<Piece.Move> it = moves.iterator(); it.hasNext();)
			ret.addAll(recurseMove(it.next(), piece));
		
		return ret;
	}
	
	private Square nextSquare(Square current, int x, int y) {
		return getSquare(current.pozX + x, current.pozY + y);
	}
	
	private HashSet<Square> recurseMove(Piece.Move move, Piece piece) {
		HashSet<Square> ret = new HashSet<Square>();
		
		if (move == null || piece == null)
			return ret;

		int count = 0;
		for (Square next = nextSquare(getSquare(piece), move.x, move.y); next != null && (move.limit == null || count < move.limit);
				next = nextSquare(next, move.x, move.y)) {
			boolean add = true;
			
			if (move.conditions.contains(MoveType.OnlyAttack)) {
				if (next.piece == null || next.piece.player == piece.player)
					add = false;
			}
			else if (move.conditions.contains(MoveType.OnlyMove)) {
				if (next.piece != null)
					add = false;
			}
			else if (next.piece != null && next.piece.player == piece.player)
				add = false;
			
			if (move.conditions.contains(MoveType.OnlyWhenFresh) && piece.hasMoved())
				add = false;
			
			if (add)
				ret.add(next);
			
			if (!move.conditions.contains(MoveType.Unblockable) && next.piece != null)
				break;

			count++;
		}
		
		return ret;
	}
	
	public void resizeChessboard(int height) {
		BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = resized.createGraphics();
		g.drawImage(Chessboard.orgImage, 0, 0, height, height, null);
		g.dispose();
		Chessboard.image = resized.getScaledInstance(height, height, 0);
		this.square_height = (float) (height / 8);
		if (this.settings.renderLabels) {
			height += 2 * (this.upDownLabel.getHeight(null));
		}
		this.setSize(height, height);

		resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
		g = resized.createGraphics();
		g.drawImage(Chessboard.org_able_square, 0, 0, (int) square_height, (int) square_height, null);
		g.dispose();
		Chessboard.able_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);

		resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
		g = resized.createGraphics();
		g.drawImage(Chessboard.org_sel_square, 0, 0, (int) square_height, (int) square_height, null);
		g.dispose();
		Chessboard.sel_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);
		this.drawLabels();
	}

	protected void drawLabels() {
		this.drawLabels((int) this.square_height);
	}

	protected final void drawLabels(int square_height) {
		// BufferedImage uDL = new BufferedImage(800, 800,
		// BufferedImage.TYPE_3BYTE_BGR);
		int min_label_height = 20;
		int labelHeight = (int) Math.ceil(square_height / 4);
		labelHeight = (labelHeight < min_label_height) ? min_label_height : labelHeight;
		int labelWidth = (int) Math.ceil(square_height * 8 + (2 * labelHeight));
		BufferedImage uDL = new BufferedImage(labelWidth + min_label_height, labelHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D uDL2D = (Graphics2D) uDL.createGraphics();
		uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		uDL2D.setColor(Color.white);

		uDL2D.fillRect(0, 0, labelWidth + min_label_height, labelHeight);
		uDL2D.setColor(Color.black);
		uDL2D.setFont(new Font("Arial", Font.BOLD, 12));
		int addX = (square_height / 2);
		if (this.settings.renderLabels) {
			addX += labelHeight;
		}

		String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h" };
		if (!this.settings.upsideDown) {
			for (int i = 1; i <= letters.length; i++) {
				uDL2D.drawString(letters[i - 1], (square_height * (i - 1)) + addX, 10 + (labelHeight / 3));
			}
		} else {
			int j = 1;
			for (int i = letters.length; i > 0; i--, j++) {
				uDL2D.drawString(letters[i - 1], (square_height * (j - 1)) + addX, 10 + (labelHeight / 3));
			}
		}
		uDL2D.dispose();
		this.upDownLabel = uDL;

		uDL = new BufferedImage(labelHeight, labelWidth + min_label_height, BufferedImage.TYPE_3BYTE_BGR);
		uDL2D = (Graphics2D) uDL.createGraphics();
		uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		uDL2D.setColor(Color.white);
		// uDL2D.fillRect(0, 0, 800, 800);
		uDL2D.fillRect(0, 0, labelHeight, labelWidth + min_label_height);
		uDL2D.setColor(Color.black);
		uDL2D.setFont(new Font("Arial", Font.BOLD, 12));

		if (this.settings.upsideDown) {
			for (int i = 1; i <= 8; i++) {
				uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (i - 1)) + addX);
			}
		} else {
			int j = 1;
			for (int i = 8; i > 0; i--, j++) {
				uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (j - 1)) + addX);
			}
		}
		uDL2D.dispose();
		this.LeftRightLabel = uDL;
	}

	public void componentMoved(ComponentEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void componentShown(ComponentEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void componentHidden(ComponentEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}
}
