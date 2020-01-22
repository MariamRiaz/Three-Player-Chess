package jchess.pieces;

/**
 * Enum describing constraints, conditions, and move types to be observed when evaluating this Move.
 */
public enum MoveType {
	OnlyAttack,
	OnlyMove,
	Unblockable,
	OnlyWhenFresh,
	Castling, 
	EnPassant
}
