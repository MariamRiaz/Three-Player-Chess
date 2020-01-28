package jchesstest;

import jchess.game.GameModel;
import jchess.game.IGameModel;
import jchess.game.chessboard.model.Square;
import jchess.game.history.MoveHistoryController;
import jchess.game.history.MoveHistoryEntry;
import jchess.game.history.MoveHistoryModel;
import jchess.game.history.MoveHistoryView;
import jchess.game.player.Player;
import jchess.move.MoveDefinition;
import jchess.move.MoveType;
import jchess.move.effects.BoardTransition;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoveHistoryTest {
    private MoveHistoryController moveHistoryController;
    private MoveHistoryView moveHistoryViewMock;
    private ArrayList<Character> columnNames = new ArrayList<>();
    private BoardTransition moveEffectMock;


    @Before
    public void setup() {
        IGameModel settingsMock = mock(GameModel.class);
        Piece pieceMock = mock(Piece.class);
        moveEffectMock = mock(BoardTransition.class);
        Square square1 = mock(Square.class);
        Square square2 = mock(Square.class);
        PieceDefinition pieceDefinitionMock = mock(PieceDefinition.class);
        MoveDefinition moveMock = mock(MoveDefinition.class);
        MoveType moveTypeMock = MoveType.OnlyMove;
        moveHistoryViewMock = mock(MoveHistoryView.class);
        MoveHistoryEntry entryMock = mock(MoveHistoryEntry.class);

        Player testPlayer = new Player("test", "white");
        columnNames.add('a');
        columnNames.add('b');
        columnNames.add('c');

        when(square1.getPozX()).thenReturn(1);
        when(square1.getPozY()).thenReturn(2);

        when(square2.getPozX()).thenReturn(2);
        when(square2.getPozY()).thenReturn(2);
        
        when(entryMock.getPiece()).thenReturn(pieceMock);
        when(pieceMock.getDefinition()).thenReturn(pieceDefinitionMock);
        when(entryMock.getMove()).thenReturn(moveMock);

        when(moveHistoryViewMock.getTable()).thenReturn(new JTable());
        
        when(moveEffectMock.getMoveHistoryEntry()).thenReturn(entryMock);
        when(moveEffectMock.getMoveHistoryEntry().getPiece().getDefinition()).thenReturn(pieceDefinitionMock);
        when(moveEffectMock.getMoveHistoryEntry().getPiece().getDefinition().getSymbol()).thenReturn("K");
        when(moveEffectMock.getMoveHistoryEntry().getMove()).thenReturn(moveMock);
        when(moveEffectMock.getMoveHistoryEntry().getPriorityMoveType()).thenReturn(moveTypeMock);
        when(moveEffectMock.getMoveHistoryEntry().getMove().getFormatString(moveEffectMock.getMoveHistoryEntry().getPriorityMoveType())).thenReturn("move");
        when(moveEffectMock.getMoveHistoryEntry().getFromSquare()).thenReturn(square1);
        when(moveEffectMock.getMoveHistoryEntry().getToSquare()).thenReturn(square2);

        when(settingsMock.getPlayerWhite()).thenReturn(testPlayer);

        moveHistoryController = new MoveHistoryController(columnNames);
    }

    @Test
    public void testGetMoves() {

        assertTrue(moveHistoryController.getMoves().isEmpty());

        moveHistoryController.getMoves().add("test");

        assertEquals("test", moveHistoryController.getMoves().get(0));

    }

    @Test
    public void testAddMoveToTable() {

        assertTrue(moveHistoryController.getMoves().isEmpty());

        moveHistoryController.addMove(moveEffectMock);

        assertEquals(1, moveHistoryController.getMoves().size());
        assertEquals(1, moveHistoryController.getMoveHistoryModel().getRowCount());
        assertEquals("move", moveHistoryController.getMoves().get(0));
    }

    @Test
    public void testClearMoveForwardStack() {

        MoveHistoryModel moveHistoryModelMock = new MoveHistoryModel();

        moveHistoryController.setMoveHistoryModel(moveHistoryModelMock);
        assertTrue(moveHistoryController.getMoveHistoryModel().getMoveForwardStack().empty());

        moveHistoryModelMock.getMoveForwardStack().push(moveEffectMock);
        assertFalse(moveHistoryController.getMoveHistoryModel().getMoveForwardStack().empty());

        moveHistoryController.clearMoveForwardStack();
        assertTrue(moveHistoryController.getMoveHistoryModel().getMoveForwardStack().empty());

    }

    @Test
    public void testUndoOne() {

        //Prepare
        moveHistoryController.setMoveHistoryView(moveHistoryViewMock);

        moveHistoryController.addMove(moveEffectMock);
        moveHistoryController.switchColumns(true);
        moveHistoryController.addMove(moveEffectMock);
        moveHistoryController.switchColumns(true);
        moveHistoryController.addMove(moveEffectMock);
        moveHistoryController.switchColumns(true);
        moveHistoryController.addMove(moveEffectMock);
        moveHistoryController.switchColumns(true);

        assertEquals(2, moveHistoryController.getMoveHistoryModel().getRowCount());
        assertEquals(4, moveHistoryController.getMoveHistoryModel().getMoveBackStack().size());

        //Execute
        moveHistoryController.undoOne();

        //Assert Row count to be one less
        //Assert MoveBackStack to be one less
        assertEquals(1, moveHistoryController.getMoveHistoryModel().getRowCount());
        assertEquals(3, moveHistoryController.getMoveHistoryModel().getMoveBackStack().size());

    }

    @Test
    public void testRedoOne() {

        //Prepare
        moveHistoryController.getMoveHistoryModel().getMoveForwardStack().push(moveEffectMock);
        assertEquals(moveEffectMock, moveHistoryController.getMoveHistoryModel().getMoveForwardStack().firstElement());
        assertTrue(moveHistoryController.getMoveHistoryModel().getMoveBackStack().empty());

        //Execute
        moveHistoryController.redoOne();

        //Assert getMoveForwardStack() to be empty
        //Assert moveEffectMock was added to MoveForwardStack of Model to have been called
        assertTrue(moveHistoryController.getMoveHistoryModel().getMoveForwardStack().empty());
        assertEquals(moveEffectMock, moveHistoryController.getMoveHistoryModel().getMoveBackStack().pop());
    }

    @Test
    public void testSwitchColumns_for_each_player_if_direction_is_forward() {

        EnumSet.allOf(MoveHistoryController.PlayerColumn.class).forEach(playerColumn -> {

            //Prepare
            moveHistoryController.getMoveHistoryModel().setActivePlayerColumn(playerColumn);

            //Execute
            moveHistoryController.switchColumns(true);

            //Assert player switched accordingly
            switch (playerColumn) {
                case player1:
                    assertEquals(MoveHistoryController.PlayerColumn.player2, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
                case player2:
                    assertEquals(MoveHistoryController.PlayerColumn.player3, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
                case player3:
                    assertEquals(MoveHistoryController.PlayerColumn.player1, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
            }
        });
    }

    @Test
    public void testSwitchColumns_for_each_player_if_direction_is__backward() {

        EnumSet.allOf(MoveHistoryController.PlayerColumn.class).forEach(playerColumn -> {

            //Prepare
            moveHistoryController.getMoveHistoryModel().setActivePlayerColumn(playerColumn);

            //Execute
            moveHistoryController.switchColumns(false);

            //Assert player switched accordingly
            switch (playerColumn) {
                case player1:
                    assertEquals(MoveHistoryController.PlayerColumn.player3, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
                case player2:
                    assertEquals(MoveHistoryController.PlayerColumn.player1, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
                case player3:
                    assertEquals(MoveHistoryController.PlayerColumn.player2, moveHistoryController.getMoveHistoryModel().getActivePlayerColumn());
                    break;
            }
        });
    }
}