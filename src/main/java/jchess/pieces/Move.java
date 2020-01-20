package jchess.pieces;

import java.util.HashSet;
/**
 * Class representing a potential Move of a Piece. It describes the direction, limit and conditions for this Move.
 */
public class Move {
	private static final HashSet<MoveType> defaultConditions = new HashSet<MoveType>();
	
	private int x, y;
	private Integer limit;
	private HashSet<MoveType> conditions;
	
	public HashSet<MoveType> getConditions() {
		if (conditions == null)
			return defaultConditions;
		return conditions;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}