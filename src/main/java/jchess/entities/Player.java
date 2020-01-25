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
package jchess.entities;

import java.io.Serializable;

/**
 * Class representing the player in the game
 */
public class Player implements Serializable {
    public static String colorToLetter(Player.colors color) {
    	switch (color) {
    	case black:
    		return "B";
    	case white:
    		return "W";
    	case gray:
    		return "G";
    	}
    	
		return "";
    }

	public String name;

	public enum colors {

		white, black, gray
	}

	public colors color;

	public boolean goDown;

	public Player() {
	}

	public Player(String name, String color) {
		this.name = name;
		this.color = colors.valueOf(color);
		this.goDown = false;
	}

	/**
	 * Method setting the players name
	 * 
	 * @param name name of player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method getting the players name
	 * 
	 * @return name of player
	 */
	public String getName() {
		return this.name;
	}

}
