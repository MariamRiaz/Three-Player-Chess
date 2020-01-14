package jchess.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jchess.entities.Square;
import jchess.model.RoundChessboardModel;
import jchess.pieces.Move;
import jchess.pieces.MoveType;
import jchess.pieces.Orientation;
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
	
	/**
	 * Retrieves a HashSet of the valid Squares, to which a Piece on the given Square can move, disregarding the constraint to keep certain Pieces non-threatened.
	 * @param square The Square on which the Piece to be moved is located.
	 * @return The valid target Squares for the Piece on the given Square.
	 */
    private HashSet<Square> getValidTargetSquares(Square square) {
        HashSet<Square> ret = new HashSet<>();

        if (square == null || square.getPiece() == null)
            return ret;//TODO error handling

        HashSet<Move> moves = square.getPiece().getDefinition().getMoves();
        for (Move it : moves)
            ret.addAll(evaluateMoveToTargetSquares(it, square));

        return ret;
    }

    /**
     * Evaluates which of the potential Squares are actually possible to move to with the current board status, the given Move definition instance, and the Piece on the given Square.
     * @param move The Move definition by which to select potential target Squares.
     * @param square The Square on which the Piece to be moved is located.
     * @return A HashSet of the concrete Squares to which the Piece can move with the given Move definition.
     */
    private HashSet<Square> evaluateMoveToTargetSquares(Move move, Square square) {
        HashSet<Square> ret = new HashSet<>();

        if (move == null || square == null || square.getPiece() == null)
            return ret;//TODO error handling

        int count = 0;
        Orientation otn = square.getPiece().getOrientation().clone();
        for (Square next = nextSquare(square, move.x, move.y, otn); next != null
                && (move.limit == null || count < move.limit) && !ret.contains(next); 
        		next = nextSquare(next, move.x, move.y, otn)) {
            boolean add = true;

            if (move.conditions.contains(MoveType.OnlyAttack)) {
                if (next.getPiece() == null || next.getPiece().player == square.getPiece().player)
                    add = false;
            } else if (move.conditions.contains(MoveType.OnlyMove)) {
                if (next.getPiece() != null)
                    add = false;
            } else if (next.getPiece() != null && next.getPiece().player == square.getPiece().player)
                add = false;

            if (move.conditions.contains(MoveType.OnlyWhenFresh) && square.getPiece().hasMoved())
                add = false;

            if (add)
                ret.add(next);

            if (!move.conditions.contains(MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return ret;
    }

    /**
     * Gets the next Square when incrementing the indices of the given one by the given x and y values.
     * @param current The current Square.
     * @param x The x incrementation.
     * @param y The y incrementation.
     * @param orientation The Orientation of the Piece that is moving.
     * @return The next Square, if any.
     */
    private Square nextSquare(Square current, int x, int y, Orientation orientation) {
    	if (orientation != null) {
	    	if (orientation.x)
	    		x = -x;
	    	if (orientation.y)
	    		y = -y;
	    }
    	
        Square retVal = model.getSquare(current.getPozX() + x, current.getPozY() + y);
        
        if (model.getInnerRimConnected() && current.getPozX() + x < 0)
        	orientation.reverseX();
        
        return retVal;
    }

    /**
     * Evaluates whether the Piece on the given Square cannot be made non-threatened regardless of the moves its owning Player undertakes.
     * @param square    Square  The Square on which the Piece is located which is to be checked.
     * @return          boolean true if a the Piece is unsavable.
     */
    public boolean squareIsUnsavable(Square square) {
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
     * Gets all Squares to which the Piece on the given Square can move such that the given Squares to be saved are all non-threatened by other Players.
     * @param moving The Square on which the Piece to be moved is located.
     * @param toSave The Squares which must be non-threatened by other Players.
     * @return The possible Squares to which this Piece can be moved under these constraints.
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
     * Determines whether the Piece on the given Square is threatened
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
