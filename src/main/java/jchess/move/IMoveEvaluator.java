package jchess.move;

import jchess.move.effects.BoardTransition;
import jchess.pieces.Piece;

import java.util.HashSet;

public interface IMoveEvaluator {


    boolean pieceIsUnsavable(Piece piece);

    HashSet<BoardTransition> getPieceTargetToSavePieces(Piece moving, HashSet<Piece> toSave);

}
