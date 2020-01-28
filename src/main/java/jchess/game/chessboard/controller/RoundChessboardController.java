package jchess.game.chessboard.controller;

import jchess.JChessApp;
import jchess.game.player.Player;
import jchess.game.chessboard.PolarPoint;
import jchess.game.chessboard.model.Square;
import jchess.game.IGameModel;
import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.view.RoundChessboardView;
import jchess.game.chessboard.view.SquareView;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.history.IMoveHistoryController;
import jchess.io.Images;
import jchess.move.MoveEvaluator;
import jchess.move.effects.BoardTransition;
import jchess.move.effects.PositionChange;
import jchess.move.effects.StateChange;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;
import jchess.game.chessboard.CartesianPolarConverter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Queue;

/**
 * Class that represents the interaction interface for the RoundChessboard component
 */
public class RoundChessboardController implements IChessboardController {
    private RoundChessboardModel model;
    private AbstractChessboardView view;
    private Square activeSquare;
    private SquareObservable squareObservable;
    private IMoveHistoryController movesHistory;
    private HashSet<BoardTransition> moveEffects = null;

    /**
     * Instantiates the RoundChessboardController with the given arguments.
     *
     * @param settings     The gameModel of the game.
     * @param movesHistory The MoveHistoryController of the game, where the controller will store played moves.
     */
    public RoundChessboardController(RoundChessboardLoader chessboardLoader, int chessboardSize, IGameModel settings, IMoveHistoryController movesHistory) {
        this.model = chessboardLoader.loadDefaultFromJSON(settings);
        this.view = new RoundChessboardView(chessboardSize, Images.BOARD, model.getRows(), model.getColumns(), model.getSquares());
        view.addMouseListener(this);
        this.movesHistory = movesHistory;
        this.squareObservable = new SquareObservable();
    }

    /**
     * @return The view of the chessboard.
     */
    public AbstractChessboardView getView() {
        return view;
    }

    /**
     * @param observer Adds an Observer to the currently selected Square.
     */
    public void addSelectSquareObserver(Observer observer) {
        this.squareObservable.addObserver(observer);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Processes MoueseEvent on mouse press.
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Square square = getSquareFromClick(mouseEvent.getX(), mouseEvent.getY());
        if (square != null)
            squareObservable.setSquare(square);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     *
     * @param fromX The x index of the origin Square.
     * @param fromY The y index of the origin Square.
     * @param toX   The x index of the target Square.
     * @param toY   The y index of the target Square.
     * @return Whether the move is possible.
     */
    public boolean moveIsPossible(int fromX, int fromY, int toX, int toY) {
        Square square = model.getSquare(fromX, fromY);
        Square to = model.getSquare(toX, toY);

        if (square == null || to == null || square.getPiece() == null)
            return false;

        MoveEvaluator evaluator = new MoveEvaluator(this);
        HashSet<Piece> crucialPieces = getCrucialPieces(square.getPiece().getPlayer());
        HashSet<BoardTransition> moveEffects = evaluator
                .getPieceTargetToSavePieces(square.getPiece(), crucialPieces);
        
        HashSet<Square> squares = new HashSet<>();
        moveEffects.forEach(m ->
        	{ 
        		if (m.getMoveHistoryEntry() != null) 
        			squares.add(m.getMoveHistoryEntry().getToSquare()); 
        	});
        
    	return squares.contains(model.getSquare(to.getPozX(), to.getPozY()));
    }

    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     *
     * @param squareFrom The origin Square, containing its x and y indices.
     * @param squareTo   The target Square, containing its x and y indices.
     * @return Whether the move is possible.
     */
    public boolean moveIsPossible(Square squareFrom, Square squareTo) {
        if (squareFrom == null || squareTo == null)
            return false;

        return moveIsPossible(squareFrom.getPozX(), squareFrom.getPozY(), squareTo.getPozX(), squareTo.getPozY());
    }

    /**
     * Selectes the given Square.
     *
     * @param sq The Square.
     */
    public void select(Square sq) {
        setActiveSquare(sq);
        view.repaint();
    }

    /**
     * Checks whether the given Piece cannot be made non-threatened regardless what move its owning Player makes.
     *
     * @param piece The Piece to check.
     * @return Whether the Piece can be saved.
     */
    public boolean pieceIsUnsavable(Piece piece) {
        return new MoveEvaluator(this).pieceIsUnsavable(piece);
    }

    public HashSet<Piece> getCrucialPieces(Player player) {
        return model.getCrucialPieces(player);
    }

    public HashSet<Piece> getCrucialPieces() {
        return model.getCrucialPieces();
    }

    /**
     * @return The currently selected Square.
     */
    public Square getActiveSquare() {
        return activeSquare;
    }

    /**
     * Sets the currently selected Square.
     *
     * @param square The Square to select, by indices.
     */
    public void setActiveSquare(Square square) {
        this.activeSquare = square;
        if (square == null) {
            view.resetActiveCell();
            view.resetPossibleMoves();
            moveEffects = null;
        } else {
            view.setActiveCell(square.getPozX(), square.getPozY());
            moveEffects = new MoveEvaluator(this)
                    .getPieceTargetToSavePieces(square.getPiece(), getCrucialPieces(square.getPiece().getPlayer()));

            HashSet<Square> squares = new HashSet<>();
            for (BoardTransition me : moveEffects)
            	if (me.getMoveHistoryEntry() != null)
            		squares.add(me.getMoveHistoryEntry().getToSquare());
            view.setMoves(squares);
        }
    }

    /**
     * Gets the Square that a given Piece is on, if any.
     *
     * @param piece The Piece whose Square to retrieve.
     * @return The Square of the given Piece, if any.
     */
    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece) : null;//TODO error handling
    }

    /**
     * Gets a List of all Squares in the board.
     *
     * @return The List of Squares.
     */
    public List<Square> getSquares() {
        return this.model.getSquares();
    }

    /**
     * Method move a Piece from the given Square to a new Square, as defined by their x and y indices.
     *
     * @param begin               The origin Square, where the moving Piece is located.
     * @param end                 The target Square, on which the moving Piece should end.
     * @checks refresh             Whether or not to refresh the chessboard.
     * @checks clearForwardHistory Whether or not to clear the forward history of the MoveHistoryController instance for this game.
     */
    public void move(Square begin, Square end) {
    	BoardTransition move = null;
    	
    	for (BoardTransition me : moveEffects)
    		if (me.getMoveHistoryEntry() != null && model.getSquare(me.getMoveHistoryEntry().getPiece()) == begin 
    			&& me.getMoveHistoryEntry().getToSquare() == end) {
    			move = me;
    			break;
    		}
    	
    	applyBoardTransition(move);
    	
        this.unselect();
        this.movesHistory.clearMoveForwardStack();
        this.movesHistory.addMove(move);

        view.updateAfterMove();
    }
    
    public void applyBoardTransition(BoardTransition boardTransition) {
        applyPositionChanges(boardTransition.getPositionChanges());
        applyStateChanges(boardTransition.getStateChanges());
    }

    /**
     * Method to remove the piece once moved from current position to next square where it is moved
     *
     * @param positionChanges
     * gets the information about the piece and the next square on which it is moved and sets the visual of the piece there
     */


    private void applyPositionChanges(List<PositionChange> positionChanges) {
    	for (PositionChange positionChange : positionChanges) {
            if (view != null)
                view.removeVisual(model.getSquare(positionChange.getPiece()));
                
            model.setPieceOnSquare(positionChange.getPiece(), positionChange.getSquare());
            if (view != null) {
                final Square square = positionChange.getSquare();
                if (square != null)
                	view.setVisual(square.getPiece(), square.getPozX(), square.getPozY());
            }
        }

    }

    
    private void applyStateChanges(List<StateChange> stateChanges) {
        for (StateChange stateChange : stateChanges) {
            final Square sq = model.getSquare(stateChange.getID());
            if (sq == null)
                continue;

            if (stateChange.getState().getDefinition() == PieceDefinition.PLACEHOLDER)
                stateChange.getState().setDefinition(PieceLoader.getPieceDefinition(
                        JChessApp.jcv.showPawnPromotionBox(stateChange.getState().getPlayer().getColor().getColor())));

            model.setPieceOnSquare(stateChange.getState(), sq);
            if (view != null)
                view.setVisual(stateChange.getState(), sq.getPozX(), sq.getPozY());
        }
    }

    public void reverseBoardTransition(BoardTransition boardTransition) {
        applyStateChanges(boardTransition.getStateChangesReverse());
        applyPositionChanges(boardTransition.getPositionChangesReverse());
    }

    /**
     * Unselects the currently selected Square.
     */
    public void unselect() {
        setActiveSquare(null);
    }

    /**
     * Undoes the last-played move.
     *
     * @return Whether or not the undo operation was successful.
     */
    public boolean undo() {
        Queue<BoardTransition> last = this.movesHistory.undo();

        if (last != null && last.size() != 0) {
            for (BoardTransition me : last)
                reverseBoardTransition(me);

            this.unselect();
            view.repaint();

            return true;
        } else return false;
    }

    /**
     * Redoes the move that was undone last.
     *
     * @return Whether or not the redo operation was successful.
     */
    public boolean redo() {
        Queue<BoardTransition> first = this.movesHistory.redo();

        if (first != null && first.size() != 0) {
            for (BoardTransition me : first)
                applyBoardTransition(me);

            this.unselect();
            view.repaint();

            return true;
        }

        return false;
    }

    /**
     * Gets the Square instance from a mouse click on the board view.
     *
     * @param x The x coordinate of the mouse click.
     * @param y The y coordinate of the mouse click.
     * @return Reference to the clicked Square, if any.
     */
    public Square getSquareFromClick(int x, int y) {
        Point clickedPoint = new Point(x, y);
        CartesianPolarConverter converter = new CartesianPolarConverter();
        PolarPoint polarPoint = converter.getPolarFromCartesian(clickedPoint, view.getCircleCenter());

        for (SquareView cell : view.getCells()) {
            double top = cell.getTopBound(), bottom = cell.getBottomBound(), left = cell.getLeftBound(), right = cell.getRightBound();

            if (polarPoint.getRadius() <= top && polarPoint.getRadius() > bottom && polarPoint.getDegrees() >= left && polarPoint.getDegrees() < right)
                return getSquare(cell.getxIndex(), cell.getyIndex());
        }
        return null;
    }

    /**
     * Gets the Square with the given board indices.
     *
     * @param x The x index of the Square.
     * @param y The y index of the Square.
     * @return The Square.
     */
    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }

    public RoundChessboardModel getModel() {
        return model;
    }
}
