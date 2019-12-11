package jchessTest;

import jchess.Game;
import jchess.Settings;
import jchess.controller.GameClock;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import static org.mockito.Mockito.mock;

public class GameClockTest {
    Game game;
    GameClock gameClock;

    @Before
    public void setup() {
        Game game = mock (Game.class);
        game.settings = new Settings();
        gameClock = new GameClock(game);
    }

    @Test
    public void setTimes() {
        gameClock.setTimes(10);
        Assert.assertEquals(10, gameClock.gameClockView.clock1.get_left_time());
        Assert.assertEquals(10, gameClock.gameClockView.clock2.get_left_time());
        Assert.assertEquals(10, gameClock.gameClockView.clock3.get_left_time());
    }

}