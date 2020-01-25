package jchess.controller;

import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.entities.SquareObservable;
import jchess.helper.CartesianPolarConverter;
import jchess.helper.MoveEvaluator;
import jchess.entities.PolarPoint;
import jchess.model.RoundChessboardModel;
import jchess.move.effects.MoveEffect;
import jchess.pieces.Piece;
import jchess.view.PolarCell;
import jchess.view.RoundChessboardView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
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
    private MoveHistoryController movesHistory;
    private HashSet<MoveEffect> moveEffects = null;

    public static int bottom = 7;
    public static int top = 0;

    /**
     * Instantiates the RoundChessboardController with the given arguments.
     *
     * @param model        The chessboard model to be used by the controller.
     * @param view         The chessboard view to be used by the controller.
     * @param settings     The settings of the game.
     * @param movesHistory The MoveHistoryController of the game, where the controller will store played moves.
     */
    public RoundChessboardController(RoundChessboardModel model, RoundChessboardView view, Settings settings, MoveHistoryController movesHistory) {
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
        if (square != null)
            squareObservable.setSquare(square);
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
        for (MoveEffect me : new MoveEvaluator(model)
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
     * Checks whether the given Piece is threatened by Pieces of other Players.
     *
     * @param piece The Piece to check.
     * @return Whether the Piece is threatened.
     */
    public boolean pieceIsThreatened(Piece piece) {
        return new MoveEvaluator(model).squareIsThreatened(model.getSquare(piece));
    }

    /**
     * Checks whether the given Piece cannot be made non-threatened regardless what move its owning Player makes.
     *
     * @param piece The Piece to check.
     * @return Whether the Piece can be saved.
     */
    public boolean pieceIsUnsavable(Piece piece) {
        return new MoveEvaluator(model).pieceIsUnsavable(piece);
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
            moveEffects = new MoveEvaluator(model)
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
        return this.model.squares;
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

        move.apply(model, view);

        if (refresh)
            this.unselect();

        if (clearForwardHistory) {
            this.movesHistory.clearMoveForwardStack();
            this.movesHistory.addMove(move, true);
        } else this.movesHistory.addMove(move, false);

        view.updateAfterMove();

        //end.getPiece().setHasMoved(true);
        // TODO: Reverse orientation on jump across board center.
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
     * @param refresh Whether or not to refresh the board.
     * @return Whether or not the undo operation was successful.
     */
    public synchronized boolean undo(boolean refresh) {
        MoveEffect last = this.movesHistory.undo();

        if (last != null) {
            last.reverse(model, view);

            if (refresh) {
                this.unselect();
                view.repaint();
            }

            return true;
        } else return false;
    }

    /**
     * Redoes the move that was undone last.
     *
     * @param refresh Whether or not to refresh the board.
     * @return Whether or not the redo operation was successful.
     */
    public boolean redo(boolean refresh) {
        MoveEffect first = this.movesHistory.redo();

        if (first != null) {
            first.apply(model, view);

            if (refresh) {
                this.unselect();
                view.repaint();
            }

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

        for (PolarCell cell : view.getCells()) {
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

    /**
     * Gets the height of the board view.
     *
     * @return The view height.
     */
    public int getHeight() {
        return view.getHeight();
    }
}
