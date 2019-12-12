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

package jchess.entities;

import jchess.controller.RoundChessboardController;
import jchess.controller.MoveHistory.castling;
import jchess.pieces.Piece;

/**
 * A model class for getting the current place and the new place of a piece once its moved
 * also will have the record for the pieces that are moved, taken or promoted
 */

public class PlayedMove {

	protected Square from = null;
	protected Square to = null;
	protected Piece movedPiece = null, movedPieceState = null;
	protected Piece takenPiece = null;
	protected Piece promotedTo = null;
	protected boolean wasEnPassant = false;
	protected castling castlingMove = castling.none;
	protected boolean wasPawnTwoFieldsMove = false;

	/**
	 * Constructor of PlayedMove class
	 * @param from Square from which the piece is moved
	 * @param to Square to which the piece is moved
	 * @param movedPiece type of moved piece
	 * @param movedPieceState previous state of moved piece
	 * @param takenPiece piece that was killed by the moved piece
	 * @param castlingMove is the current move a castling move?
	 * @param wasEnPassant is the current move an EnPassant?
	 * @param promotedPiece promoted piece
	 */
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

	/**
	 * Get the Square from which the piece is moved
	 */
	public Square getFrom() {
		return this.from;
	}

	/**
	 * Get the Square to which the piece is moved
	 */
	public Square getTo() {
		return this.to;
	}

	/**
	 * Get the Piece which is moved
	 */
	public Piece getMovedPiece() {
		return this.movedPiece;
	}

	/**
	 * Get the previous state of the Piece which is moved
	 */
	public Piece getMovedPieceState() {
		return this.movedPieceState;
	}

	/**
	 * Get the Piece which is taken
	 */
	public Piece getTakenPiece() {
		return this.takenPiece;
	}

	/**
	 * To check if the move was an EnPassant
	 */
	public boolean wasEnPassant() {
		return this.wasEnPassant;
	}

	/**
	 * To check if the move was a pawn two fields move
	 */
	public boolean wasPawnTwoFieldsMove() {
		return this.wasPawnTwoFieldsMove;
	}

	/**
	 * To get the type of Castling move
	 */
	public castling getCastlingMove() {
		return this.castlingMove;
	}

	/**
	 * To get the Promoted Piece
	 */
	public Piece getPromotedPiece() {
		return this.promotedTo;
	}
}
