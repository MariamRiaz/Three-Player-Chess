package jchess.pieces;

import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class PieceDefinition {
	/**
     * Loads a PieceDefinition from the given JsonElement.
	 * @param jsonBody The source.
	 * @return The stored PieceDefinition.
	 */
	public static PieceDefinition loadFromJSON(JsonElement jsonBody) {
		return new Gson().fromJson(jsonBody, PieceDefinition.class);
	}
	
	private int value;
	private String type, symbol;
	private HashSet<Move> moves;
	
	/**
	 * Creates a new Piece with the same attributes as those of the Piece other.
	 * @param other Piece whose attributes to copy. Must be non-null.
	 */
	private PieceDefinition(PieceDefinition other) {
		if (other == null)
			throw new NullPointerException("Argument 'other' is null.");
		
		this.value = other.value;
		this.moves = new HashSet<Move>(other.moves);
		this.symbol = new String(other.symbol);
		this.type = new String(other.type);
	}

	/**
	 * Returns a deep copy of this Piece.
	 */
	public PieceDefinition clone() {
		return new PieceDefinition(this);
	}
	
	/**
	 * @return The Piece type, e.g. "King".
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return The Piece symbol, e.g. "K".
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * @return The value of this Piece in points.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * @return List of all available Moves for this Piece.
	 */
	public HashSet<Move> getMoves() {
		return moves;
	}
}
