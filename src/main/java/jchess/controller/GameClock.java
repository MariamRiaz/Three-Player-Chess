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
package jchess.controller;

import java.util.logging.Level;

import jchess.helper.Log;
import jchess.model.GameModel;
import jchess.helper.GameRoundTimer;
import jchess.view.GameClockView;

/**
 * Class to represent the full game clock logic interacts with game clock view to generate the clocks on the game window
 */
public class GameClock implements Runnable {

    private GameModel gameModel;
    private Thread thread;
    private IGameController gameController;
    private GameClockView gameClockView;
    private GameRoundTimer runningClock;
    private int totalPlayerTimeLimit;
    private int timeSpentByPlayers[];
    private enum PlayerColors {WHITE, BLACK, GRAY}
    private PlayerColors activePlayer;

    /**
     * @param gameController The current gameController
     */
    public GameClock(IGameController gameController) {
        super();
        gameClockView = new GameClockView(gameController);
        this.runningClock = new GameRoundTimer();
        this.gameController = gameController;
        this.gameModel = gameController.getGameModel();
        int time = this.gameModel.getTimeForGame();
        activePlayer = PlayerColors.WHITE;
        this.setTimes(time);
		this.thread = new Thread(this);
		if (this.gameModel.timeLimitSet) {
			thread.start();
		}
	}

	public GameClockView getGameClockView() {
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
		this.runningClock = null;

		try {// block this thread
			this.thread.wait();
		} catch (java.lang.InterruptedException exc) {
			Log.log(Level.SEVERE, "Error blocking thread: " + exc);
		} catch (java.lang.IllegalMonitorStateException exc1) {
			Log.log(Level.SEVERE, "Error blocking thread: " + exc1);
			throw exc1;
		}
	}

	void switchPlayers(boolean forward) {
		if (forward) {
			if (this.activePlayer == PlayerColors.WHITE) {
				this.activePlayer = PlayerColors.BLACK;
				this.runningClock.resetTimer();
			} else if (this.activePlayer == PlayerColors.BLACK) {
				this.activePlayer = PlayerColors.GRAY;
				this.runningClock.resetTimer();
			} else {
				this.activePlayer = PlayerColors.WHITE;
				this.runningClock.resetTimer();

			}
		} else {
			if (this.activePlayer == PlayerColors.WHITE) {
				this.activePlayer = PlayerColors.GRAY;
				this.runningClock.resetTimer();
			} else if (this.activePlayer == PlayerColors.BLACK) {
				this.activePlayer = PlayerColors.WHITE;
				this.runningClock.resetTimer();
			} else {
				this.activePlayer = PlayerColors.BLACK;
				this.runningClock.resetTimer();
			}
		}
	}

	public void setTimes(int time) {
		totalPlayerTimeLimit = time;
		timeSpentByPlayers = new int[]{totalPlayerTimeLimit, totalPlayerTimeLimit, totalPlayerTimeLimit};
	}

	/**
	 * Method with is running the time on clock
	 */
	public void run() {
		while (true) {
			if (this.runningClock != null) {
				this.runningClock.increment();
				if((this.timeSpentByPlayers[0] > 0) && (this.timeSpentByPlayers[1] > 0) && (this.timeSpentByPlayers[2] > 0)){
				if(this.activePlayer == PlayerColors.WHITE){
					this.timeSpentByPlayers[0] = this.timeSpentByPlayers[0]  - 1;
				}
				if(this.activePlayer == PlayerColors.BLACK){
					this.timeSpentByPlayers[1]  = this.timeSpentByPlayers[1] - 1;
				}
				if(this.activePlayer == PlayerColors.GRAY){
					this.timeSpentByPlayers[2] = this.timeSpentByPlayers[2] - 1;
				}
				gameClockView.updateClocks(this.timeSpentByPlayers);
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.log(Level.SEVERE, "Some error in gameClock thread: " + e);
				}
				}
				if (this.runningClock != null && ((this.timeSpentByPlayers[0]  == 0) ||
						(this.timeSpentByPlayers[1]  == 0) ||
						(this.timeSpentByPlayers[2]  == 0) )) {
					this.timeOver();
				}
			}
		}
	}

	/**
	 * Method of checking is the time of the game is not over
	 */
	private void  timeOver() {
		String color = new String();
		if (this.timeSpentByPlayers[0]  == 0) {// Check which player win
			color = PlayerColors.BLACK.toString();
		} else if (this.timeSpentByPlayers[1]  == 0) {
			color = PlayerColors.WHITE.toString();
		} else {// if called in wrong moment
			Log.log("Time over called when player got time 2 play");
		}
		this.gameController.endGame("Time is over! " + color + " player wins the gameController.");
		this.stop();
		// JOptionPane.showMessageDialog(this, "koniec czasu");
	}
}
