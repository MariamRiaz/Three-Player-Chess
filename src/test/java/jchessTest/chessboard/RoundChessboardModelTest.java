package jchessTest.chessboard;

import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.player.Player;
import jchess.pieces.Piece;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundChessboardModelTest {

    public RoundChessboardModel sut;

    @Before
    public void setup() {
        sut = new RoundChessboardModel(2, 3, false, false);
    }

    @Test
    public void testInit() {
        assertEquals(sut.getRows(), 2);
        assertEquals(sut.getColumns(), 3);
        assertFalse(sut.getInnerRimConnected());
        assertEquals(sut.getSquares().size(), 2 * 3);
    }

    @Test
    public void testGetSquare() {
        assertNotNull(sut.getSquare(0,0));
        assertNotNull(sut.getSquare(1,0));
        assertNotNull(sut.getSquare(2,0));
        assertNotNull(sut.getSquare(0,1));
        assertNotNull(sut.getSquare(1,1));
        assertNotNull(sut.getSquare(2,1));

    }

    @Test
    public void testAddCrucialPiece() {
        // Arrange
        Piece piece = mock(Piece.class);
        // Act
        sut.addCrucialPiece(piece);
        // Assert
        assertTrue(sut.getCrucialPieces().contains(piece));

    }
    @Test
    public void testGetSquareWithPiece() {
        // Arrange
        Piece piece = mock(Piece.class);
        Square square = sut.getSquare(0, 0);
        // Act
        sut.setPieceOnSquare(piece, square);
        // Assert
        assertEquals(sut.getSquare(piece), square);
    }

    @Test
    public void testGetSquareWithId() {
        // Arrange
        Piece piece = mock(Piece.class);
        when(piece.getID()).thenReturn(0);
        Square square = sut.getSquare(0, 0);
        // Act
        sut.setPieceOnSquare(piece, square);
        // Assert
        assertEquals(sut.getSquare(0), square);
    }

    @Test
    public void getSquaresBetween() {
        // Arrange
        Square firstSquare = sut.getSquare(0,0);
        Square secondSquare = sut.getSquare(2,0);
        // Act
        HashSet<Square> squares = sut.getSquaresBetween(firstSquare, secondSquare);
        Square betweenSquareLast = (Square) Arrays.asList(squares.toArray()).get(0);
        Square betweenSquareSecond = (Square) Arrays.asList(squares.toArray()).get(1);
        Square betweenSquareFirst = (Square) Arrays.asList(squares.toArray()).get(2);
        // Assert
        assertEquals(squares.size(), 3);
        assertEquals(betweenSquareLast, secondSquare);
        assertEquals(betweenSquareSecond, sut.getSquare(1,0));
        assertEquals(betweenSquareFirst, firstSquare);
    }

    @Test
    public void getCrucaialPiecesGivenPlayers() {
        // Arrange
        Player one = mock(Player.class);
        Player two = mock(Player.class);
        Piece firstPiece = mock(Piece.class);
        Piece secondPiece = mock(Piece.class);
        sut.addCrucialPiece(firstPiece);
        sut.addCrucialPiece(secondPiece);
        when(firstPiece.getPlayer()).thenReturn(one);
        when(secondPiece.getPlayer()).thenReturn(two);
        // Act
        HashSet<Piece> playerOneCrucialPieces = sut.getCrucialPieces(one);
        HashSet<Piece> playerTwoCrucialPieces = sut.getCrucialPieces(two);
        // Assert
        assertTrue(playerOneCrucialPieces.contains(firstPiece));
        assertFalse(playerOneCrucialPieces.contains(secondPiece));
        assertFalse(playerTwoCrucialPieces.contains(firstPiece));
        assertTrue(playerTwoCrucialPieces.contains(secondPiece));
    }

}