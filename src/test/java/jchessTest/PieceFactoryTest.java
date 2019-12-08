package jchessTest;

import static org.junit.Assert.*;

import jchess.pieces.PieceFactory;
import org.junit.Test;

import jchess.Player;

public class PieceFactoryTest {
    @Test
    public void testRookCreation() {
        assertTrue(PieceFactory.createRook(new Player()) != null);
    }

    @Test
    public void testKingCreation() {
        assertTrue(PieceFactory.createKing(new Player()) != null);
    }

    @Test
    public void testKnightCreation() {
        assertTrue(PieceFactory.createKnight(new Player()) != null);
    }

    @Test
    public void testQueenCreation() {
        assertTrue(PieceFactory.createQueen(new Player()) != null);
    }

    @Test
    public void testBishopCreation() {
        assertTrue(PieceFactory.createBishop(new Player()) != null);
    }

    @Test
    public void testPawnCreation() {
        assertTrue(PieceFactory.createPawn(new Player(), false) != null);
    }
}
