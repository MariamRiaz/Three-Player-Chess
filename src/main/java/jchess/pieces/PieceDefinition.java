package jchess.pieces;

import java.util.HashSet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PieceDefinition {
	public static PieceDefinition loadFromJSON(JsonElement jsonBody) {
		int value = 0;
		String type = null, symbol = null;
		HashSet<Move> moves = new HashSet<Move>();
		
		if (jsonBody != null && jsonBody.isJsonObject()) {
			JsonObject json = jsonBody.getAsJsonObject();
			
			if (json.get("value") != null && json.get("value").isJsonPrimitive())
				value = json.get("value").getAsInt();
			
			if (json.get("type") != null && json.get("type").isJsonPrimitive())
				type = json.get("type").getAsString();
			
			if (json.get("symbol") != null && json.get("symbol").isJsonPrimitive())
				symbol = json.get("symbol").getAsString();
			
			if (json.get("moves") != null && json.get("moves").isJsonArray())
				for (JsonElement element : json.get("moves").getAsJsonArray()) 
					if (element.isJsonObject())
						moves.add(Move.loadFromJSON(element.getAsJsonObject()));
		}
		
		return new PieceDefinition (type, value, symbol, moves);
	}
	
	public final int value;
	public final String type, symbol;
	public final HashSet<Move> moves;
	
	/**
	 * Creates a new Piece based on the given parameters. Piece attributes cannot be changed after initialization.
	 * @param player Must be non-null.
	 * @param type Must be non-null. The type of this Piece, e.g. Pawn, King etc.
	 * @param value The value in points of this Piece.
	 * @param symbol Must be non-null. The shorthand symbol of this Piece, e.g. N, K, Q etc.
	 * @param moves The Moves for this Piece. Each Move must be non-null.
	 */
	private PieceDefinition(String type, int value, String symbol, HashSet<Move> moves) {
		this.value = value;
		
		if (moves == null)
			throw new NullPointerException("Argument 'moves' is null.");
		this.moves = moves;
		
		if (type == null)
			throw new NullPointerException("Argument 'type' is null.");
		this.symbol = symbol;
		
		if (symbol == null)
			throw new NullPointerException("Argument 'symbol' is null.");
		this.type = type;
	}
	
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
	 * @return List of all available Moves for this Piece.
	 */
	public HashSet<Move> getMoves() {
		return moves;
	}
}
