package jchesstest.game;

import jchess.game.GameController;
import jchess.game.chessboard.controller.IChessboardController;
import jchess.game.chessboard.controller.RoundChessboardController;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GameControllerTest {

    GameController gameController;

    @Before
    public void setup() {
        gameController = new GameController();

    }

    @Test
    public void testUndo() {

        //Arrange
        IChessboardController chessboardControllerMock = mock(RoundChessboardController.class);
        when(chessboardControllerMock.undo()).thenReturn(true);
        gameController.setChessboardController(chessboardControllerMock);

        //Act
        gameController.undo();

        //Assert
        verify(chessboardControllerMock, times(1)).undo();
    }

    @Test
    public void testRedo() {

        //Arrange
        IChessboardController chessboardControllerMock = mock(RoundChessboardController.class);
        when(chessboardControllerMock.redo()).thenReturn(true);
        gameController.setChessboardController(chessboardControllerMock);

        //Act
        gameController.redo();

        //Assert
        verify(chessboardControllerMock, times(1)).redo();
    }

}
