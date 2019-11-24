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

/**
 * Class to represent a chess pawn king. King is the most important
 * piece for the game. Loose of king is the and of game.
 * When king is in danger by the opponent then it's a Checked, and when have
 * no other escape then stay on a square "in danger" by the opponent
 * then it's a CheckedMate, and the game is over.
 *
 *       |_|_|_|_|_|_|_|_|7
        |_|_|_|_|_|_|_|_|6
        |_|_|_|_|_|_|_|_|5
        |_|_|X|X|X|_|_|_|4
        |_|_|X|K|X|_|_|_|3
        |_|_|X|X|X|_|_|_|2
        |_|_|_|_|_|_|_|_|1
        |_|_|_|_|_|_|_|_|0
        0 1 2 3 4 5 6 7
 */
import java.util.Arrays;
import java.util.HashSet;

public class KingMovement2P implements IMovement {
	@Override
	public HashSet<Move> getMoves() {
		return new HashSet<Move>(Arrays.asList(new Move(1, 1, 1), new Move(1, -1, 1), new Move(-1, -1, 1), new Move(-1, 1, 1),
				new Move(1, 0, 1), new Move(-1, 0, 1), new Move(0, 1, 1), new Move(0, -1, 1)));
	}
}