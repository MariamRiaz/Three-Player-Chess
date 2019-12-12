package jchess.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jchess.entities.Square;
import jchess.model.RoundChessboardModel;
import jchess.pieces.Piece;

/**
 * Class that contains method to evaluate if a move is valid and gets possible target Squares
 */
public class MoveEvaluator {
	private RoundChessboardModel model;

    /**
     * Constructor
     * @param model     RoundChessboardModel    the model of the chessboard
     */
	public MoveEvaluator (RoundChessboardModel model) {
		this.model = model;
	}
	
    private HashSet<Square> getValidTargetSquares(Square square) {
        HashSet<Square> ret = new HashSet<>();

        if (square == null || square.getPiece() == null)
            return ret;//TODO error handling

        HashSet<Piece.Move> moves = square.getPiece().getMoves();
        for (Piece.Move it : moves)
            ret.addAll(evaluateMoveToTargetSquares(it, square));

        return ret;
    }

    private HashSet<Square> evaluateMoveToTargetSquares(Piece.Move move, Square square) {
        HashSet<Square> ret = new HashSet<>();

        if (move == null || square == null || square.getPiece() == null)
            return ret;//TODO error handling

        int count = 0;
        for (Square next = nextSquare(square, move.x, move.y); next != null
                && (move.limit == null || count < move.limit) && !ret.contains(next); next = nextSquare(next, move.x, move.y)) {
            boolean add = true;

            if (move.conditions.contains(Piece.Move.MoveType.OnlyAttack)) {
                if (next.getPiece() == null || next.getPiece().player == square.getPiece().player)
                    add = false;
            } else if (move.conditions.contains(Piece.Move.MoveType.OnlyMove)) {
                if (next.getPiece() != null)
                    add = false;
            } else if (next.getPiece() != null && next.getPiece().player == square.getPiece().player)
                add = false;

            if (move.conditions.contains(Piece.Move.MoveType.OnlyWhenFresh) && square.getPiece().hasMoved())
                add = false;

            if (add)
                ret.add(next);

            if (!move.conditions.contains(Piece.Move.MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return ret;
    }

    private Square nextSquare(Square current, int x, int y) {
        return model.getSquare(current.getPozX() + x, current.getPozY() + y);
    }

    /**
     * evaluates whether the Piece on the given Square is unsavable
     * @param square    Square  the square on which the piece is located for which the savable check shall be performed
     * @return          boolean true if a the Piece is unsavable
     */
    public boolean squareUnsavable(Square square) {
        if (square == null || square.getPiece() == null)
            return false;
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player != square.getPiece().player)
                continue;
			
            if (!getValidTargetSquaresToSavePiece(sq, square).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * gets all Squares where a Piece can move to in order to be saved
     * @param moving
     * @param toSave
     * @return
     */
    public HashSet<Square> getValidTargetSquaresToSavePiece(Square moving, Square... toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;
		
		List<Square> temp = Arrays.asList(toSave);
		boolean movingPieceToBeSaved = temp.contains(moving);
		
        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            Square target = it.next(), start = moving;
            Piece old = target.getPiece();
			
            model.setPieceOnSquare(moving.getPiece(), target);
            for (Square square : temp)
            	if (movingPieceToBeSaved && squareIsThreatened(target))
            		it.remove();
            	else if (squareIsThreatened(square))
                    it.remove();

            model.setPieceOnSquare(old, target);
            model.setPieceOnSquare(moving.getPiece(), start);
        }

        return ret;
    }

    /**
     * determines whether the Piece on the given Square is threatened
     * @param square    Square  square on which the piece is located that is potentially threatened
     * @return          boolean true if Piece is threatened
     */
    public boolean squareIsThreatened(Square square) {
        if (square == null || square.getPiece() == null)
            return false;
		
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player == square.getPiece().player)
                continue;
			
            HashSet<Square> validMoveSquares = getValidTargetSquares(sq);
            for (Square it2 : validMoveSquares)
                if (it2.equals(square))
                    return true;
        }
        return false;
    }
}
