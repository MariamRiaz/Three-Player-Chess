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
	
	/**
	 * @return The conditions, under which this move is possible.
	 */
	public HashSet<MoveType> getConditions() {
		if (conditions == null)
			return defaultConditions;
		return conditions;
	}
	
	/**
	 * @return The limit to the amount of Squares this move can recurse when being evaluated.
	 */
	public Integer getLimit() {
		return limit;
	}
	
	/**
	 * @return The x offset in Squares on a single recursion of the move.
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The y offset in Squares on a single recursion of the move.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Gets the format string to use when displaying this move in the move history.
	 * @param type The priority MoveType of the move out of all of its MoveType conditions.
	 * @return The corresponding format string.
	 */
	public String getFormatString(MoveType type) {
		if (formatStrings == null || formatStrings.get(type) == null) {
			final String retVal = defaultFormatStrings.get(type);
			if (retVal == null)
				return getDefaultFormatString();
			return retVal;
		}
		return formatStrings.get(type);
	}
	
	/**
	 * @return The default format string for this move to be displayed in the move history.
	 */
	public String getDefaultFormatString() {
		if (formatStrings != null && !formatStrings.isEmpty())
			return formatStrings.entrySet().iterator().next().getValue();
		if (!defaultFormatStrings.isEmpty())
			return defaultFormatStrings.entrySet().iterator().next().getValue();
		return "";
	}
}