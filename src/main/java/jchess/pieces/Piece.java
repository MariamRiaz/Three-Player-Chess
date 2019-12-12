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

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashSet;

import jchess.entities.Player;

/**
 * Class to represent a Piece of any kind. Each Piece is defined by specific values for its member attributes.
 */
public class Piece {
	/**
	 * Class representing a potential Move of a Piece. It describes the direction, limit and conditions for this Move.
	 * @author Stefan
	 */
	public static class Move {
		/**
		 * Enum describing constraints, conditions, and move types to be observed when evaluating this Move.
		 * @author Stefan
		 */
		public enum MoveType {
			OnlyAttack,
			OnlyMove,
			Unblockable,
			OnlyWhenFresh,
			Castling, //TODO: Add relevant Move instance to King implementation and code in Chessboard.recurseMove
			EnPassant //TODO: Add relevant Move instance to Pawn implementation and code in Chessboard.recurseMove
		}
		
		public final int x, y;
		public final Integer limit;
		public final HashSet<MoveType> conditions;
		
		protected Move(int x, int y, Integer limit, MoveType... conditions) {
			this.x = x;
			this.y = y;
			this.limit = limit;
			this.conditions = new HashSet<MoveType>(Arrays.asList(conditions));
			
			if (this.conditions.contains(MoveType.OnlyAttack) && this.conditions.contains(MoveType.OnlyMove))
				throw new InvalidParameterException("Move conditions cannot include 'OnlyAttack' and 'OnlyMove' simultaneously.");
		}
	}
	
	private boolean hasMoved = false;
	
	public final Player player;
	public final int value;
	public final String type, symbol;
	public final HashSet<Move> moves;
	
	/**
	 * Creates a new Piece based on the given parameters. Piece attributes cannot be changed after initialization.
	 * @param player Must be non-null.
	 * @param type Must be non-null. The type of this Piece, e.g. Pawn, King etc.
	 * @param value The value in points of this Piece.
	 * @param symbol Must be non-null. The shorthand symbol of this Piece, e.g. N, K, Q etc.
	 * @param moves The Moves for this Piece. Each Move must be non-null.
	 */
	public Piece(Player player, String type, int value, String symbol, Move... moves) {
		this.value = value;
		
		this.moves = new HashSet<Move>(Arrays.asList(moves));
		
		if (player == null)
			throw new NullPointerException("Argument 'player' is null.");
		this.player = player;
		
		if (type == null)
			throw new NullPointerException("Argument 'type' is null.");
		this.symbol = symbol;
		
		if (symbol == null)
			throw new NullPointerException("Argument 'symbol' is null.");
		this.type = type;
	}
	
	/**
	 * Creates a new Piece with the same attributes as those of the Piece other.
	 * @param other Piece whose attributes to copy. Must be non-null.
	 */
	public Piece(Piece other) {
		if (other == null)
			throw new NullPointerException("Argument 'other' is null.");
		
		this.value = other.value;
		
		this.moves = new HashSet<Move>(other.moves);
		this.player = other.player;
		this.symbol = new String(other.symbol);
		this.type = new String(other.type);
	}
	
	/**
	 * Returns a copy of this Piece.
	 */
	public Piece clone() {
		return new Piece(this);
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
	 * @return List of all available Moves for this Piece.
	 */
	public HashSet<Move> getMoves() {
		return moves;
	}
}
