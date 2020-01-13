package jchess.controller;

import jchess.JChessApp;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.entities.SquareObservable;
import jchess.helper.CartesianPolarConverter;
import jchess.helper.MoveEvaluator;
import jchess.entities.PolarPoint;
import jchess.model.RoundChessboardModel;
import jchess.pieces.Orientation;
import jchess.pieces.Piece;
import jchess.pieces.PieceLoader;
import jchess.entities.PlayedMove;
import jchess.view.PolarCell;
import jchess.view.RoundChessboardView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observer;

/**
 * Class that represents the interaction interface for the RoundChessboard component
 */
public class RoundChessboardController extends MouseAdapter {
    private RoundChessboardModel model;
    private RoundChessboardView view;
    private Square activeSquare;
    private Settings settings;
    private SquareObservable squareObservable;
    private Piece twoSquareMovedPawn = null;
    private MoveHistory movesHistory;

    public static int bottom = 7;
    public static int top = 0;

    /**
     * Instantiates the RoundChessboardController with the given arguments.
     * @param model The chessboard model to be used by the controller.
     * @param view The chessboard view to be used by the controller.
     * @param settings The settings of the game.
     * @param movesHistory The MoveHistory of the game, where the controller will store played moves.
     */
    public RoundChessboardController(RoundChessboardModel model, RoundChessboardView view, Settings settings, MoveHistory movesHistory) {
        this.model = model;
        this.view = view;
        view.addMouseListener(this);
        this.movesHistory = movesHistory;
        this.settings = settings;
        this.squareObservable = new SquareObservable();
    }

    /**
     * @return The view of the chessboard.
     */
    public RoundChessboardView getView() {
        return view;
    }

    /**
     * @param observer Adds an Observer to the currently selected Square.
     */
    public void addSelectSquareObserver(Observer observer) {
        this.squareObservable.addObserver(observer);
    }

    /**
     * Processes MoueseEvent on mouse press.
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Square square = getSquareFromClick(mouseEvent.getX(), mouseEvent.getY());
        if(square != null)
            squareObservable.setSquare(square);
    }

    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     * @param fromX The x index of the origin Square.
     * @param fromY The y index of the origin Square.
     * @param toX The x index of the target Square.
     * @param toY The y index of the target Square.
     * @return Whether the move is possible.
     */
    public boolean moveIsPossible(int fromX, int fromY, int toX, int toY) {
    	Square square = model.getSquare(fromX, fromY), to = model.getSquare(toX, toY);

    	if (square == null || to == null || square.getPiece() == null)
    		return false;
    	return new MoveEvaluator(model)
    			.getValidTargetSquaresToSavePiece(square, model.getSquare(getKing(square.getPiece().player)))
    			.contains(model.getSquare(to.getPozX(), to.getPozY()));
    }

    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     * @param squareFrom The origin Square, containing its x and y indices.
     * @param squareTo The target Square, containing its x and y indices.
     * @return Whether the move is possible.
     */
    public boolean moveIsPossible(Square squareFrom, Square squareTo) {
    	if (squareFrom == null || squareTo == null)
    		return false;

    	return moveIsPossible(squareFrom.getPozX(), squareFrom.getPozY(), squareTo.getPozX(), squareTo.getPozY());
    }

    /**
     * Selectes the given Square.
     * @param sq The Square.
     */
    public void select(Square sq) {
        setActiveSquare(sq);
        view.repaint();
    }
    
    /**
     * Checks whether the given Piece is threatened by Pieces of other Players.
     * @param piece The Piece to check.
     * @return Whether the Piece is threatened.
     */
    public boolean pieceIsThreatened(Piece piece) {
    	return new MoveEvaluator(model).squareIsThreatened(model.getSquare(piece));
    }

    /**
     * Checks whether the given Piece cannot be made non-threatened regardless what move its owning Player makes.
     * @param piece The Piece to check.
     * @return Whether the Piece can be saved.
     * @see RoundChessboardController.pieceIsThreatened
     */
    public boolean pieceIsUnsavable(Piece piece) {
    	return new MoveEvaluator(model).squareIsUnsavable(model.getSquare(piece));
    }

    /**
     * @return The currently selected Square.
     */
    public Square getActiveSquare() {
        return activeSquare;
    }

    /**
   	 * Sets the currently selected Square.
     * @param square The Square to select, by indices.
     */
    public void setActiveSquare(Square square) {
        this.activeSquare = square;
        if(square == null) {
            view.resetActiveCell();
            view.resetPossibleMoves();
        } else {
            view.setActiveCell(square.getPozX(), square.getPozY());
            view.setMoves(new MoveEvaluator(model)
                    .getValidTargetSquaresToSavePiece(square, model.getSquare(getKing(square.getPiece().player))));
        }
    }

    /**
     * Gets the Square that a given Piece is on, if any.
     * @param piece The Piece whose Square to retrieve.
     * @return The Square of the given Piece, if any.
     */
    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece) : null;//TODO error handling
    }

    /**
     * Gets a List of all Squares in the board.
     * @return The List of Squares.
     */
    public List<Square> getSquares() {
        return this.model.squares;
    }

    /**
     * Gets the king Piece of a given Player.
     * @param player The Player whose King to retrieve.
     * @return The given Player's King.
     */
    public Piece getKing(Player player) {
        if (player == null)
            return null;
        if (player.color == Player.colors.black)
            return model.kingBlack;
        else if (player.color == Player.colors.gray)
        	return model.kingGray;
        return model.kingWhite;
    }

    /**
     * Method move a Piece from the given Square to a new Square, as defined by their x and y indices.
     * @param begin The origin Square, where the moving Piece is located.
     * @param end The target Square, on which the moving Piece should end.
     * @param refresh Whether or not to refresh the chessboard.
     * @param clearForwardHistory Whether or not to clear the forward history of the MoveHistory instance for this game.
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {
        MoveHistory.castling wasCastling = MoveHistory.castling.none;
        Piece promotedPiece = null;
        boolean wasEnPassant = false;

        Piece tempBegin = begin.getPiece(), tempBeginState = tempBegin != null ? tempBegin.clone() : null;
        Piece tempEnd = end.getPiece();

        model.setPieceOnSquare(begin.getPiece(), end);
        model.setPieceOnSquare(null, begin);
        
        if (end.getPiece().getDefinition().type.equals("King")) {
            end.getPiece().setHasMoved(true);

            if (begin.getPozX() + 2 == end.getPozX()) {
                move(model.getSquare(7, begin.getPozY()), model.getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
                wasCastling = MoveHistory.castling.shortCastling;
            } else if (begin.getPozX() - 2 == end.getPozX()) {
                move(model.getSquare(0, begin.getPozY()), model.getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
                wasCastling = MoveHistory.castling.longCastling;
            }
        } else if (end.getPiece().getDefinition().type.equals("Rook")) 
            end.getPiece().setHasMoved(true);
        else if (end.getPiece().getDefinition().type.equals("Pawn")) {
            if (twoSquareMovedPawn != null && model.getSquare(end.getPozX(), begin.getPozY()) == getSquare(twoSquareMovedPawn)) {
                tempEnd = model.getSquare(end.getPozX(), begin.getPozY()).getPiece();

                model.getSquare(end.getPozX(), begin.getPozY()).setPiece(null);
                wasEnPassant = true;
            }

            if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2)
                twoSquareMovedPawn = end.getPiece();
            else twoSquareMovedPawn = null;

            end.getPiece().setHasMoved(true);

            if (end.getPozY() == 1 || end.getPozY() == 9 || end.getPozY() == 17)
            {
                if (clearForwardHistory) {
                    String color;
                    if (end.getPiece().player.color == Player.colors.white)
                        color = "W";
                    else color = "B";

                    String newPiece = JChessApp.jcv.showPawnPromotionBox(color);
                    
                	end.getPiece().setDefinition(PieceLoader.getPieceDefinition(newPiece));
                	
                    view.setVisual(end.getPiece(), end.getPozX(), end.getPozY());
                    promotedPiece = end.getPiece();
                }
            }
        } else if (!end.getPiece().getDefinition().type.equals("Pawn"))
            twoSquareMovedPawn = null;
        
        if (refresh)
            this.unselect();

        if (clearForwardHistory) {
            this.movesHistory.clearMoveForwardStack();
            this.movesHistory.addMove(begin, end, tempBegin, tempBeginState, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
        } else this.movesHistory.addMove(begin, end, tempBegin, tempBeginState, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
        
        view.updateAfterMove(end.getPiece(), begin.getPozX(), begin.getPozY(), end.getPozX(), end.getPozY());
        end.getPiece().setHasMoved(true);
    }

    /**
     * Unselects the currently selected Square.
     */
    public void unselect() {
        setActiveSquare(null);
    }

    /**
     * Undoes the last-played move.
     * @param refresh Whether or not to refresh the board.
     * @return Whether or not the undo operation was successful.
     */
    public synchronized boolean undo(boolean refresh)
    {
        PlayedMove last = this.movesHistory.undo();
        
        if (last != null && last.getFrom() != null) {
            Square begin = last.getFrom();
            Square end = last.getTo();
            
            try {
                Piece moved = last.getMovedPiece(), movedState = last.getMovedPieceState();
                
                model.setPieceOnSquare(moved, null);
                view.removeVisual(moved, end.getPozX(), end.getPozY());
                model.setPieceOnSquare(movedState, begin);
                view.setVisual(movedState, begin.getPozX(), begin.getPozY());

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != MoveHistory.castling.none) {
                    Piece rook = null;
                    if (last.getCastlingMove() == MoveHistory.castling.shortCastling) {
                        rook = model.getSquare(end.getPozX() - 1, end.getPozY()).getPiece();
                        model.setPieceOnSquare(rook, model.getSquare(7, begin.getPozY()));

                        view.setVisual(rook, 7, begin.getPozY());
                    } else {
                        rook = model.getSquare(end.getPozX() + 1, end.getPozY()).getPiece();
                        model.setPieceOnSquare(rook, model.getSquare(0, begin.getPozY()));
                        view.setVisual(rook, 0, begin.getPozY());
                    }
                    movedState.setHasMoved(false);
                    rook.setHasMoved(false);
                } else if (movedState.getDefinition().type.equals("Rook"))
                    movedState.setHasMoved(false);
                else if (movedState.getDefinition().type.equals("Pawn") && last.wasEnPassant()) {
                    Piece pawn = last.getTakenPiece();
                    model.setPieceOnSquare(pawn, model.getSquare(end.getPozX(), begin.getPozY()));
                    view.setVisual(pawn, end.getPozX(), end.getPozY());

                } else if (movedState.getDefinition().type.equals("Pawn") && last.getPromotedPiece() != null) {
                    Piece promoted = model.getSquare(end.getPozX(), end.getPozY()).getPiece();
                    model.setPieceOnSquare(promoted, null);
                    view.removeVisual(promoted, end.getPozX(), end.getPozY());
                }
                
                PlayedMove oneMoveEarlier = this.movesHistory.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
                    Piece canBeTakenEnPassant = model.getSquare(oneMoveEarlier.getTo().getPozX(),
                            oneMoveEarlier.getTo().getPozY()).getPiece();
                    if (canBeTakenEnPassant.getDefinition().type.equals("Pawn"))
                        this.twoSquareMovedPawn = canBeTakenEnPassant;
                }

                if (taken != null && !last.wasEnPassant()) {
                    model.setPieceOnSquare(taken, null);
                    view.removeVisual(taken, last.getTo().getPozX(), last.getTo().getPozY());

                    model.setPieceOnSquare(taken, end);
                    view.setVisual(taken, end.getPozX(), end.getPozY());
                } else {
                    view.removeVisual(end.getPiece(), end.getPozX(), end.getPozY());
                    model.setPieceOnSquare(end.getPiece(), null);
                }

                if (refresh) {
                    this.unselect();
                    view.repaint();
                }

            } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
                return false;
            } catch (java.lang.NullPointerException exc) {
                return false;
            }

            return true;
        } else return false;
    }

    /**
     * Redoes the move that was undone last.
     * @param refresh Whether or not to refresh the board.
     * @return Whether or not the redo operation was successful.
     */
    public boolean redo(boolean refresh) {
        if (this.settings.gameType == Settings.gameTypes.local) {
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
                
                if (refresh) {
                    this.unselect();
                    view.repaint();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the Square instance from a mouse click on the board view.
     * @param x The x coordinate of the mouse click.
     * @param y The y coordinate of the mouse click.
     * @return Reference to the clicked Square, if any.
     */
    public Square getSquareFromClick(int x, int y) {
        Point clickedPoint = new Point(x, y);
        CartesianPolarConverter converter = new CartesianPolarConverter();
        PolarPoint polarPoint = converter.getPolarFromCartesian(clickedPoint, view.getCircleCenter());
        
        for (PolarCell cell : view.getCells()) {
            double top = cell.getTopBound(), bottom = cell.getBottomBound(), left = cell.getLeftBound(), right = cell.getRightBound();
            
            if (polarPoint.getRadius() <= top && polarPoint.getRadius() > bottom && polarPoint.getDegrees() >= left && polarPoint.getDegrees() < right)
            	return getSquare(cell.getxIndex(), cell.getyIndex());
        }
        return null;
    }

    /**
     * Gets the Square with the given board indices.
     * @param x The x index of the Square.
     * @param y The y index of the Square.
     * @return The Square.
     */
    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }

    /**
     * Gets the height of the board view.
     * @return The view height.
     */
    public int getHeight() {
        return view.getHeight();
    }
}
