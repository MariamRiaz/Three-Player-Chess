package jchess.pieces;

import jchess.Player;
import jchess.pieces.Piece.Move.MoveType;

public class PieceFactory {
	public static final Piece createRook(Player player) {
		return new Piece(player, "Rook", 2, "R", new Piece.Move(1, 0, null), new Piece.Move(-1, 0, null), new Piece.Move(0, 1, null), new Piece.Move(0, -1, null));
	}
	
	public static final Piece createKing(Player player) {
		return new Piece(player, "King", 99, "K", new Piece.Move(1, 1, 1), new Piece.Move(1, -1, 1), new Piece.Move(-1, -1, 1), new Piece.Move(-1, 1, 1),
				new Piece.Move(1, 0, 1), new Piece.Move(-1, 0, 1), new Piece.Move(0, 1, 1), new Piece.Move(0, -1, 1));
	}
	
	public static final Piece createKnight(Player player) {
		return new Piece(player, "Knight", 3, "N", new Piece.Move(-1, 2, 1), new Piece.Move(-1, -2, 1), new Piece.Move(1, 2, 1), new Piece.Move(1, -2, 1),
				new Piece.Move(2, -1, 1), new Piece.Move(-2, -1, 1), new Piece.Move(2, 1, 1), new Piece.Move(-2, 1, 1));
	}
	
	public static final Piece createQueen(Player player) {
		return new Piece(player, "Queen", 4, "Q", new Piece.Move(1, 1, null), new Piece.Move(1, -1, null), new Piece.Move(-1, -1, null), new Piece.Move(-1, 1, null),
				new Piece.Move(1, 0, null), new Piece.Move(-1, 0, null), new Piece.Move(0, 1, null), new Piece.Move(0, -1, null));
	}

	public static final Piece createBishop(Player player) {
		return new Piece(player, "Bishop", 3, "B", new Piece.Move(-1, -1, null), new Piece.Move(-1, 1, null),
				new Piece.Move(1, 1, null), new Piece.Move(1, -1, null));
	}
	
	public static final Piece createPawn(Player player, boolean direction) {
		int y;
		if (direction)
			y = -1;
		else y = 1;
		return new Piece(player, "Pawn", 1, "", new Piece.Move(0, y, 1, MoveType.OnlyMove), new Piece.Move(0, y, 2, MoveType.OnlyMove, MoveType.OnlyWhenFresh),
				new Piece.Move(1, y, 1, MoveType.OnlyAttack), new Piece.Move(-1, y, 1, MoveType.OnlyAttack));
	}
}
