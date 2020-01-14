package jchess.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jchess.entities.Player;
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
	 * @param piece The Piece to be moved.
	 * @return The valid target Squares for the Piece on the given Square.
	 */
    private HashSet<Square> getValidTargetSquares(Piece piece) {
        HashSet<Square> ret = new HashSet<>();

        if (piece == null)
            return ret;//TODO error handling

        HashSet<Move> moves = piece.getDefinition().getMoves();
        for (Move it : moves)
            ret.addAll(evaluateMoveToTargetSquares(it, piece));

        return ret;
    }

    /**
     * Evaluates which of the potential Squares are actually possible to move to with the current board state, the given Move definition instance and the given Piece.
     * @param move The Move definition by which to select potential target Squares.
     * @param piece The Piece to be moved.
     * @return A HashSet of the concrete Squares to which the Piece can move with the given Move definition.
     */
    private HashSet<Square> evaluateMoveToTargetSquares(Move move, Piece piece) {
        HashSet<Square> ret = new HashSet<>();

        if (move == null || piece == null)
            return ret;//TODO error handling

        int count = 0;
        Orientation otn = piece.getOrientation().clone();
        for (Square next = nextSquare(model.getSquare(piece), move.x, move.y, otn); next != null
                && (move.limit == null || count < move.limit) && !ret.contains(next); 
        		next = nextSquare(next, move.x, move.y, otn)) {
            boolean add = true;

            if (move.conditions.contains(MoveType.OnlyAttack)) {
                if (next.getPiece() == null || next.getPiece().player == piece.player)
                    add = false;
            } else if (move.conditions.contains(MoveType.OnlyMove)) {
                if (next.getPiece() != null)
                    add = false;
            } else if (next.getPiece() != null && next.getPiece().player == piece.player)
                add = false;

            if (move.conditions.contains(MoveType.OnlyWhenFresh) && piece.hasMoved())
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
     * Evaluates whether the given Piece cannot be made non-threatened regardless of the moves its owning Player undertakes.
     * @param piece    Piece  The Piece to be checked.
     * @return          boolean true if a the Piece is unsavable.
     */
    public boolean pieceIsUnsavable(Piece piece) {
        if (piece == null)
            return false;
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player != piece.player)
                continue;
			
            if (!getValidTargetSquaresToSavePiece(sq.getPiece(), piece).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * Gets all Squares to which the Piece on the given Square can move such that the given Squares to be saved are all non-threatened by other Players.
     * @param moving The Piece to be moved.
     * @param toSave The Pieces which must be non-threatened by other Players.
     * @return The possible Squares to which this Piece can be moved under these constraints.
     */
    public HashSet<Square> getValidTargetSquaresToSavePiece(Piece moving, Piece... toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;
		
		final List<Piece> temp = Arrays.asList(toSave);
		
        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            final Square target = it.next(), start = model.getSquare(moving);
            final Piece old = target.getPiece();
			
            model.setPieceOnSquare(moving, target);
            for (Piece piece : temp)
            	if (squareIsThreatened(model.getSquare(piece)))
            		it.remove();

            model.setPieceOnSquare(old, target);
            model.setPieceOnSquare(moving, start);
        }

        return ret;
    }

    /**
     * Determines whether the Piece on the given Square is threatened
     * @param square    Square  The Square to check for being threatened.
     * @return          boolean true if Piece is threatened
     */
    public boolean squareIsThreatened(Square square) {
        if (square == null || square.getPiece() == null)
            return false;
		
        return squareIsThreatened(square, square.getPiece().player);
    }
    
    /**
     * Determines whether the given Square is threatened
     * @param square    Square  The Square to check for being threatened.
     * @param player 	Player The Player for which the Square should be checked.
     * @return          boolean true if Piece is threatened by any other player.
     */
    public boolean squareIsThreatened(Square square, Player player) {
        if (square == null)
            return false;
		
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player == player)
                continue;
			
            HashSet<Square> validMoveSquares = getValidTargetSquares(sq.getPiece());
            for (Square it2 : validMoveSquares)
                if (it2.equals(square))
                    return true;
        }
        return false;
    }
}
