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

/**
 * Class to represent a chess pawn rook Rook can move: |_|_|_|X|_|_|_|_|7
 * |_|_|_|X|_|_|_|_|6 |_|_|_|X|_|_|_|_|5 |_|_|_|X|_|_|_|_|4 |X|X|X|B|X|X|X|X|3
 * |_|_|_|X|_|_|_|_|2 |_|_|_|X|_|_|_|_|1 |_|_|_|X|_|_|_|_|0 0 1 2 3 4 5 6 7
 *
 */
public class RookMovement2P implements IMovement {
	@Override
	public HashSet<Move> getMoves() {
		return new HashSet<Move>(Arrays.asList(new Move(1, 0, null), new Move(-1, 0, null), new Move(0, 1, null), new Move(0, -1, null)));
	}
}
