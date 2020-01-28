package jchess.game.chessboard.controller;

import jchess.JChessApp;
import jchess.game.chessboard.CartesianPolarConverter;
import jchess.game.chessboard.PolarPoint;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.chessboard.view.RoundChessboardView;
import jchess.game.chessboard.view.SquareView;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryEntry;
import jchess.game.player.Player;
import jchess.io.Images;
import jchess.move.IMoveEvaluator;
import jchess.move.MoveEvaluator;
import jchess.move.effects.BoardTransition;
import jchess.move.effects.PositionChange;
import jchess.move.effects.StateChange;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Queue;

/**
 * Class that represents the interaction interface for the RoundChessboard component.
 */
public class RoundChessboardController implements IChessboardController {
    private RoundChessboardModel model;
    private AbstractChessboardView view;
    private Square activeSquare;
    private SquareObservable squareObservable;
    private IMoveHistoryController movesHistory;
    private HashSet<BoardTransition> moveEffects = null;

    /**
     * Creates a new chessboard controller and populates the view and the model.
     *
     * @param chessboardSize       The size of the chessboard view
     * @param roundChessboardModel A round-chessboard model
     * @param movesHistory         A move history controller
     */
    public RoundChessboardController(RoundChessboardModel roundChessboardModel,
                                     int chessboardSize, IMoveHistoryController movesHistory) {
        this.model = roundChessboardModel;
        this.view = new RoundChessboardView(chessboardSize,
                Images.BOARD, model.getRows(), model.getColumns(), model.getSquares());
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Processes MoueseEvent on mouse press.
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Square square = getSquareFromClick(mouseEvent.getX(), mouseEvent.getY());
        if (square != null) {

            squareObservable.setSquare(square);
        }
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
     * {@inheritDoc}
     */
    public boolean moveIsPossible(Square squareFrom, Square squareTo, MoveEvaluator evaluator) {
        if (squareFrom == null || squareTo == null || squareFrom.getPiece() == null) {
            return false;
        }

        HashSet<Piece> crucialPieces = getCrucialPieces(squareFrom.getPiece().getPlayer());
        HashSet<BoardTransition> moveEffects = evaluator
                .getPieceTargetToSavePieces(squareFrom.getPiece(), crucialPieces);

        HashSet<Square> squares = new HashSet<>();
        moveEffects.forEach(m -> {
            if (m.getMoveHistoryEntry() != null) {
                squares.add(m.getMoveHistoryEntry().getToSquare());
            }
        });
        return squares.contains(model.getSquare(squareTo.getPozX(), squareTo.getPozY()));
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
    public boolean pieceIsUnsavable(Piece piece, IMoveEvaluator evaluator) {
        return evaluator.pieceIsUnsavable(piece);
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

    private void setActiveSquare(Square square) {
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
            for (BoardTransition me : moveEffects) {
                if (me.getMoveHistoryEntry() != null) {
                    squares.add(me.getMoveHistoryEntry().getToSquare());
                }
            }
            view.setMoves(squares);
        }
    }

    public void setView(AbstractChessboardView view) {
        this.view = view;
    }

    /**
     * {@inheritDoc}
     */
    public Square getSquare(Piece piece) {
        return piece != null ? model.getSquare(piece) : null;
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
        for (BoardTransition me : moveEffects) {
            if (me.getMoveHistoryEntry() != null && model.getSquare(me.getMoveHistoryEntry().getPiece()) == begin
                    && me.getMoveHistoryEntry().getToSquare() == end) {
                move = me;
                break;
            }
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
            if (view != null) {
                view.removeVisual(model.getSquare(positionChange.getPiece()));
            }
            model.setPieceOnSquare(positionChange.getPiece(), positionChange.getSquare());
            if (view != null) {
                final Square square = positionChange.getSquare();
                if (square != null) {
                    view.setVisual(square.getPiece(), square.getPozX(), square.getPozY());
                }
            }
        }
    }

    private void applyStateChanges(List<StateChange> stateChanges) {
        for (StateChange stateChange : stateChanges) {
            final Square sq = model.getSquare(stateChange.getID());
            if (sq == null) {
                continue;
            }
            if (stateChange.getState().getDefinition() == PieceDefinition.PLACEHOLDER) {
                stateChange.getState().setDefinition(PieceLoader.getPieceDefinition(
                        JChessApp.jcv.showPawnPromotionBox(stateChange.getState().getPlayer().getColor().getColor())));
            }
            model.setPieceOnSquare(stateChange.getState(), sq);
            if (view != null) {
                view.setVisual(stateChange.getState(), sq.getPozX(), sq.getPozY());
            }
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
            for (BoardTransition me : last) {
                reverseBoardTransition(me);
            }
            this.unselect();
            view.repaint();
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean redo() {
        Queue<BoardTransition> first = this.movesHistory.redo();
        if (first != null && first.size() != 0) {
            for (BoardTransition me : first) {
                applyBoardTransition(me);
            }
            this.unselect();
            view.repaint();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Square getSquareFromClick(int x, int y) {
        Point clickedPoint = new Point(x, y);
        CartesianPolarConverter converter = new CartesianPolarConverter();
        PolarPoint polarPoint = converter.getPolarFromCartesian(clickedPoint, view.getCircleCenter());
        for (SquareView cell : view.getCells()) {
            double top = cell.getTopBound(),
                    bottom = cell.getBottomBound(), left = cell.getLeftBound(), right = cell.getRightBound();
            if (polarPoint.getRadius() <= top && polarPoint.getRadius() > bottom && polarPoint.getDegrees() >= left
                    && polarPoint.getDegrees() < right) {
                return getSquare(cell.getxIndex(), cell.getyIndex());
            }
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
