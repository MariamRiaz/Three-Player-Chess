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
 * Author: Mateusz Sławomir Lach ( matlak, msl )
 */
package jchess.pieces;

import jchess.UI.board.Square;
import jchess.controller.RoundChessboardController;
import jchess.model.RoundChessboardModel;
import jchess.pieces.MoveHistory.castling;
import jchess.view.RoundChessboardViewInitializer;

public class PlayedMove {

	protected Square from = null;
	protected Square to = null;
	protected Piece movedPiece = null, movedPieceState = null;
	protected Piece takenPiece = null, takenPieceState = null;
	protected Piece promotedTo = null;
	protected boolean wasEnPassant = false;
	protected castling castlingMove = castling.none;
	protected boolean wasPawnTwoFieldsMove = false;

	PlayedMove(Square from, Square to, Piece movedPiece, Piece movedPieceState, Piece takenPiece, Piece takenPieceState, castling castlingMove, boolean wasEnPassant,
			Piece promotedPiece) {
		this.from = from;
		this.to = to;

		this.movedPiece = movedPiece;
		this.takenPiece = takenPiece;
		this.movedPieceState = movedPieceState;
		this.takenPieceState = takenPieceState;

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

	public Piece getTakenPieceState() {
		return this.takenPieceState;
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
