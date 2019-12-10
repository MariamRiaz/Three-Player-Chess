package jchessTest;

import jchess.Game;
import jchess.UI.GameClock;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameClockTest {
    Game game = new Game();
    GameClock gameClock = new GameClock(game);
    //Thread thread;

    @Test
    public void start() {
    }

    @Test (expected = java.lang.IllegalMonitorStateException.class)
    public void stop() {
        gameClock.stop();
    }

    @Test
    public void switch_clocks() {
    }

    @Test
    public void setTimes() {
       gameClock.setTimes(5);
    }

    @Test
    public void run() {
    }
}