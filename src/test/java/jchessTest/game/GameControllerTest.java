package jchessTest.game;

import jchess.game.GameController;
import jchess.game.GameModel;
import jchess.game.IGameModel;
import jchess.game.chessboard.controller.IChessboardController;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.player.Player;
import jchess.io.Images;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import static org.mockito.Mockito.*;

public class GameControllerTest {

    GameController gameController;
    Player testPlayer;
    JOptionPane lul;


    @Before
    public void setup() {
        gameController = new GameController();

        testPlayer = new Player("", Images.WHITE_COLOR);

        lul = spy(JOptionPane.class);

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

        //Act
        gameController.redo();

        //Assert
        verify(chessboardControllerMock, times(1)).redo();
    }

}
