package jchess.controller;

import jchess.*;
import jchess.UI.board.Square;
import jchess.helper.CartesianPolarConverter;
import jchess.view.PolarCell;
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

public class RoundChessboardController {

    private RoundChessboardModel model;
    private RoundChessboardView view;
    private Square activeSquare;
    private Settings settings;

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

    public RoundChessboardController(Settings settings, MoveHistory movesHistory) {
        this.model = new RoundChessboardModel(rows, squaresPerRow, settings);
        this.movesHistory = movesHistory;
        this.view = new RoundChessboardView(600, "3-player-board.png", rows, squaresPerRow, model.squares);
        this.settings = settings;
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
    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {

        try {
            select(model.getSquare(beginX, beginY));
            if (getValidTargetSquares(activeSquare.getPiece()).contains(model.getSquare(endX, endY))) // move
            {
                move(model.getSquare(beginX, beginY), model.getSquare(endX, endY), true, true);
            } else {
                Log.log("Bad move");
                return false;
            }
            unselect();
            return true;

        } catch (StringIndexOutOfBoundsException | NullPointerException | ArrayIndexOutOfBoundsException exc) {
            return false;
        } finally {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, "ERROR");
        }
    }

    public void select(Square sq) {
        setActiveSquare(sq);
        view.setActiveCell(sq.getPozX(), sq.getPozY());
        view.setMoves(getValidTargetSquares(sq.getPiece()));
        Log.log("Selected square with X: "
                + activeSquare.getPozX() + " and Y: " + activeSquare.getPozY());
        view.repaint();
    }

    public HashSet<Square> getValidTargetSquares(Piece piece) {
        HashSet<Square> ret = new HashSet<>();

        if (piece == null)
            return ret;//TODO error handling

        HashSet<Piece.Move> moves = piece.getMoves();
        for (Iterator<Piece.Move> it = moves.iterator(); it.hasNext(); )
            ret.addAll(evaluateMoveToTargetSquares(it.next(), piece));

        return ret;
    }

    private HashSet<Square> evaluateMoveToTargetSquares(Piece.Move move, Piece piece) {
        HashSet<Square> ret = new HashSet<Square>();
		
		if (move == null || piece == null)
			return ret;
		
		int count = 0;
		for (Square next = nextSquare(getSquare(piece), move.x, move.y); next != null
				&& (move.limit == null || count < move.limit); next = nextSquare(next, move.x, move.y)) {
			boolean add = true;
			
			if (move.conditions.contains(MoveType.OnlyAttack)) {
				if (next.getPiece() == null || next.getPiece().player == piece.player)
					add = false;
			} else if (move.conditions.contains(MoveType.OnlyMove)) {
				if (next.getPiece() != null)
					add = false;
			} else if (next.getPiece() != null && next.getPiece().player == piece.player)
				add = false;
			
			if (move.conditions.contains(MoveType.OnlyWhenFresh) && piece.hasMoved())
				add = false;

			if (move.conditions.contains(MoveType.EnPassant)) {
				add = false;
				
				Piece target = getPawnSkippedOverSquareEnPassant(next);
				if (target != null && target.player != piece.player)
					add = true;
			}
			
			if (move.conditions.contains(MoveType.Castling)) {
				Square current = getSquare(piece);
				
				if (squareThreatened(current, piece.player, getKingBlack(), getKingWhite()))
					add = false;
				else {
					int start = move.x < 0 ? 0 : current.getPozX() + 1,
							end = move.x < 0 ? current.getPozX() : squaresPerRow;
					
					Piece rk = getSquare(move.x < 0 ? 0 : end - 1, current.getPozY()).getPiece();
					if (rk == null || rk.hasMoved())
						add = false;
					else for (; start < end; start++) {
							Square sq = getSquare(start, current.getPozY());
							if ((sq.getPiece() != null && sq.getPiece() != rk) || squareThreatened(sq, piece.player, piece, getKingBlack(), getKingWhite())) {
								add = false;
								break;
							}
					}
				}
			}
			
			if (add)
				ret.add(next);
			
			if (!move.conditions.contains(MoveType.Unblockable) && next.getPiece() != null)
				break;
			
			count++;
		}
		
		return ret;
    }
    
	private Piece getPawnSkippedOverSquareEnPassant(Square square) {
		java.util.List<PlayedMove> lastMoves = movesHistory.getLastMoveOfEachPlayer();
		for (Iterator<PlayedMove> it = lastMoves.iterator(); it.hasNext();) {
			PlayedMove lastMove = it.next();
			
			if (lastMove.wasPawnTwoFieldsMove() 
					&& square == getSquare(lastMove.getTo().getPozX(), lastMove.getFrom().getPozY() + (lastMove.getTo().getPozY() - lastMove.getFrom().getPozY()) / 2))
				return lastMove.getTo().getPiece();
		}
		
		return null;
	}

    private Square nextSquare(Square current, int x, int y) {
        return model.getSquare(current.getPozX() + x, current.getPozY() + y);
    }

    public Square getActiveSquare() {
        return activeSquare;
    }

    public void setActiveSquare(Square square) {
        this.activeSquare = square;
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
        return model.kingWhite;
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
			Piece pawnForEnPassant = getPawnSkippedOverSquareEnPassant(end);
			if (pawnForEnPassant != null)
			{
				tempEnd = pawnForEnPassant;
				model.setPieceOnSquare(tempEnd, null);
				
				wasEnPassant = true;
			}

			if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) // moved two square
				breakCastling = true;

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
					
					model.removePieceFromSquare(begin);
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
        view.resetActiveCell();
        view.resetPossibleMoves();
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
				Piece promoted = getSquare(end).getPiece();
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

    public boolean pieceUnsavable(Piece piece) {
        if (piece == null)
            return false;
        for (Square square : model.squares) {
            if (square.getPiece() == null || square.getPiece().player != piece.player)
                continue;

            if (!getValidTargetSquaresToSavePiece(square.getPiece(), piece).isEmpty())
                return false;
        }
        return true;
    }

    public Square getSquare(Square square) {
    	if (square != null)
    		return getSquare(square.getPozX(), square.getPozY());
    	return null;
    }
    
    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }

    public HashSet<Square> getValidTargetSquaresToSavePiece(Piece moving, Piece toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;

        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            Square target = it.next(), start = getSquare(moving);
            Piece old = target.getPiece();

            model.setPieceOnSquare(moving, target);
            if (pieceThreatened(toSave))
                it.remove();

            model.setPieceOnSquare(old, target);
            model.setPieceOnSquare(moving, start);
        }

        return ret;
    }
    
    public boolean pieceThreatened(Piece piece) {
    	return squareThreatened(getSquare(piece), piece.player);
    }
    
    public boolean squareThreatened(Square square, Player player, Piece... exclude) {
        if (square == null)
			return false;
		
		java.util.List<Piece> exclusionList = Arrays.asList(exclude);
		
		for (Square el : model.squares) {
            if (el.getPiece() == null)
                continue;
            if (el.getPiece().player == player)
                continue;
			
			if (exclusionList.contains(el.getPiece()) || el.getPiece().player == player)
				continue;

			HashSet<Square> validMoveSquares = getValidTargetSquares(el.getPiece());
			for (Iterator<Square> it2 = validMoveSquares.iterator(); it2.hasNext();)
				if (it2.next() == square)
					return true;
		}

		return false;
    }

    public int getHeight() {
        return view.getHeight();
    }
}
