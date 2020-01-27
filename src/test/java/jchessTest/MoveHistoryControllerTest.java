package jchessTest;

import com.google.gson.JsonParser;
import jchess.JChessApp;
import jchess.game.GameController;
import jchess.game.GameModel;
import jchess.game.IGameController;
import jchess.game.IGameModel;
import jchess.game.chessboard.model.Square;
import jchess.game.history.IMoveHistoryView;
import jchess.game.history.MoveHistoryController;
import jchess.game.history.MoveHistoryModel;
import jchess.game.player.Player;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryControllerTest {
    IGameController gameController;
    MoveHistoryController moveHistoryController;
    MoveHistoryModel moveHistoryModel;
    IMoveHistoryView moveHistoryView;
    IGameModel settingsMock;
    Square square1;
    Square square2;
    private Piece pieceMock;
    ArrayList<Character> columnNames = new ArrayList<>();
    MoveEffect moveEffect;
    MoveEffectsBuilder moveEffectsBuilder;


    @Before
    public void setup() {
        gameController = mock(GameController.class);
        settingsMock = mock(GameModel.class);
        pieceMock = mock(Piece.class);
        moveEffect = mock(MoveEffect.class);
        square1 = mock(Square.class);
        square2 = mock(Square.class);


        Player testPlayer = new Player("test", "white");
        columnNames.add('a');
        columnNames.add('b');
        columnNames.add('c');

        when(square1.getPozX()).thenReturn(1);
        when(square1.getPozY()).thenReturn(2);

        when(square1.getPozX()).thenReturn(3);
        when(square1.getPozY()).thenReturn(4);

        when(moveEffect.getMoving().getDefinition().getSymbol()).thenReturn("K");
        when(moveEffect.getFrom()).thenReturn(square1);
        when(moveEffect.getTrigger()).thenReturn(square2);

        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);
        moveHistoryController = new MoveHistoryController(columnNames);
    }

    @Test
    public void getMoves() {
        assert moveHistoryController.getMoves().isEmpty();
    }

    @Test
    public void testAddMoveToTable() {

        assertTrue(moveHistoryController.getMoves().isEmpty());

        moveHistoryController.addMove(moveEffect, true, true);

        assertTrue(!moveHistoryController.getMoves().isEmpty());

    }

}