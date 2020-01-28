/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.game.clock;

import jchess.game.IGameController;
import jchess.game.IGameModel;
import jchess.logging.Log;

import java.util.logging.Level;

/**
 * Class to represent the full game clock logic interacts with game clock view to generate the clocks on the game window
 */
public class GameClock implements IGameClock {

    private Thread thread;
    private IGameController gameController;
    private AbstractGameClockView gameClockView;
    private int[] timeSpentByPlayers;

    private enum PlayerColors {WHITE, BLACK, GRAY}

    private PlayerColors activePlayer;

    /**
     * @param gameController The current gameController
     */
    public GameClock(IGameController gameController) {
        super();
        gameClockView = new GameClockView();
        int time = gameController.getGameModel().getTimeForGame();
        activePlayer = PlayerColors.WHITE;
        this.setTimes(time);
        this.thread = new Thread(this);
        if (gameController.getGameModel().getTimeLimitSet()) {
            thread.start();
        }
    }

    public void setPlayerNames(String playerOne, String playerTwo, String playerThree) {
        gameClockView.setPlayerNames(playerOne, playerTwo, playerThree);
    }

    public AbstractGameClockView getGameClockView() {
        return gameClockView;
    }

    /**
     * Method to init game clock
     */
    public void start() {
        this.thread.start();
    }

    /**
     * Method to stop game clock
     */
    private void stop() {

        try {// block this thread
            this.thread.wait();
        } catch (java.lang.InterruptedException exc) {
            Log.log(Level.SEVERE, "Error blocking thread: " + exc);
        } catch (java.lang.IllegalMonitorStateException exc1) {
            Log.log(Level.SEVERE, "Error blocking thread: " + exc1);
            throw exc1;
        }
    }

    public void switchPlayers(boolean forward) {
        if (forward) {
            if (this.activePlayer == PlayerColors.WHITE) {
                this.activePlayer = PlayerColors.BLACK;
            } else if (this.activePlayer == PlayerColors.BLACK) {
                this.activePlayer = PlayerColors.GRAY;
            } else {
                this.activePlayer = PlayerColors.WHITE;

            }
        } else {
            if (this.activePlayer == PlayerColors.WHITE) {
                this.activePlayer = PlayerColors.GRAY;
            } else if (this.activePlayer == PlayerColors.BLACK) {
                this.activePlayer = PlayerColors.WHITE;
            } else {
                this.activePlayer = PlayerColors.BLACK;
            }
        }
    }

    public void setTimes(int time) {
        timeSpentByPlayers = new int[]{time, time, time};
    }

    /**
     * Method with is running the time on clock
     */
    public void run() {
        while (true) {
            if ((this.timeSpentByPlayers[0] > 0) && (this.timeSpentByPlayers[1] > 0) && (this.timeSpentByPlayers[2] > 0)) {
                if (this.activePlayer == PlayerColors.WHITE) {
                    this.timeSpentByPlayers[0] = this.timeSpentByPlayers[0] - 1;
                }
                if (this.activePlayer == PlayerColors.BLACK) {
                    this.timeSpentByPlayers[1] = this.timeSpentByPlayers[1] - 1;
                }
                if (this.activePlayer == PlayerColors.GRAY) {
                    this.timeSpentByPlayers[2] = this.timeSpentByPlayers[2] - 1;
                }
                gameClockView.updateClocks(this.timeSpentByPlayers);
                try {
                    thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.log(Level.SEVERE, "Some error in gameClock thread: " + e);
                }
            }
            if ((this.timeSpentByPlayers[0] == 0) ||
                    (this.timeSpentByPlayers[1] == 0) ||
                    (this.timeSpentByPlayers[2] == 0)) {
                this.timeOver();
            }
        }
    }


    /**
     * Method of checking is the time of the game is not over
     */
    private void timeOver() {
        String color = new String();
        if (this.timeSpentByPlayers[0] == 0) {// Check which player win
            color = PlayerColors.BLACK.toString();
        } else if (this.timeSpentByPlayers[1] == 0) {
            color = PlayerColors.WHITE.toString();
        } else {// if called in wrong moment
            Log.log("Time over called when player got time 2 play");
        }
        this.gameController.endGame("Time is over! " + color + " player wins the gameController.");
        this.stop();
        // JOptionPane.showMessageDialog(this, "koniec czasu");
    }
}
