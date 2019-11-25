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

import jchess.Player;

/**
 * Class to represent a piece of any kind.
 */
public class Piece {
	public static class Move {
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
	 * @param player Must be non-null.
	 * @param movement Must be non-null.
	 * @param type Must be non-null.
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
	
	public Piece setHasMoved(boolean val) {
		hasMoved = val;
		return this;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	/**
	 * @return List of all available moves for this Piece.
	 */
	public HashSet<Move> getMoves() {
		return moves;
	}
}
