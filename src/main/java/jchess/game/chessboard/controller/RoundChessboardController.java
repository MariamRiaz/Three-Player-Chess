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
     * {@inheritDoc}
     */
    public AbstractChessboardView getView() {
        return view;
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectSquareObserver(Observer observer) {
        this.squareObservable.addObserver(observer);
    }

    /**
     * Processes a mouse-click event.
     */
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

    /**
     * Processes a mouse-released event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Processes a mouse-entered event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Processes a mouse-exited event.
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean moveIsPossible(Square squareFrom, Square squareTo) {
        if (squareFrom == null || squareTo == null)
            return false;

        return moveIsPossible(squareFrom.getPozX(), squareFrom.getPozY(), squareTo.getPozX(), squareTo.getPozY());
    }

    /**
     * {@inheritDoc}
     */
    public void select(Square sq) {
        setActiveSquare(sq);
        view.repaint();
    }

    /**
     * {@inheritDoc}
     */
    public boolean pieceIsUnsavable(Piece piece) {
        return new MoveEvaluator(this).pieceIsUnsavable(piece);
    }

    /**
     * {@inheritDoc}
     */
    public HashSet<Piece> getCrucialPieces(Player player) {
        return model.getCrucialPieces(player);
    }

    /**
     * {@inheritDoc}
     */
    public HashSet<Piece> getCrucialPieces() {
        return model.getCrucialPieces();
    }

    /**
     * {@inheritDoc}
     */
    public Square getActiveSquare() {
        return activeSquare;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece) : null;//TODO error handling
    }

    /**
     * {@inheritDoc}
     */
    public List<Square> getSquares() {
        return this.model.getSquares();
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    public void applyBoardTransition(BoardTransition boardTransition) {
        applyPositionChanges(boardTransition.getPositionChanges());
        applyStateChanges(boardTransition.getStateChanges());
    }

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

    /**
     * {@inheritDoc}
     */
    public void reverseBoardTransition(BoardTransition boardTransition) {
        applyStateChanges(boardTransition.getStateChangesReverse());
        applyPositionChanges(boardTransition.getPositionChangesReverse());
    }

    /**
     * {@inheritDoc}
     */
    public void unselect() {
        setActiveSquare(null);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
    private Square getSquareFromClick(int x, int y) {
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
     * {@inheritDoc}
     */
    public Square getSquare(int x, int y) {
        return model.getSquare(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public RoundChessboardModel getModel() {
        return model;
    }
}
