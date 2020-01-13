package jchess.pieces;

import java.security.InvalidParameterException;
import java.util.HashSet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Class representing a potential Move of a Piece. It describes the direction, limit and conditions for this Move.
 */
public class Move {
	private static MoveType parseMoveType(String str) {
		switch (str) {
		case "OnlyAttack":
			return MoveType.OnlyAttack;
		case "OnlyMove":
			return MoveType.OnlyMove;
		case "Unblockable":
			return MoveType.Unblockable;
		case "OnlyWhenFresh":
			return MoveType.OnlyWhenFresh;
		case "Castling":
			return MoveType.Castling;
		case "EnPassant":
			return MoveType.EnPassant;
		default:
			return null;
		}
	}
	
	public static Move loadFromJSON(JsonObject jsonBody) {
		int x = 0, y = 0;
		Integer limit = null;
		HashSet<MoveType> conditions = new HashSet<MoveType>();

		if (jsonBody.get("x") != null)
			x = jsonBody.get("x").getAsInt();
		
		if (jsonBody.get("y") != null)
			y = jsonBody.get("y").getAsInt();
		
		if (jsonBody.get("limit") != null)
			limit = jsonBody.get("limit").getAsInt();
		
		if (jsonBody.get("conditions") != null && jsonBody.get("conditions").isJsonArray())
			for (JsonElement element : jsonBody.get("conditions").getAsJsonArray()) 
				if (element.isJsonPrimitive()) {
					MoveType mt = parseMoveType(element.getAsString());
					if (mt != null)
						conditions.add(mt);
				}
		
		return new Move(x, y, limit, conditions);
	}
	
	public final int x, y;
	public final Integer limit;
	public final HashSet<MoveType> conditions;
	
	private Move(int x, int y, Integer limit, HashSet<MoveType> conditions) {
		this.x = x;
		this.y = y;
		this.limit = limit;
		
		if (conditions == null)
			throw new NullPointerException("Argument 'conditions' is null.");
		this.conditions = conditions;
		
		if (this.conditions.contains(MoveType.OnlyAttack) && this.conditions.contains(MoveType.OnlyMove))
			throw new InvalidParameterException("Move conditions cannot include 'OnlyAttack' and 'OnlyMove' simultaneously.");
	}
}