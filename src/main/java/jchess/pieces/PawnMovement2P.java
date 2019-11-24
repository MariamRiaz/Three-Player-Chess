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

import java.util.Arrays;
import java.util.HashSet;

import jchess.pieces.IMovement.Move.MoveType;

/**
 * Class to represent a pawn piece Pawn can move only forvard and can beat only
 * across In first move pawn can move 2 sqares pawn can be upgreade to rook,
 * knight, bishop, Queen if it's in the squers nearest the side where opponent
 * is lockated Firat move of pawn: |_|_|_|_|_|_|_|_|7 |_|_|_|_|_|_|_|_|6
 * |_|_|_|X|_|_|_|_|5 |_|_|_|X|_|_|_|_|4 |_|_|_|P|_|_|_|_|3 |_|_|_|_|_|_|_|_|2
 * |_|_|_|_|_|_|_|_|1 |_|_|_|_|_|_|_|_|0 0 1 2 3 4 5 6 7
 *
 * Move of a pawn: |_|_|_|_|_|_|_|_|7 |_|_|_|_|_|_|_|_|6 |_|_|_|_|_|_|_|_|5
 * |_|_|_|X|_|_|_|_|4 |_|_|_|P|_|_|_|_|3 |_|_|_|_|_|_|_|_|2 |_|_|_|_|_|_|_|_|1
 * |_|_|_|_|_|_|_|_|0 0 1 2 3 4 5 6 7 Beats with can take pawn:
 * |_|_|_|_|_|_|_|_|7 |_|_|_|_|_|_|_|_|6 |_|_|_|_|_|_|_|_|5 |_|_|X|_|X|_|_|_|4
 * |_|_|_|P|_|_|_|_|3 |_|_|_|_|_|_|_|_|2 |_|_|_|_|_|_|_|_|1 |_|_|_|_|_|_|_|_|0 0
 * 1 2 3 4 5 6 7
 */
public class PawnMovement2P implements IMovement {
	public final boolean facingUp; 
	
	public PawnMovement2P(boolean facingUp) {
		this.facingUp = facingUp;
	}
	
	@Override
	public HashSet<Move> getMoves() {
		int y;
		if (facingUp)
			y = -1;
		else y = 1;
		
		return new HashSet<Move>(Arrays.asList(new Move(0, y, 1, MoveType.OnlyMove), new Move(0, y, 2, MoveType.OnlyMove, MoveType.OnlyWhenFresh),
					new Move(1, y, 1, MoveType.OnlyAttack), new Move(-1, y, 1, MoveType.OnlyAttack)));
	}
}
