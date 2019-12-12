package jchess.controller;

import jchess.JChessApp;
import jchess.Log;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.helper.CartesianPolarConverter;
import jchess.helper.MoveEvaluator;
import jchess.helper.PolarPoint;
import jchess.model.RoundChessboardModel;
import jchess.pieces.MoveHistory;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;
import jchess.pieces.PlayedMove;
import jchess.pieces.MoveHistory.castling;
import jchess.pieces.Piece.Move.MoveType;
import jchess.view.RoundChessboardView;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import jchess.view.PolarCell;
import jchess.view.RoundChessboardView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observer;

public class RoundChessboardController extends MouseAdapter {

    private RoundChessboardModel model;
    private RoundChessboardView view;
    private Square activeSquare;
    private Settings settings;
    private SquareObservable squareObservable;

    // For undo:
    private Square undo1_sq_begin = null;
    private Square undo1_sq_end = null;
    private Piece undo1_piece_begin = null;
    private Piece undo1_piece_end = null;
    private Piece ifWasEnPassant = null;
    private Piece ifWasCastling = null;
    private boolean breakCastling = false;
    private int rows = 24;
    private int squaresPerRow = 6;
    // ----------------------------
    // For En passant:
    // |-> Pawn whose in last turn moved two square
    public Piece twoSquareMovedPawn = null;
    public Piece twoSquareMovedPawn2 = null;
    private MoveHistory movesHistory;

    public static int bottom = 7;
    public static int top = 0;

    public RoundChessboardController(RoundChessboardModel model, RoundChessboardView view, Settings settings, MoveHistory movesHistory) {
        this.model = model;
        this.view = view;
        view.addMouseListener(this);
        this.movesHistory = movesHistory;
        this.settings = settings;
        this.squareObservable = new SquareObservable();
    }

    public RoundChessboardModel getModel() {
        return model;
    }

    public RoundChessboardView getView() {
        return view;
    }
//    public void resizeChessboard(int height) {
//        this.view.resizeChessboard(height);
//    }

    //    public void resizeChessboard(){
//        this.view.resizeChessboard(view.get_height(settings.renderLabels));
//    }
//

    public void addSelectSquareObserver(Observer observer) {
        this.squareObservable.addObserver(observer);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Square square = getSquareFromClick(mouseEvent.getX(), mouseEvent.getY());
        if(square != null) {
            squareObservable.setSquare(square);
        }
    }

    public boolean moveIsPossible(int x, int y, int toX, int toY) {
    	Square square = model.getSquare(x, y), to = model.getSquare(toX, toY);

    	if (square == null || to == null || square.getPiece() == null)
    		return false;
    	return new MoveEvaluator(model)
    			.getValidTargetSquaresToSavePiece(square, square.getPiece().player, getKing(square.getPiece().player))
    			.contains(model.getSquare(to.getPozX(), to.getPozY()));
    }

    public boolean movePossible(Square square, Square to) {
    	if (square == null || to == null)
    		return false;

    	return moveIsPossible(square.getPozX(), square.getPozY(), to.getPozX(), to.getPozY());
    }

    public void select(Square sq) {
        setActiveSquare(sq);
        Log.log("Selected square with X: "
                + activeSquare.getPozX() + " and Y: " + activeSquare.getPozY());
        view.repaint();
    }

    public boolean pieceIsThreatened(Piece piece) {
    	return new MoveEvaluator(model).squareIsThreatened(model.getSquare(piece), piece.player);
    }

    public boolean pieceIsUnsavable(Piece piece) {
    	return new MoveEvaluator(model).squareUnsavable(model.getSquare(piece));
    }

    public Square getActiveSquare() {
        return activeSquare;
    }

    public void setActiveSquare(Square square) {
        this.activeSquare = square;
        if(square == null) {
            view.resetActiveCell();
            view.resetPossibleMoves();
        } else {
            view.setActiveCell(square.getPozX(), square.getPozY());
            view.setMoves(new MoveEvaluator(model)
                    .getValidTargetSquaresToSavePiece(square, square.getPiece().player, getKing(square.getPiece().player)));
        }
    }

    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece) : null;//TODO error handling
    }

    public List<Square> getSquares() {
        return this.model.squares;
    }

    public Piece getKing(Player player) {
        if (player == null)
            return null;
        if (player.color == player.color.black)
            return model.kingBlack;
        else if (player.color == player.color.gray)
        	return model.kingGray;
        return model.kingWhite;
    }
    
    private void unflagLastMovedPieces(Player player) {
		for (int i = 0; i < model.getColumns(); i++)
			for (int j = 0; j < model.getRows(); j++) {
				Square sq = model.getSquare(i, j);
				if (sq != null && sq.getPiece() != null && sq.getPiece().player == player)
					sq.getPiece().setWasPlayedLast(false);
			}
    }

    /**
     * Method move piece from square to square
     *
     * @param begin   square from which move piece
     * @param end     square where we want to move piece *
     * @param refresh chessboard, default: true
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {
    	castling wasCastling = MoveHistory.castling.none;
		Piece promotedPiece = null;
		boolean wasEnPassant = false;

		Piece tempBegin = begin.getPiece(), tempBeginState = tempBegin != null ? tempBegin.clone() : null;// 4 moves history
		Piece tempEnd = end.getPiece(); // 4 moves history
		
		tempBegin.setLastMove(null); // TODO change to actual move
		
		unflagLastMovedPieces(tempBegin.player);
		
		ifWasCastling = null;
		breakCastling = false;
		
		if (tempBegin.type.equals("King")) {

			if (!tempBeginState.hasMoved())
				breakCastling = true;

			// Castling
			if (begin.getPozX() + 2 == end.getPozX()) {
				move(this.getSquare(7, begin.getPozY()), this.getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
				ifWasCastling = tempEnd; // for undo
				wasCastling = MoveHistory.castling.shortCastling;
			} else if (begin.getPozX() - 2 == end.getPozX()) {
				move(getSquare(0, begin.getPozY()), this.getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
				ifWasCastling = tempBegin; // for undo
				wasCastling = MoveHistory.castling.longCastling;
			}
			// endOf Castling
		} else if (tempBegin.type.equals("Rook")) {
			if (!tempBeginState.hasMoved())
				breakCastling = true;
		} else if (tempBegin.type.equals("Pawn")) {
			Piece pawnForEnPassant = new MoveEvaluator(model).getPawnSkippedOverSquareEnPassant(end);
			if (pawnForEnPassant != null) {
				tempEnd = pawnForEnPassant;
				model.setPieceOnSquare(tempEnd, null);
				
				wasEnPassant = true;
			}

			if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) { // moved two square
				tempBegin.setLastMove(new Piece.Move(end.getPozX() - begin.getPozX(), end.getPozY() - begin.getPozY(), 1, MoveType.EnPassant)); // TODO fix hacky fix
				breakCastling = true;
			}

			if (end.getPozY() == 0 || end.getPozY() == 7) // promote Pawn TODO change
			{
				if (clearForwardHistory) {
					String color;
					if (tempBegin.player.color == Player.colors.white)
						color = "W"; // promotionWindow was show with pieces in this color
					else color = "B";
					
					String newPiece = JChessApp.jcv.showPawnPromotionBox(color); // return name of new piece
					Piece promoted;
					
					if (newPiece.equals("Queen")) // transform pawn to queen
						promoted = PieceFactory.createQueen(tempBegin.player);
					else if (newPiece.equals("Rook")) // transform pawn to rook
						promoted = PieceFactory.createRook(tempBegin.player);
					else if (newPiece.equals("Bishop")) // transform pawn to bishop
						promoted = PieceFactory.createBishop(tempBegin.player);
					else // transform pawn to knight
						promoted = PieceFactory.createKing(tempBegin.player);
					
					model.setPieceOnSquare(null, begin);
					view.removeVisual(tempBegin, begin);
					model.setPieceOnSquare(promoted, end);
					view.setVisual(promoted, end);
					
					promotedPiece = tempBegin;
				}
			}
		}
		
		if (refresh) {
			this.unselect();// unselect square
			view.repaint();
		}

		if (promotedPiece == null) {
			model.setPieceOnSquare(tempBegin, end);
			view.removeVisual(tempBegin, begin);
			view.setVisual(tempBegin, end);
		}
		tempBegin.setHasMoved(true);
		
		if (clearForwardHistory) {
			this.movesHistory.clearMoveForwardStack();
			this.movesHistory.addMove(begin, end, tempBegin, tempBeginState, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
		} else 
			this.movesHistory.addMove(begin, end, tempBegin, tempBeginState, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
    }

    public void unselect() {
        setActiveSquare(null);
    }

    public synchronized boolean undo(boolean refresh) // undo last move
    {   
        PlayedMove last = this.movesHistory.undo();
		
		if (last != null && last.getFrom() != null) {
			Square begin = last.getFrom();
			Square end = last.getTo();

			Piece moved = last.getMovedPiece(), movedState = last.getMovedPieceState();
			model.setPieceOnSquare(moved, null);
			view.removeVisual(moved, end);
			
			model.setPieceOnSquare(moved.load(movedState), begin);
			view.setVisual(moved, begin);
			
			Piece taken = last.getTakenPiece();
			if (last.getCastlingMove() != castling.none) {
				Piece rook = null;
				if (last.getCastlingMove() == castling.shortCastling) {
					rook = getSquare(end.getPozX() - 1, end.getPozY()).getPiece();
					model.setPieceOnSquare(rook, getSquare(squaresPerRow - 1, begin.getPozY()));
					view.setVisual(rook, squaresPerRow - 1, begin.getPozY());
				} else {
					rook = getSquare(end.getPozX() + 1, end.getPozY()).getPiece();
					model.setPieceOnSquare(rook, getSquare(0, begin.getPozY()));
					view.setVisual(rook, 0, begin.getPozY());
				}
				
				rook.setHasMoved(false);
				this.breakCastling = false;
			} else if (moved.type.equals("Pawn") && last.wasEnPassant()) {
				model.setPieceOnSquare(taken, null);
				view.removeVisual(taken, end);
				
				model.setPieceOnSquare(taken, getSquare(end.getPozX(), begin.getPozY()));
				view.setVisual(taken, end.getPozX(), begin.getPozY());
			} else if (moved.type.equals("Pawn") && !last.wasPawnTwoFieldsMove())
				moved.setHasMoved(true);
			else if (moved.type.equals("Pawn") && last.getPromotedPiece() != null) {
				Piece promoted = model.getSquare(end).getPiece();
				model.setPieceOnSquare(promoted, null);
				view.removeVisual(promoted, end);
			}
				// check one more move back for en passant
			/*PlayedMove oneMoveEarlier = this.moves_history.getLastMoveFromHistory();
			if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
				Piece canBeTakenEnPassant = getSquare(oneMoveEarlier.getTo().pozX,
						oneMoveEarlier.getTo().pozY).piece;
				if (canBeTakenEnPassant.type.equals("Pawn")) {
					this.twoSquareMovedPawn = canBeTakenEnPassant;
				}
			}*/
			
			if (taken != null && !last.wasEnPassant()) {
				model.setPieceOnSquare(taken, null);
				view.removeVisual(taken, last.getTo());
				
				model.setPieceOnSquare(taken, end);
				view.setVisual(taken, end);
			}
				if (refresh) {
				this.unselect();// unselect square
				view.repaint();
			}
			return true;
		} else return false;
    }

    public boolean redo(boolean refresh) {
        if (this.settings.gameType == Settings.gameTypes.local) // redo only for local game
        {
            PlayedMove first = this.movesHistory.redo();

            Square from = null;
            Square to = null;

            if (first != null) {
                from = first.getFrom();
                to = first.getTo();

                this.move(from, to, true, false);
                if (first.getPromotedPiece() != null) {
                    Piece promoted = model.setPieceOnSquare(first.getPromotedPiece(), to);
                    view.setVisual(promoted, to.getPozX(), to.getPozY());
                }
                return true;
            }

        }
        return false;
    }

    /**
     * method to get reference to square from given x and y integeres
     *
     * @param x x position on chessboard
     * @param y y position on chessboard
     * @return reference to searched square
     */
    public Square getSquareFromClick(int x, int y) {
        Point clickedPoint = new Point(x, y);
        CartesianPolarConverter converter = new CartesianPolarConverter();
        PolarPoint polarPoint = converter.getPolarFromCartesian(clickedPoint, view.getCircleCenter());
        for (PolarCell cell : view.getCells()) {
            double top = cell.getTopBound();
            double bottom = cell.getBottomBound();
            double left = cell.getLeftBound();
            double right = cell.getRightBound();
            if (polarPoint.getRadius() <= top && polarPoint.getRadius() > bottom) {
                if (polarPoint.getDegrees() >= left && polarPoint.getDegrees() < right) {
                    System.out.println("X: " + cell.getxIndex() + " Y: " + cell.getyIndex());
                    return getSquare(cell.getxIndex(), cell.getyIndex());
                }
            }
        }
        return null;
    }

    public Piece getKingWhite() {
        return model.kingWhite;
    }

    public Piece getKingBlack() {
        return model.kingBlack;
    }
    
    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }
    
    public int getHeight() {
        return view.getHeight();
    }
}
