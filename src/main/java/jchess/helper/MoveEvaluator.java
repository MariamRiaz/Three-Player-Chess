package jchess.helper;

import java.util.HashSet;
import java.util.Iterator;

import jchess.entities.Player;
import jchess.entities.Square;
import jchess.model.RoundChessboardModel;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.move.Orientation;
import jchess.move.effects.MoveEffects;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;

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
    private HashSet<MoveEffects> getValidTargetSquares(Piece piece) {
        HashSet<MoveEffects> ret = new HashSet<>();
        
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
    private HashSet<MoveEffects> evaluateMoveToTargetSquares(Move move, Piece piece) {
        HashSet<MoveEffects> ret = new HashSet<>();
        HashSet<Square> traversed = new HashSet<Square>();
        
        if (move == null || piece == null)
            return ret;//TODO error handling

        int count = 0;
        Orientation otn = piece.getOrientation().clone();
        for (Square next = nextSquare(model.getSquare(piece), move.x, move.y, otn); next != null
                && (move.limit == null || count < move.limit) && !traversed.contains(next); 
        		next = nextSquare(next, move.x, move.y, otn)) {
        	
            boolean add = true;
            MoveEffectsBuilder meb = new MoveEffectsBuilder(piece, next, move);

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
            
            if (move.conditions.contains(MoveType.Castling)) {
            	if (piece.hasMoved())
            		add = false;
            	else {
            		
            	}
            }
            
            if (move.conditions.contains(MoveType.EnPassant)) {
            	
            }
            
            if (add) {
                if (meb.isEmpty())
                	meb.addPosChange(model.getSquare(piece), next)
                		.addStateChange(piece, piece.clone().setHasMoved(true).setOrientation(otn.clone()));
            	
                if (piece.getDefinition().type.equals("Pawn") && model.isEnemyStart(next, piece.player.color))
                	meb.addStateChange(piece, piece.clone().setDefinition(PieceDefinition.PLACEHOLDER));
                
                ret.add(meb.build());
            	traversed.add(next);
            }
                
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
    	if (current == null)
    		return null;
    	
    	if (orientation != null) {
	    	if (orientation.x)
	    		x = -x;
	    	if (orientation.y)
	    		y = -y;
	    	
            if (model.getInnerRimConnected() && current.getPozX() + x < 0)
            	orientation.reverseX();
	    }
    	
        Square retVal = model.getSquare(current.getPozX() + x, current.getPozY() + y);
        
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
			
            if (!getValidTargetSquaresToSavePiece(sq.getPiece(), new HashSet<Piece>() {{ add(piece); }} ).isEmpty())
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
    public HashSet<MoveEffects> getValidTargetSquaresToSavePiece(Piece moving, HashSet<Piece> toSave) {
        HashSet<MoveEffects> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;
		
        for (Iterator<MoveEffects> it = ret.iterator(); it.hasNext(); ) {
            final MoveEffects me = it.next();
        	
            me.apply(model, null);
            boolean rm = false;
            for (Piece piece : toSave) 
            	if (squareIsThreatened(model.getSquare(piece))) {
            		rm = true;
            		break;
            	}
            me.reverse(model, null);
            
            if (rm)
            	it.remove();
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
        
        Piece piece = square.getPiece();
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player == player)
                continue;
			
            HashSet<MoveEffects> validMoveSquares = getValidTargetSquares(sq.getPiece());
            for (MoveEffects it2 : validMoveSquares) {
            	it2.apply(model, null);
            	if ((piece == null && square.getPiece() != null) || (piece != null && model.getSquare(piece) == null)) {
            		it2.reverse(model, null);
            		return true;
            	}
            	it2.reverse(model, null);
            }
        }
        return false;
    }
}
