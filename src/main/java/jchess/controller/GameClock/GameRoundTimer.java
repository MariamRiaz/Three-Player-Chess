package jchess.controller.GameClock;

public class GameRoundTimer {
    private int timeSpentInRound;

    public GameRoundTimer(){
        this.timeSpentInRound = 0;
    }

    public boolean increment() {
            this.timeSpentInRound = this.timeSpentInRound + 1;
            return true;
    }

    public void resetTimer(){
        this.timeSpentInRound = 0;
    }


}
