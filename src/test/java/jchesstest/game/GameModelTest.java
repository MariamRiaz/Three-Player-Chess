package jchessTest.game;

import jchess.game.GameModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameModelTest {

    GameModel sut;

    @Before
    public void setup() {
        sut = new GameModel();
    }

    @Test
    public void testInit() {
        Assert.assertNotNull(sut);
        Assert.assertNotNull(sut.getPlayerBlack());
        Assert.assertNotNull(sut.getPlayerGray());
        Assert.assertNotNull(sut.getPlayerWhite());
        Assert.assertFalse(sut.getTimeLimitSet());
    }


}
