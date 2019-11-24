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
package jchess.pieces;

import java.util.HashSet;

import jchess.Player;
import jchess.UI.board.Square;

/**
 * Class to represent a piece of any kind.
 */
public class Piece {
	private static int nextID = 0;
	
	private Square square = null;
	private boolean hasMoved = false;
	
	public final int ID, value;
	public final Player player;
	public final String type, symbol;
	public final IMovement movement;
	
	/**
	 * @param player Must be non-null.
	 * @param movement Must be non-null.
	 * @param type Must be non-null.
	 */
	public Piece(IMovement movement, String type, Player player, int value, String symbol) {
		this.ID = nextID++;
		this.value = value;
		if (player == null)
			throw new NullPointerException("Argument 'player' is null.");
		this.player = player;
		if (movement == null)
			throw new NullPointerException("Argument 'movement' is null.");
		this.movement = movement;
		if (type == null)
			throw new NullPointerException("Argument 'type' is null.");
		this.symbol = symbol;
		if (symbol == null)
			throw new NullPointerException("Argument 'symbol' is null.");
		this.type = type;
	}
	
	public Square getSquare() {
		return square;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public Piece refresh() {
		hasMoved = false;
		return this;
	}
	
	public Piece setSquare(Square square) {
		if (square != null && this.square != null && this.square != square)
			hasMoved = true;
		this.square = square;
		return this;
	}
	
	/**
	 * @return List of all available moves for this Piece.
	 */
	public HashSet<IMovement.Move> getMoves() {
		return movement.getMoves();
	}
}
