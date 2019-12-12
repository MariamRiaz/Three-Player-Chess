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
import jchess.Game;
import jchess.helper.Log;
import jchess.entities.Player;
import jchess.Settings;
import jchess.helper.Clock;
import jchess.view.GameClockView;

/**
 * Class to represent the full game clock logic interacts with game clock view to generate the clocks on the game window
 */
public class GameClock implements Runnable {

	private Clock runningClock;
	private Settings settings;
	private Thread thread;
	private Game game;
	public GameClockView gameClockView;
	/**
	 * @param game The current game
	 */
	public GameClock(Game game) {
		super();
		gameClockView = new GameClockView(game);
		gameClockView.clock1 = new Clock();// white player clock
		gameClockView.clock2 = new Clock();// black player clock
		gameClockView.clock3 = new Clock();// gray player clock
		this.runningClock = gameClockView.clock1;// running/active clock
		this.game = game;
		this.settings = game.getSettings();

		int time = this.settings.getTimeForGame();

		this.setTimes(time);
		this.setPlayers(this.settings.getPlayerBlack(), this.settings.getPlayerWhite(), this.settings.getPlayerGray());

		this.thread = new Thread(this);
		if (this.settings.timeLimitSet) {
			thread.start();
		}
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
	public void stop() {
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

	/**
	 * Method of switching the players clocks
	 */
	public void switch_clocks() {

		if (this.runningClock == gameClockView.clock1) {
			this.runningClock = gameClockView.clock2;
		} else if (this.runningClock == gameClockView.clock2) {
			this.runningClock = gameClockView.clock3;
		}
		else {
			this.runningClock = gameClockView.clock1;
		}
	}

	public void setTimes(int time) {
		/*
		 * rather in chess game players got the same time 4 game, so why in
		 * documentation this method've 2 parameters ?
		 */
		gameClockView.clock1.init(time);
		gameClockView.clock2.init(time);
		gameClockView.clock3.init(time);
	}

	/**
	 * Method with is setting the players clocks
	 * 
	 * @param p1 Capt player information
	 * @param p2 Capt player information
	 */
	private void setPlayers(Player p1, Player p2, Player p3) {
		/*
		 * in documentation it's called 'setPlayer' but when we've 'setTimes' better to
		 * use one convention of naming methods - this've to be repaired in
		 * documentation by Wąsu:P dojdziemy do tego:D:D:D
		 */
		if (p1.color == p1.color.white) {
			if (p2.color == p2.color.black) {
			gameClockView.clock1.setPlayer(p1);
			gameClockView.clock2.setPlayer(p2);
			gameClockView.clock3.setPlayer(p3);
			}
			else if (p2.color == p2.color.gray) {
				gameClockView.clock1.setPlayer(p1);
				gameClockView.clock2.setPlayer(p3);
				gameClockView.clock3.setPlayer(p2);
			}}

		if (p1.color == p1.color.black) {
			if (p2.color == p2.color.white) {
				gameClockView.clock1.setPlayer(p2);
				gameClockView.clock2.setPlayer(p1);
				gameClockView.clock3.setPlayer(p3);
			}
			else if (p2.color == p2.color.gray){
				gameClockView.clock1.setPlayer(p3);
				gameClockView.clock2.setPlayer(p1);
				gameClockView.clock3.setPlayer(p2);
			}}

		if (p1.color == p1.color.gray) {
			if (p2.color == p2.color.white) {
				gameClockView.clock1.setPlayer(p2);
				gameClockView.clock2.setPlayer(p3);
				gameClockView.clock3.setPlayer(p1);
			}
			else if (p2.color == p2.color.black){
				gameClockView.clock1.setPlayer(p3);
				gameClockView.clock2.setPlayer(p2);
				gameClockView.clock3.setPlayer(p1);
			}}
	}

	/**
	 * Method with is running the time on clock
	 */
	public void run() {
		while (true) {
			if (this.runningClock != null) {
				if (this.runningClock.decrement()) {
					gameClockView.repaint();
					try {
						thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.log(Level.SEVERE, "Some error in gameClock thread: " + e);
					}
					// if(this.game.blockedChessboard)
					// this.game.blockedChessboard = false;
				}
				if (this.runningClock != null && this.runningClock.get_left_time() == 0) {
					this.timeOver();
				}
			}
		}
	}

	/**
	 * Method of checking is the time of the game is not over
	 */
	private void timeOver() {
		String color = new String();
		if (gameClockView.clock1.get_left_time() == 0) {// Check which player win
			color = gameClockView.clock2.getPlayer().color.toString();
		} else if (gameClockView.clock2.get_left_time() == 0) {
			color = gameClockView.clock1.getPlayer().color.toString();
		} else {// if called in wrong moment
			Log.log("Time over called when player got time 2 play");
		}
		this.game.endGame("Time is over! " + color + " player win the game.");
		this.stop();

		// JOptionPane.showMessageDialog(this, "koniec czasu");
	}
}
