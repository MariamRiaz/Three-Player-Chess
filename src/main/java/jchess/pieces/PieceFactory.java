package jchess.pieces;

import jchess.entities.Player;
import jchess.pieces.Piece.Move.MoveType;

/*
* A factory class to generate all the pieces of each player and get the possible move values
*  for each player at the current timestamp
* */

public class PieceFactory {
	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @return The new rook Piece.
	 */
	public static final Piece createRook(Player player) {
		return new Piece(player, "Rook", 2, "R", new Piece.Move(1, 0, null), new Piece.Move(-1, 0, null), new Piece.Move(0, 1, null), new Piece.Move(0, -1, null));
	}

	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @return The new king Piece.
	 */
	public static final Piece createKing(Player player) {
		return new Piece(player, "King", 99, "K", new Piece.Move(1, 1, 1), new Piece.Move(1, -1, 1), new Piece.Move(-1, -1, 1), new Piece.Move(-1, 1, 1),
				new Piece.Move(1, 0, 1), new Piece.Move(-1, 0, 1), new Piece.Move(0, 1, 1), new Piece.Move(0, -1, 1));
	}

	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @return The new knight Piece.
	 */
	public static final Piece createKnight(Player player) {
		return new Piece(player, "Knight", 3, "N", new Piece.Move(-1, 2, 1), new Piece.Move(-1, -2, 1), new Piece.Move(1, 2, 1), new Piece.Move(1, -2, 1),
				new Piece.Move(2, -1, 1), new Piece.Move(-2, -1, 1), new Piece.Move(2, 1, 1), new Piece.Move(-2, 1, 1));
	}

	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @return The new queen Piece.
	 */
	public static final Piece createQueen(Player player) {
		return new Piece(player, "Queen", 4, "Q", new Piece.Move(1, 1, null), new Piece.Move(1, -1, null), new Piece.Move(-1, -1, null), new Piece.Move(-1, 1, null),
				new Piece.Move(1, 0, null), new Piece.Move(-1, 0, null), new Piece.Move(0, 1, null), new Piece.Move(0, -1, null));
	}

	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @return The new bishop Piece.
	 */
	public static final Piece createBishop(Player player) {
		return new Piece(player, "Bishop", 3, "B", new Piece.Move(-1, -1, null), new Piece.Move(-1, 1, null),
				new Piece.Move(1, 1, null), new Piece.Move(1, -1, null));
	}

	/**
	 * @param player The new Piece's owning Player. Must be non-null.
	 * @param direction The direction in which the pawn will move
	 * @return The new pawn Piece.
	 */
	public static final Piece createPawn(Player player, boolean direction) {
		int y;
		if (direction)
			y = -1;
		else y = 1;
		return new Piece(player, "Pawn", 1, "", new Piece.Move(0, y, 1, MoveType.OnlyMove), new Piece.Move(0, y, 2, MoveType.OnlyMove, MoveType.OnlyWhenFresh),
				new Piece.Move(1, y, 1, MoveType.OnlyAttack), new Piece.Move(-1, y, 1, MoveType.OnlyAttack));
	}
}
