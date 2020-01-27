package jchess.move;

import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.model.Square;
import jchess.game.player.Player;
import jchess.move.buff.Buff;
import jchess.move.buff.BuffType;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
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
     * Retrieves a HashSet of the valid Squares, to which a Piece on the given Square can move, disregarding the constraint to keep certain Pieces non-threatened.
     *
     * @param piece The Piece to be moved.
     * @return The valid target Squares for the Piece on the given Square.
     */
    private HashSet<MoveEffect> getValidTargetSquares(Piece piece) {
        HashSet<MoveEffect> moveEffects = new HashSet<>();
        if (piece == null)
            return moveEffects;//TODO error handling

        HashSet<Move> moves = piece.getDefinition().getMoves();
        for (Move it : moves)
            moveEffects.addAll(evaluateMoveToTargetSquares(it, piece));

        return moveEffects;
    }

    /**
     * Evaluates which of the potential Squares are actually possible to move to with the current board state, the given Move definition instance and the given Piece.
     *
     * @param move  The Move definition by which to select potential target Squares.
     * @param piece The Piece to be moved.
     * @return A HashSet of the concrete Squares to which the Piece can move with the given Move definition.
     */
    private HashSet<MoveEffect> evaluateMoveToTargetSquares(Move move, Piece piece) {
        HashSet<MoveEffect> moveEffects = new HashSet<>();
        if (move == null || piece == null)
            return moveEffects;//TODO error handling

        int count = 0;
        Set<Square> traversed = new HashSet<>();
        Orientation orientation = piece.getOrientation().clone();
        final Square from = chessboardController.getSquare(piece);

        for (Square next = nextSquare(from, move.getX(), move.getY(), orientation);
             next != null && (move.getLimit() == null || count < move.getLimit()) && !traversed.contains(next);
             next = nextSquare(next, move.getX(), move.getY(), orientation)) {

            MoveEffectsBuilder moveEffectsBuilder = new MoveEffectsBuilder(piece, next, from, move, true);
            moveEffectsBuilder = evaluateAndGenEffects(move, next, piece, moveEffectsBuilder);

            if (moveEffectsBuilder != null) {
                if (moveEffectsBuilder.isEmpty())
                    moveEffectsBuilder.addPosChange(chessboardController.getSquare(piece), next)
                            .addStateChange(piece, piece.clone().setHasMoved(true).reorient(orientation.clone()));

                if (piece.getDefinition().getType().equals("Pawn") && chessboardController.getModel().isInPromotionArea(next))
                    moveEffectsBuilder.addStateChange(piece, piece.clone().setDefinition(PieceDefinition.PLACEHOLDER));

                moveEffects.add(moveEffectsBuilder.build());
                traversed.add(next);
            }

            if (!move.getConditions().contains(MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return moveEffects;
    }

    private MoveEffectsBuilder evaluateApplyConfusion(Square next,
                                                      MoveEffectsBuilder moveEffectsBuilder) {
        Piece attackedPiece = next.getPiece();
        if (attackedPiece != null) {
            return moveEffectsBuilder
                    .addStateChange(attackedPiece, attackedPiece.clone().addBuff(new Buff(BuffType.Confusion, 2)));
        }
        return null;
    }

    private MoveEffectsBuilder evaluateCastling(Move move, Square next, Piece piece, MoveEffectsBuilder meb) {
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

            return meb.addPosChange(chessboardController.getSquare(piece), next)
                    .addStateChange(piece, piece.clone().setHasMoved(true))
                    .addPosChange(rook, nextRook)
                    .addStateChange(rook.getPiece(), rook.getPiece().clone().setHasMoved(true));
        }
    }

    private MoveEffectsBuilder evaluateAndGenEffects(Move move, Square next, Piece piece, MoveEffectsBuilder meb) {
        if (move.getConditions().contains(MoveType.OnlyAttack)) {
            if (next.getPiece() == null || next.getPiece().getPlayer() == piece.getPlayer())
                return null;
        } else if (move.getConditions().contains(MoveType.OnlyMove)) {
            if (next.getPiece() != null)
                return null;
        } else if (next.getPiece() != null && next.getPiece().getPlayer() == piece.getPlayer())
            return null;

        if (move.getConditions().contains(MoveType.OnlyWhenFresh) && piece.hasMoved())
            return null;

        if (move.getConditions().contains(MoveType.Castling))
            return evaluateCastling(move, next, piece, meb);
        if (move.getConditions().contains(MoveType.ApplyConfusion)) {
            return evaluateApplyConfusion(next, meb);
        }

        if (move.getConditions().contains(MoveType.EnPassant)) {

        }

        return meb;
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

            if (!getValidTargetSquaresToSavePiece(sq.getPiece(), new HashSet<Piece>() {{
                add(piece);
            }}).isEmpty())
                return false;
        }
        return true;
    }

    /**
     * Gets all Squares to which the Piece on the given Square can move such that the given Squares to be saved are all non-threatened by other Players.
     *
     * @param moving The Piece to be moved.
     * @param toSave The Pieces which must be non-threatened by other Players.
     * @return The possible Squares to which this Piece can be moved under these constraints.
     */
    public HashSet<MoveEffect> getValidTargetSquaresToSavePiece(Piece moving, HashSet<Piece> toSave) {
        HashSet<MoveEffect> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;

        for (Iterator<MoveEffect> it = ret.iterator(); it.hasNext(); ) {
            final MoveEffect me = it.next();

            chessboardController.apply(me);
            boolean rm = false;
            for (Piece piece : toSave)
                if (squareIsThreatened(chessboardController.getSquare(piece))) {
                    rm = true;
                    break;
                }
            chessboardController.reverse(me);
            if (rm)
                it.remove();
        }

        return ret;
    }

    /**
     * Determines whether the Piece on the given Square is threatened
     *
     * @param square Square  The Square to check for being threatened.
     * @return boolean true if Piece is threatened
     */
    private boolean squareIsThreatened(Square square) {
        if (square == null || square.getPiece() == null)
            return false;

        return squareIsThreatened(square, square.getPiece().getPlayer());
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

            HashSet<MoveEffect> validMoveSquares = getValidTargetSquares(sq.getPiece());
            for (MoveEffect it2 : validMoveSquares) {
                chessboardController.apply(it2);
                if ((piece == null && square.getPiece() != null) || (piece != null && chessboardController.getSquare(piece) == null)) {
                    chessboardController.reverse(it2);
                    return true;
                }
                chessboardController.reverse(it2);
            }
        }
        return false;
    }
}
