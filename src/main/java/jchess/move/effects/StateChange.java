package jchess.move.effects;

import jchess.pieces.Piece;

public class StateChange {
	public final int id;
	public final Piece state;
	
	protected StateChange (int id, Piece state) {
		this.id = id;
		this.state = state;
	}
}
