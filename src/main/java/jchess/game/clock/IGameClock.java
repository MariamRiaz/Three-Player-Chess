package jchess.game.clock;

public interface IGameClock extends Runnable {


    AbstractGameClockView getGameClockView();

    void start();

    void switchPlayers(boolean forward);

    void setTimes(int time);

    void setPlayerNames(String playerOne, String playerTwo, String playerThree);
}
