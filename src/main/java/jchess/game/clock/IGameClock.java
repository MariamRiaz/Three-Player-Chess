package jchess.game.clock;

import jchess.game.clock.AbstractGameClockView;

public interface IGameClock extends Runnable{


    AbstractGameClockView getGameClockView();

    void start();

    void switchPlayers(boolean forward);

    void setTimes(int time);
}
