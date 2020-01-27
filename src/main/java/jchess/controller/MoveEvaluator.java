package jchess.controller;

import jchess.controller.RoundChessboardController;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.helper.IMoveEvaluator;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.move.Orientation;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Class that contains method to evaluate if a move is valid and gets possible target Squares
 */
public class MoveEvaluator implements IMoveEvaluator {
    private RoundChessboardController chessboard;

    /**
     * Constructor
     *
     * @param model RoundChessboardModel    the model of the chessboard
     */
    public MoveEvaluator(RoundChessboardController chessboard) {
        this.chessboard = chessboard;
    }

    /**
     * Retrieves a HashSet of the valid Squares, to which a Piece on the given Square can move, disregarding the constraint to keep certain Pieces non-threatened.
     *
     * @param piece The Piece to be moved.
     * @return The valid target Squares for the Piece on the given Square.
     */
    private HashSet<MoveEffect> getValidTargetSquares(Piece piece) {
        HashSet<MoveEffect> ret = new HashSet<>();

        if (piece == null)
            return ret;//TODO error handling

        HashSet<Move> moves = piece.getDefinition().getMoves();
        for (Move it : moves)
            ret.addAll(evaluateMoveToTargetSquares(it, piece));

        return ret;
    }

    /**
     * Evaluates which of the potential Squares are actually possible to move to with the current board state, the given Move definition instance and the given Piece.
     *
     * @param move  The Move definition by which to select potential target Squares.
     * @param piece The Piece to be moved.
     * @return A HashSet of the concrete Squares to which the Piece can move with the given Move definition.
     */
    private HashSet<MoveEffect> evaluateMoveToTargetSquares(Move move, Piece piece) {
        HashSet<MoveEffect> ret = new HashSet<>();

        if (move == null || piece == null)
            return ret;//TODO error handling

        int count = 0;
        HashSet<Square> traversed = new HashSet<Square>();
        Orientation otn = piece.getOrientation().clone();
        final Square from = chessboard.getSquare(piece);

        for (Square next = nextSquare(from, move.getX(), move.getY(), otn);
             next != null && (move.getLimit() == null || count < move.getLimit()) && !traversed.contains(next);
             next = nextSquare(next, move.getX(), move.getY(), otn)) {

            MoveEffectsBuilder meb = new MoveEffectsBuilder(piece, next, from, move, true);
            meb = evaluateAndGenEffects(move, next, piece, meb);

            if (meb != null) {
                if (meb.isEmpty())
                    meb.addPosChange(chessboard.getSquare(piece), next)
                            .addStateChange(piece, piece.clone().setHasMoved(true).reorient(otn.clone()));

                if (piece.getDefinition().getType().equals("Pawn") && chessboard.getModel().isInPromotionArea(next))
                    meb.addStateChange(piece, piece.clone().setDefinition(PieceDefinition.PLACEHOLDER));

                ret.add(meb.build());
                traversed.add(next);
            }

            if (!move.getConditions().contains(MoveType.Unblockable) && next.getPiece() != null)
                break;

            count++;
        }

        return ret;
    }

    private MoveEffectsBuilder evaluateCastling(Move move, Square next, Piece piece, MoveEffectsBuilder meb) {
        if (piece.hasMoved())
            return null;
        else {
            Square rook = null, nextRook = null;
            if (move.getY() > 0) {
                rook = chessboard.getSquare(next.getPozX(), next.getPozY() + 1);
                nextRook = chessboard.getSquare(next.getPozX(), next.getPozY() - 1);
            } else {
                rook = chessboard.getSquare(next.getPozX(), next.getPozY() - 1);
                nextRook = chessboard.getSquare(next.getPozX(), next.getPozY() + 1);
            }

            if (rook.getPiece() == null || rook.getPiece().hasMoved())
                return null;

            for (Square sq : chessboard.getModel().getSquaresBetween(chessboard.getSquare(piece), rook))
                if (squareIsThreatened(sq, piece.getPlayer()))
                    return null;

            return meb.addPosChange(chessboard.getSquare(piece), next)
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

            if (chessboard.getModel().getInnerRimConnected() && current.getPozX() + x < 0)
                orientation.reverseX();
        }

        Square retVal = chessboard.getSquare(current.getPozX() + x, current.getPozY() + y);

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
        for (Square sq : chessboard.getSquares()) {
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

            chessboard.apply(me);
            boolean rm = false;
            for (Piece piece : toSave)
                if (squareIsThreatened(chessboard.getSquare(piece))) {
                    rm = true;
                    break;
                }
            chessboard.reverse(me);

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
        for (Square sq : chessboard.getSquares()) {
            if (sq.getPiece() == null || sq.getPiece().getPlayer() == player)
                continue;

            HashSet<MoveEffect> validMoveSquares = getValidTargetSquares(sq.getPiece());
            for (MoveEffect it2 : validMoveSquares) {
                chessboard.apply(it2);
                if ((piece == null && square.getPiece() != null) || (piece != null && chessboard.getSquare(piece) == null)) {
                    chessboard.reverse(it2);
                    return true;
                }
                chessboard.reverse(it2);
            }
        }
        return false;
    }
}
