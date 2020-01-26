package jchess.controller;

import jchess.view.AbstractGameClockView;

public interface IGameClock extends Runnable{


    AbstractGameClockView getGameClockView();

    void start();

    void switchPlayers(boolean forward);

    void setTimes(int time);
}
