package jchess.move;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Class representing a potential Move of a Piece. It describes the direction, limit and conditions for this Move.
 */
public class MoveDefinition {
	public static final String formatStringPiece = "Piece", formatStringFrom = "From", formatStringTo = "To";
	
	private static final HashMap<MoveType, String> defaultFormatStrings = new HashMap<MoveType, String>() {{
			put(MoveType.OnlyMove, "${" + formatStringPiece + "}${" + formatStringFrom + "}-${" + formatStringTo + "}");
			put(MoveType.OnlyAttack, "${" + formatStringPiece + "}${" + formatStringFrom + "}x${" + formatStringTo + "}");
	}};

	private static final HashSet<MoveType> defaultConditions = new HashSet<MoveType>();
	
	private int x, y;
	private Integer limit;
	private HashSet<MoveType> conditions;
	private HashMap<MoveType, String> formatStrings;
	
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
	
	public String getFormatString(MoveType type) {
		if (formatStrings == null || formatStrings.get(type) == null) {
			final String retVal = defaultFormatStrings.get(type);
			if (retVal == null)
				return getDefaultFormatString();
			return retVal;
		}
		return formatStrings.get(type);
	}
	
	public String getDefaultFormatString() {
		if (formatStrings != null && !formatStrings.isEmpty())
			return formatStrings.entrySet().iterator().next().getValue();
		if (!defaultFormatStrings.isEmpty())
			return defaultFormatStrings.entrySet().iterator().next().getValue();
		return "";
	}
}