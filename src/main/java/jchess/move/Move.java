package jchess.move;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Class representing a potential Move of a Piece. It describes the direction, limit and conditions for this Move.
 */
public class Move {
	public static final String formatStringPiece = "Piece", formatStringFrom = "From", formatStringTo = "To";
	
	private static final HashMap<MoveType, String> defaultFormatStrings = new HashMap<MoveType, String>() {{
			put(MoveType.OnlyMove, "${" + formatStringPiece + "}${" + formatStringFrom + "}-${" + formatStringTo + "}");
			put(MoveType.OnlyAttack, "${" + formatStringPiece + "}${" + formatStringFrom + "}x${" + formatStringTo + "}");
	}};
	
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
		HashMap<MoveType, String> formatStrings = new HashMap<>();

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
		
		if (jsonBody.get("format") != null && jsonBody.get("format").isJsonObject())
			for (Entry<String, JsonElement> element : jsonBody.get("format").getAsJsonObject().entrySet()) {
				final MoveType mt = parseMoveType(element.getKey());
				if (mt != null && element.getValue().isJsonPrimitive())
					formatStrings.put(mt, element.getValue().getAsString());
			}
		
		return new Move(x, y, limit, conditions, formatStrings.isEmpty() ? defaultFormatStrings : formatStrings);
	}
	
	public final int x, y;
	public final Integer limit;
	public final HashSet<MoveType> conditions;
	public final HashMap<MoveType, String> formatStrings;
	
	private Move(int x, int y, Integer limit, HashSet<MoveType> conditions, HashMap<MoveType, String> formatStrings) {
		this.x = x;
		this.y = y;
		this.limit = limit;
		
		if (conditions == null)
			throw new NullPointerException("Argument 'conditions' is null.");
		this.conditions = conditions;
		
		if (this.conditions.contains(MoveType.OnlyAttack) && this.conditions.contains(MoveType.OnlyMove))
			throw new InvalidParameterException("Move conditions cannot include 'OnlyAttack' and 'OnlyMove' simultaneously.");
		
		if (formatStrings == null)
			throw new NullPointerException("Argument 'formatStrings' is null.");
		this.formatStrings = formatStrings;
			
	}
}