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
 * A model class for getting the current place and the new place of a piece once its moved
 * also will have the record for the pieces that are moved, taken or promoted
 */
package jchess.pieces;

import jchess.UI.board.Square;
import jchess.controller.RoundChessboardController;
import jchess.controller.MoveHistory.castling;

public class PlayedMove {

	protected Square from = null;
	protected Square to = null;
	protected Piece movedPiece = null, movedPieceState = null;
	protected Piece takenPiece = null;
	protected Piece promotedTo = null;
	protected boolean wasEnPassant = false;
	protected castling castlingMove = castling.none;
	protected boolean wasPawnTwoFieldsMove = false;

	public PlayedMove(Square from, Square to, Piece movedPiece, Piece movedPieceState, Piece takenPiece, castling castlingMove, boolean wasEnPassant,
					  Piece promotedPiece) {
		this.from = from;
		this.to = to;

		this.movedPiece = movedPiece;
		this.takenPiece = takenPiece;
		this.movedPieceState = movedPieceState;

		this.castlingMove = castlingMove;
		this.wasEnPassant = wasEnPassant;

		if (movedPiece.type.equals("Pawn") && Math.abs(to.getPozY() - from.getPozY()) == 2) {
			this.wasPawnTwoFieldsMove = true;
		} else if (movedPiece.type.equals("Pawn") && to.getPozY() == RoundChessboardController.bottom
				|| to.getPozY() == RoundChessboardController.top && promotedPiece != null) {
			this.promotedTo = promotedPiece;
		}
	}

	public Square getFrom() {
		return this.from;
	}

	public Square getTo() {
		return this.to;
	}

	public Piece getMovedPiece() {
		return this.movedPiece;
	}
	
	public Piece getMovedPieceState() {
		return this.movedPieceState;
	}

	public Piece getTakenPiece() {
		return this.takenPiece;
	}

	public boolean wasEnPassant() {
		return this.wasEnPassant;
	}

	public boolean wasPawnTwoFieldsMove() {
		return this.wasPawnTwoFieldsMove;
	}

	public castling getCastlingMove() {
		return this.castlingMove;
	}

	public Piece getPromotedPiece() {
		return this.promotedTo;
	}
}
