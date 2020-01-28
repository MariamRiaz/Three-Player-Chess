package jchess.move;

import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.model.Square;
import jchess.game.history.MoveHistoryEntry;
import jchess.game.player.Player;
import jchess.move.buff.Buff;
import jchess.move.buff.BuffType;
import jchess.move.effects.BoardTransition;
import jchess.move.effects.BoardTransitionBuilder;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that contains method to evaluate if a move is valid and gets possible target Squares
 */
public class MoveEvaluator implements IMoveEvaluator{
    private RoundChessboardController chessboardController;

    /**
     * Constructor
     *
     * @param chessboardController
     */
    public MoveEvaluator(RoundChessboardController chessboardController) {
        this.chessboardController = chessboardController;
    }

    /**
     * Gets all Squares to which the Piece on the given Square can move such that the given Squares to be saved are all non-threatened by other Players.
     *
     * @param moving The Piece to be moved.
     * @param toSave The Pieces which must be non-threatened by other Players.
     * @return The possible Squares to which this Piece can be moved under these constraints.
     */
    public HashSet<BoardTransition> getPieceTargetToSavePieces(Piece moving, HashSet<Piece> toSave) {
        HashSet<BoardTransition> ret = getPieceTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;

        for (Iterator<BoardTransition> it = ret.iterator(); it.hasNext(); ) {
            final BoardTransition me = it.next();

            chessboardController.applyBoardTransition(me);
            boolean rm = false;
            for (Piece piece : toSave)
                if (squareIsThreatened(chessboardController.getSquare(piece), piece.getPlayer())) {
                    rm = true;
                    break;
                }
            chessboardController.reverseBoardTransition(me);
            if (rm)
                it.remove();
        }

        return ret;
    }

    /**
     * Retrieves a HashSet of the valid Squares, to which a Piece on the given Square can move, disregarding the constraint to keep certain Pieces non-threatened.
     *
     * @param piece The Piece to be moved.
     * @return The valid target Squares for the Piece on the given Square.
     */
    private HashSet<BoardTransition> getPieceTargetSquares(Piece piece) {
        HashSet<BoardTransition> moveEffects = new HashSet<>();
        if (piece == null)
            return moveEffects;//TODO error handling

        HashSet<MoveDefinition> moves = piece.getDefinition().getMoves();
        for (MoveDefinition it : moves)
            moveEffects.addAll(getTargetSquaresOfMove(it, piece));

        return moveEffects;
    }

    /**
     * Evaluates which of the potential Squares are actually possible to move to with the current board state, the given Move definition instance and the given Piece.
     *
     * @param move  The Move definition by which to select potential target Squares.
     * @param piece The Piece to be moved.
     * @return A HashSet of the concrete Squares to which the Piece can move with the given Move definition.
     */
    private HashSet<BoardTransition> getTargetSquaresOfMove(MoveDefinition move, Piece piece) {
        HashSet<BoardTransition> moveEffects = new HashSet<>();
        if (move == null || piece == null)
            return moveEffects;//TODO error handling

        int count = 0;
        Set<Square> traversed = new HashSet<>();
        Orientation orientation = piece.getOrientation().clone();
        final Square from = chessboardController.getSquare(piece);

        for (Square next = nextSquare(from, move.getX(), move.getY(), orientation);
             next != null && (move.getLimit() == null || count < move.getLimit()) && !traversed.contains(next);
             next = nextSquare(next, move.getX(), move.getY(), orientation)) {

            BoardTransitionBuilder builder = new BoardTransitionBuilder(new MoveHistoryEntry(piece, next, from, move));
        	builder = buildBasicMoveBoardTransition(builder, next, piece, move, orientation);
        	
        	if (builder != null)
        		builder = buildSpecialMoveBoardTransition(builder, move, next, piece);
        	
            if (builder != null) {
                buildPawnPromotionBoardTransition(builder, piece, next);
                
                moveEffects.add(builder.build());
                traversed.add(next);
            }

            if (!move.getConditions().contains(MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return moveEffects;
    }

    /**
     * Gets the next Square when incrementing the indices of the given one by the given x and y values.
     *
     * @param current     The current Square.
     * @param x           The x incrementation.
     * @param y           The y incrementation.
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

            if (chessboardController.getModel().getInnerRimConnected() && current.getPozX() + x < 0)
                orientation.reverseX();
        }

        Square retVal = chessboardController.getSquare(current.getPozX() + x, current.getPozY() + y);
        return retVal;
    }

    /**
     * Evaluates whether the given Piece cannot be made non-threatened regardless of the moves its owning Player undertakes.
     *
     * @param piece Piece  The Piece to be checked.
     * @return boolean true if a the Piece is unsavable.
     */
    public boolean pieceIsUnsavable(Piece piece) {
        if (piece == null)
            return false;
        for (Square sq : chessboardController.getSquares()) {
            if (sq.getPiece() == null || sq.getPiece().getPlayer() != piece.getPlayer())
                continue;

            if (!getPieceTargetToSavePieces(sq.getPiece(), new HashSet<Piece>() {{
                add(piece);
            }}).isEmpty())
                return false;
        }
        return true;
    }
    
    /**
     * Determines whether the given Square is threatened
     *
     * @param square Square  The Square to check for being threatened.
     * @param player Player The Player for which the Square should be checked.
     * @return boolean true if Piece is threatened by any other player.
     */
    private boolean squareIsThreatened(Square square, Player player) {
        if (square == null)
            return false;

        Piece piece = square.getPiece();
        for (Square sq : chessboardController.getSquares()) {
            if (sq.getPiece() == null || sq.getPiece().getPlayer() == player)
                continue;

            HashSet<BoardTransition> validMoveSquares = getPieceTargetSquares(sq.getPiece());
            for (BoardTransition it2 : validMoveSquares) {
                chessboardController.applyBoardTransition(it2);
                if ((piece == null && square.getPiece() != null) || (piece != null && chessboardController.getSquare(piece) == null)) {
                    chessboardController.reverseBoardTransition(it2);
                    return true;
                }
                chessboardController.reverseBoardTransition(it2);
            }
        }
        return false;
    }

    private BoardTransitionBuilder buildDefaultMoveBoardTransition(BoardTransitionBuilder builder, Piece piece, Square square, Orientation orientation) {
    	return builder.addPosChange(chessboardController.getSquare(piece), square)
                .addStateChange(piece, piece.clone().setHasMoved(true).reorient(orientation.clone()));
    }
    
    private BoardTransitionBuilder buildPawnPromotionBoardTransition(BoardTransitionBuilder builder, Piece piece, Square toSquare) {
        if (piece.getDefinition().getType().equals("Pawn") && chessboardController.getModel().isInPromotionArea(toSquare))
            builder.addStateChange(piece, piece.clone().setDefinition(PieceDefinition.PLACEHOLDER));
        return builder;
    }

    private BoardTransitionBuilder buildApplyConfusionBoardTransition(BoardTransitionBuilder builder, Square next) {
        Piece attackedPiece = next.getPiece();
        if (attackedPiece != null) {
            builder.addStateChange(attackedPiece, attackedPiece.clone().addBuff(new Buff(BuffType.Confusion, 2)));
            builder.getMoveHistoryEntry().setPriorityMoveType(MoveType.ApplyConfusion);
            return builder;
        }
        return null;
    }

    private BoardTransitionBuilder buildCastlingBoardTransition(BoardTransitionBuilder builder, MoveDefinition move, Square next, Piece piece) {
        if (piece.hasMoved())
            return null;
        else {
            Square rook = null, nextRook = null;
            if (move.getY() > 0) {
                rook = chessboardController.getSquare(next.getPozX(), next.getPozY() + 1);
                nextRook = chessboardController.getSquare(next.getPozX(), next.getPozY() - 1);
            } else {
                rook = chessboardController.getSquare(next.getPozX(), next.getPozY() - 1);
                nextRook = chessboardController.getSquare(next.getPozX(), next.getPozY() + 1);
            }

            if (rook.getPiece() == null || rook.getPiece().hasMoved())
                return null;

            for (Square sq : chessboardController.getModel().getSquaresBetween(chessboardController.getSquare(piece), rook))
                if (squareIsThreatened(sq, piece.getPlayer()))
                    return null;

            builder.addPosChange(chessboardController.getSquare(piece), next)
                    .addStateChange(piece, piece.clone().setHasMoved(true))
                    .addPosChange(rook, nextRook)
                    .addStateChange(rook.getPiece(), rook.getPiece().clone().setHasMoved(true));
            builder.getMoveHistoryEntry().setPriorityMoveType(MoveType.Castling);
            
            return builder;
        }
    }
    
    private BoardTransitionBuilder buildExplodeBoardTransition(BoardTransitionBuilder builder, Piece piece) {
    	builder.addStateChange(piece, piece.clone().addBuff(new Buff(BuffType.ImminentExplosion, 1)));
    	builder.getMoveHistoryEntry().setPriorityMoveType(MoveType.Explode);
    	return builder;
    }

    private BoardTransitionBuilder buildSpecialMoveBoardTransition(BoardTransitionBuilder builder, MoveDefinition move, Square next, Piece piece) {
        if (move.getConditions().contains(MoveType.Castling)) {
        	builder.clear();
            return buildCastlingBoardTransition(builder, move, next, piece);
        } 
        else if (move.getConditions().contains(MoveType.ApplyConfusion)) {
            builder.clear();
        	return buildApplyConfusionBoardTransition(builder, next);
        }
        else if (move.getConditions().contains(MoveType.Explode)) {
        	builder.clear();
        	return buildExplodeBoardTransition(builder, piece);
        }
        
        return builder;
    }
    
    private BoardTransitionBuilder buildBasicMoveBoardTransition(BoardTransitionBuilder builder, Square toSquare,
    		Piece piece, MoveDefinition move, Orientation orientation) {
    	MoveType priorityMoveType = MoveType.OnlyMove;
    	
        if (move.getConditions().contains(MoveType.OnlyAttack)) {
            if (toSquare.getPiece() == null || toSquare.getPiece().getPlayer() == piece.getPlayer())
                return null;
            priorityMoveType = MoveType.OnlyAttack;
        } else if (move.getConditions().contains(MoveType.OnlyMove)) {
            if (toSquare.getPiece() != null)
                return null;
            priorityMoveType = MoveType.OnlyMove;
        } else { 
        	if (toSquare.getPiece() != null && toSquare.getPiece().getPlayer() == piece.getPlayer() && toSquare.getPiece() != piece)
        		return null;
        	if (toSquare.getPiece() != null)
        		priorityMoveType = MoveType.OnlyAttack;
        	priorityMoveType = MoveType.OnlyMove;
        }
        
        if (move.getConditions().contains(MoveType.OnlyWhenFresh)) {
        	if (piece.hasMoved())
                return null;
        	priorityMoveType = MoveType.OnlyWhenFresh;
        }
        
        buildDefaultMoveBoardTransition(builder, piece, toSquare, orientation);
        builder.getMoveHistoryEntry().setPriorityMoveType(priorityMoveType);
        return builder;
    }
}
