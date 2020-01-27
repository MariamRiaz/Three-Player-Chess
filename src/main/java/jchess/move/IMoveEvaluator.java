package jchess.move;

import jchess.move.effects.MoveEffect;
import jchess.pieces.Piece;

import java.util.HashSet;

public interface IMoveEvaluator {


    boolean pieceIsUnsavable(Piece piece);

    HashSet<MoveEffect> getValidTargetSquaresToSavePiece(Piece moving, HashSet<Piece> toSave);

}
