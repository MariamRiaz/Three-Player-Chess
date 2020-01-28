package jchessTest.chessboard;

import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.chessboard.view.RoundChessboardView;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryController;
import jchess.game.history.MoveHistoryEntry;
import jchess.move.MoveEvaluator;
import jchess.move.effects.BoardTransition;
import jchess.move.effects.PositionChange;
import jchess.move.effects.StateChange;
import jchess.pieces.Piece;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoundChessboardControllerTest {

    RoundChessboardController sut;
    RoundChessboardLoader chessboardLoader;
    IMoveHistoryController moveHistoryController;
    MoveEvaluator evaluator;
    RoundChessboardModel model;
    MoveHistoryEntry entry;

    private void setupModel(RoundChessboardModel model) {
        List<Square> squares = new ArrayList<>();
        squares.add(new Square(0, 0, null));
        squares.add(new Square(1, 0, null));
        squares.add(new Square(0, 1, null));
        squares.add(new Square(1, 1, null));
        when(model.getColumns()).thenReturn(2);
        when(model.getRows()).thenReturn(2);
        when(model.getSquares()).thenReturn(squares);
    }

    @Before
    public void setUp() {
        chessboardLoader = new RoundChessboardLoader();
        evaluator = mock(MoveEvaluator.class);
        model = mock(RoundChessboardModel.class);
        setupModel(model);
        moveHistoryController = mock(MoveHistoryController.class);
        sut = new RoundChessboardController(model, 800, moveHistoryController);
    }

    @Test
    public void testInit() {
        assertNotNull(sut.getModel());
        assertNotNull(sut.getView());
        assertNotNull(sut.getSquares());
    }

    @Test
    public void getView() {
        AbstractChessboardView view = sut.getView();
        assert view.getWidth() == 800;
        assert view.getHeight() == 800;
        assert view.getCircleCenter().getX() == 400;
        assert view.getCircleCenter().getY() == 400;
    }

    @Test
    public void testMoveIsNotPossible() {
        // Arrange
        Piece piece = mock(Piece.class);
        when(evaluator.getPieceTargetToSavePieces(piece, new HashSet<>())).thenReturn(new HashSet<>());
        Square squareFrom = new Square(0, 0, piece);
        Square squareTo = new Square(1, 1, piece);
        when(model.getSquare(squareTo.getPozX(), squareTo.getPozY())).thenReturn(squareTo);
        // Assert
        assertFalse(sut.moveIsPossible(squareFrom, squareTo, evaluator));
    }

    public HashSet<BoardTransition> setupMoveIsPossibleTransitions() {
        entry = mock(MoveHistoryEntry.class);
        BoardTransition transition = mock(BoardTransition.class);
        when(transition.getMoveHistoryEntry()).thenReturn(entry);
        HashSet<BoardTransition> transitions = new HashSet<>();
        transitions.add(transition);
        return transitions;
    }

    @Test
    public void testMoveIsPossible() {
        // Arrange
        Piece piece = mock(Piece.class);
        HashSet<BoardTransition> boardTransitions = setupMoveIsPossibleTransitions();
        when(evaluator.getPieceTargetToSavePieces(piece, new HashSet<>())).thenReturn(boardTransitions);
        Square squareFrom = new Square(0, 0, piece);
        Square squareTo = new Square(1, 1, piece);
        // Act
        when(entry.getToSquare()).thenReturn(squareTo);
        when(model.getSquare(squareTo.getPozX(), squareTo.getPozY())).thenReturn(squareTo);
        // Assert
        assertTrue(sut.moveIsPossible(squareFrom, squareTo, evaluator));
    }

    @Test
    public void testPieceIsUnsavable() {
        // Arrange
        Piece piece = mock(Piece.class);
        // Act
        when(evaluator.pieceIsUnsavable(piece)).thenReturn(true);
        // Assert
        assertTrue(sut.pieceIsUnsavable(piece, evaluator));
    }

    @Test
    public void testPieceIsNotUnsavable() {
        // Arrange
        Piece piece = mock(Piece.class);
        // Act
        when(evaluator.pieceIsUnsavable(piece)).thenReturn(false);
        // Assert
        assertFalse(sut.pieceIsUnsavable(piece, evaluator));
    }

    @Test
    public void testApplyBoardTransitionGivenValidPositionChange() {
        BoardTransition transition = mock(BoardTransition.class);
        Piece pieceMock = mock(Piece.class);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        Square square = new Square(0, 0, pieceMock);
        PositionChange change = new PositionChange(pieceMock, square);
        ArrayList<PositionChange> positionChanges = new ArrayList<>(Collections.singletonList(change));
        when(transition.getPositionChanges()).thenReturn(positionChanges);
        when(model.getSquare(pieceMock)).thenReturn(square);
        // Act
        sut.applyBoardTransition(transition);
        // Assert
        verify(model, times(1)).setPieceOnSquare(pieceMock, change.getSquare());
        verify(viewMock, times(1)).removeVisual(square);
        verify(viewMock, times(1)).setVisual(pieceMock,
                change.getSquare().getPozX(), change.getSquare().getPozY());
    }


    @Test
    public void testApplyBoardTransitionGivenValidStateChange() {
        BoardTransition transition = mock(BoardTransition.class);
        Piece pieceMock = mock(Piece.class);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        Square square = new Square(0, 0, pieceMock);
        StateChange change = new StateChange(0, pieceMock);
        ArrayList<StateChange> stateChanges = new ArrayList<>(Collections.singletonList(change));
        when(transition.getStateChanges()).thenReturn(stateChanges);
        when(model.getSquare(0)).thenReturn(square);
        // Act
        sut.applyBoardTransition(transition);
        // Assert
        verify(model, times(1)).setPieceOnSquare(pieceMock, square);
        verify(viewMock, times(1)).setVisual(pieceMock,
                square.getPozX(), square.getPozY());
    }

    @Test
    public void testReverseBoardTransitionGivenValidPositionChange() {
        BoardTransition transition = mock(BoardTransition.class);
        Piece pieceMock = mock(Piece.class);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        Square square = new Square(0, 0, pieceMock);
        PositionChange change = new PositionChange(pieceMock, square);
        ArrayList<PositionChange> positionChanges = new ArrayList<>(Collections.singletonList(change));
        when(transition.getPositionChangesReverse()).thenReturn(positionChanges);
        when(model.getSquare(pieceMock)).thenReturn(square);
        // Act
        sut.reverseBoardTransition(transition);
        // Assert
        verify(model, times(1)).setPieceOnSquare(pieceMock, change.getSquare());
        verify(viewMock, times(1)).removeVisual(square);
        verify(viewMock, times(1)).setVisual(pieceMock,
                change.getSquare().getPozX(), change.getSquare().getPozY());
    }


    @Test
    public void testReverseBoardTransitionGivenValidStateChange() {
        BoardTransition transition = mock(BoardTransition.class);
        Piece pieceMock = mock(Piece.class);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        Square square = new Square(0, 0, pieceMock);
        StateChange change = new StateChange(0, pieceMock);
        ArrayList<StateChange> stateChanges = new ArrayList<>(Collections.singletonList(change));
        when(transition.getStateChangesReverse()).thenReturn(stateChanges);
        when(model.getSquare(0)).thenReturn(square);
        // Act
        sut.reverseBoardTransition(transition);
        // Assert
        verify(model, times(1)).setPieceOnSquare(pieceMock, square);
        verify(viewMock, times(1)).setVisual(pieceMock,
                square.getPozX(), square.getPozY());
    }

    @Test
    public void testUndoWhenTrue() {
        // Arrange
        BoardTransition mockedBoardTransition = mock(BoardTransition.class);
        Queue<BoardTransition> boardTransitions = new LinkedList<>();
        boardTransitions.add(mockedBoardTransition);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        when(moveHistoryController.undo()).thenReturn(boardTransitions);
        // Act
        boolean result = sut.undo();
        // Assert
        verify(moveHistoryController, times(1)).undo();
        verify(viewMock, times(1)).repaint();
        assertTrue(result);
    }


    @Test
    public void testUndoWhenNotPossible() {
        // Arrange
        Queue<BoardTransition> boardTransitions = new LinkedList<>();
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        when(moveHistoryController.undo()).thenReturn(boardTransitions);
        // Act
        boolean result = sut.undo();
        // Assert
        verify(moveHistoryController, times(1)).undo();
        verify(viewMock, times(0)).repaint();
        assertFalse(result);
    }

    @Test
    public void testRedoWhenPossible() {
        // Arrange
        BoardTransition mockedBoardTransition = mock(BoardTransition.class);
        Queue<BoardTransition> boardTransitions = new LinkedList<>();
        boardTransitions.add(mockedBoardTransition);
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        when(moveHistoryController.redo()).thenReturn(boardTransitions);
        // Act
        boolean result = sut.redo();
        // Assert
        verify(moveHistoryController, times(1)).redo();
        verify(viewMock, times(1)).repaint();
        assertTrue(result);
    }

    @Test
    public void testRedoWhenNotPossible() {
        // Arrange
        Queue<BoardTransition> boardTransitions = new LinkedList<>();
        AbstractChessboardView viewMock = mock(RoundChessboardView.class);
        sut.setView(viewMock);
        when(moveHistoryController.redo()).thenReturn(boardTransitions);
        // Act
        boolean result = sut.redo();
        // Assert
        verify(moveHistoryController, times(1)).redo();
        verify(viewMock, times(0)).repaint();
        assertFalse(result);
    }
}
