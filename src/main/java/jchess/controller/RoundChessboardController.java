package jchess.controller;

import jchess.JChessApp;
import jchess.controller.loaders.RoundChessboardLoader;
import jchess.entities.Player;
import jchess.entities.PolarPoint;
import jchess.entities.Square;
import jchess.entities.SquareObservable;
import jchess.model.IGameModel;
import jchess.model.Images;
import jchess.model.RoundChessboardModel;
import jchess.move.MoveEvaluator;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.PositionChange;
import jchess.move.effects.StateChange;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;
import jchess.utilities.CartesianPolarConverter;
import jchess.view.AbstractChessboardView;
import jchess.view.SquareView;
import jchess.view.gameview.chessboardview.RoundChessboardView;

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
    private HashSet<MoveEffect> moveEffects = null;

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
        Square square = model.getSquare(fromX, fromY), to = model.getSquare(toX, toY);

        if (square == null || to == null || square.getPiece() == null)
            return false;

        HashSet<Square> squares = new HashSet<>();
        for (MoveEffect me : new MoveEvaluator(this)
                .getValidTargetSquaresToSavePiece(square.getPiece(), getCrucialPieces(square.getPiece().getPlayer())))
            squares.add(me.getTrigger());

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
                    .getValidTargetSquaresToSavePiece(square.getPiece(), getCrucialPieces(square.getPiece().getPlayer()));

            HashSet<Square> squares = new HashSet<>();
            for (MoveEffect me : moveEffects)
                squares.add(me.getTrigger());
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
     * @param refresh             Whether or not to refresh the chessboard.
     * @param clearForwardHistory Whether or not to clear the forward history of the MoveHistoryController instance for this game.
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {
        MoveEffect move = null;

        for (MoveEffect me : moveEffects)
            if (model.getSquare(me.getMoving()) == begin && me.getTrigger() == end) {
                move = me;
                break;
            }

        apply(move);

        if (refresh)
            this.unselect();

        if (clearForwardHistory) {
            this.movesHistory.clearMoveForwardStack();
            this.movesHistory.addMove(move, true, true);
        } else this.movesHistory.addMove(move, false, true);

        view.updateAfterMove();

        //end.getPiece().setHasMoved(true);
        // TODO: Reverse orientation on jump across board center.
    }

    public void apply(MoveEffect me) {
        for (PositionChange ent : me.getPositionChanges()) {
            if (view != null) {
                view.removeVisual(model.getSquare(ent.getPiece()));
            }
            model.setPieceOnSquare(ent.getPiece(), ent.getSquare());
            if (view != null) {
                Square square = ent.getSquare();
                view.setVisual(square.getPiece(), square.getPozX(), square.getPozY());
            }
        }

        for (StateChange ent : me.getStateChanges()) {
            final Square sq = model.getSquare(ent.getID());
            if (sq == null)
                continue;

            if (ent.getState().getDefinition() == PieceDefinition.PLACEHOLDER)
                ent.getState().setDefinition(PieceLoader.getPieceDefinition(
                        JChessApp.jcv.showPawnPromotionBox(ent.getState().getPlayer().getColor().getColor())));

            model.setPieceOnSquare(ent.getState(), sq);
            if (view != null)
                view.setVisual(ent.getState(), sq.getPozX(), sq.getPozY());
        }
    }

    public void reverse(MoveEffect me) {
        if (model == null)
            return;

        for (StateChange ent : me.getStateChangesReverse()) {
            final Square sq = model.getSquare(ent.getID());
            if (sq == null)
                continue;

            model.setPieceOnSquare(ent.getState(), sq);
            if (view != null)
                view.setVisual(ent.getState(), sq.getPozX(), sq.getPozY());
        }

        for (PositionChange ent : me.getPositionChangesReverse()) {
            if (view != null) {
                view.removeVisual(model.getSquare(ent.getPiece()));
            }
            model.setPieceOnSquare(ent.getPiece(), ent.getSquare());
            if (view != null) {
                Square square = ent.getSquare();
                view.setVisual(square.getPiece(), square.getPozX(), square.getPozY());
            }
        }
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
        Queue<MoveEffect> last = this.movesHistory.undo();

        if (last != null && last.size() != 0) {
            for (MoveEffect me : last)
                reverse(me);

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
        Queue<MoveEffect> first = this.movesHistory.redo();

        if (first != null && first.size() != 0) {
            for (MoveEffect me : first)
                apply(me);

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
