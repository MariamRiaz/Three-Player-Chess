package jchess.move.effects;

import jchess.pieces.Piece;

public class StateChange {
	private final int id;
	private final Piece state;
	
	protected StateChange (int id, Piece state) {
		this.id = id;
		this.state = state;
	}
	
	/**
	 * @return The ID of the Piece, whose state is to be swapped with the state stored in this StateChange.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * @return The new state of the Piece with the ID stored in this StateChage.
	 */
	public Piece getState() {
		return state;
	}
}
