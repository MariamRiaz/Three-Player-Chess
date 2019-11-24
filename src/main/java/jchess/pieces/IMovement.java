package jchess.pieces;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashSet;

public interface IMovement {
	public final class Move {
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
	
	/**
	 * Gets all available moves of this piece.
	 * @return Non-null list of all available moves.
	 */
	public HashSet<Move> getMoves();
}
