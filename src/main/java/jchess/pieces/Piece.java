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

import jchess.entities.Player;
import jchess.move.Orientation;

/**
 * Class to represent a Piece of any kind. Each Piece is defined by specific values for its member attributes.
 */
public class Piece {
	private static int idIncrement = 0;
	
	private boolean hasMoved = false;
	private Orientation orientation;
	
	private final int id;
	private final Player player;
	private PieceDefinition definition;
	
	/**
	 * Creates a new Piece based on the given parameters. Piece attributes cannot be changed after initialization.
	 * @param player Must be non-null.
	 * @param type Must be non-null. The type of this Piece, e.g. Pawn, King etc.
	 * @param value The value in points of this Piece.
	 * @param symbol Must be non-null. The shorthand symbol of this Piece, e.g. N, K, Q etc.
	 * @param moves The Moves for this Piece. Each Move must be non-null.
	 */
	public Piece(PieceDefinition definition, Player player, Orientation orientation) {
		if (definition == null)
			throw new NullPointerException("Argument 'definition' is null.");
		this.definition = definition;
		
		if (player == null)
			throw new NullPointerException("Argument 'player' is null.");
		this.player = player;
		
		if (orientation == null)
			throw new NullPointerException("Argument 'orientation' is null.");
		this.orientation = orientation;
		
		id = idIncrement++;
	}
	
	/**
	 * Creates a new Piece with the same attributes as those of the Piece other.
	 * @param other Piece whose attributes to copy. Must be non-null.
	 */
	public Piece(Piece other) {
		if (other == null)
			throw new NullPointerException("Argument 'other' is null.");
		
		this.player = other.player;
		this.definition = other.definition;
		this.hasMoved = other.hasMoved;
		this.orientation = other.orientation;
		this.id = other.id;
	}
	
	/**
	 * Returns a deep copy of this Piece. ID of the copy will be the same.
	 */
	public Piece clone() {
		return new Piece(this);
	}
	
	/**
	 * @return The unique ID of this Piece. Clones of this Piece will share ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * @return The Player owning this Piece.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param val Whether the Piece has moved since its creation or not.
	 * @return This Piece.
	 */
	public Piece setHasMoved(boolean val) {
		hasMoved = val;
		return this;
	}
	
	/**
	 * @return Whether this Piece has moved since its creation or not.
	 */
	public boolean hasMoved() {
		return hasMoved;
	}
	
	/**
	 * @return This Piece's orientation on the board.
	 */
	public Orientation getOrientation() {
		return orientation;
	}
	
	/**
	 * Sets this Piece's Orientation to the given non-null Orientation.
	 * @param orientation The new Orientation.
	 * @return This Piece.
	 */
	public Piece setOrientation(Orientation orientation) {
		if (orientation != null)
			this.orientation = orientation;
		return this;
	}
	
	/**
	 * @return The PieceDefinition of this Piece.
	 */
	public PieceDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * @param def This Piece's new PieceDefinition. Cannot be null.
	 * @return This Piece.
	 */
	public Piece setDefinition(PieceDefinition def) {
		if (def == null)
			throw new NullPointerException("Argument 'definition' is null.");
		this.definition = def;
		return this;
	}
}
