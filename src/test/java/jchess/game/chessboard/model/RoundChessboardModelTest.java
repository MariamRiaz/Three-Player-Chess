package jchess.game.chessboard.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoundChessboardModelTest {
    RoundChessboardModel roundChessboardModel;
    @Before
    public void setUp() throws Exception {
        roundChessboardModel = new RoundChessboardModel(5, 20, false, false);

    }

    @Test
    public void getRows() {
        assert roundChessboardModel.getRows() == 5;

    }

    @Test
    public void getColumns() {
    }

    @Test
    public void getCrucialPieces() {
    }

    @Test
    public void getSquare() {
    }

    @Test
    public void isInPromotionArea() {
    }

    @Test
    public void getInnerRimConnected() {
    }

    @Test
    public void testGetSquare() {
    }

    @Test
    public void testGetSquare1() {
    }

    @Test
    public void getSquaresBetween() {
    }

    @Test
    public void setPieceOnSquare() {
    }

    @Test
    public void getSquares() {
    }
}