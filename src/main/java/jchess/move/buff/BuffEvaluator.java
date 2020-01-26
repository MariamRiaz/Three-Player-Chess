package jchess.move.buff;

import jchess.controller.IChessboardController;
import jchess.controller.IMoveHistoryController;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.helper.MoveEvaluator;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.Random;

public class BuffEvaluator {

    private final IChessboardController chessboard;
    private final IMoveHistoryController history;
    private final Player activePlayer;

    public BuffEvaluator(IChessboardController controller, IMoveHistoryController history, Player activePlayer) {
        if (controller == null)
            throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
        this.chessboard = controller;

        if (history == null)
            throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
        this.history = history;

        if (activePlayer == null)
            throw new NullPointerException("Active player cannot be null");
        this.activePlayer = activePlayer;

    }

    public void evaluate() {
        for (Square square : chessboard.getSquares()) {
            if (square != null) {
                Piece piece = square.getPiece();
                if (piece != null) {
                    if (piece.getPlayer().equals(activePlayer)) {
                        for (BuffType buff : square.getPiece().getActiveBuffs())
                            evaluateBuff(square, buff);
                    }
                }
            }
        }
        for (Square square : chessboard.getSquares()) {
            if (square != null) {
                Piece piece = square.getPiece();
                if (piece != null) {
                    if (piece.getPlayer().equals(activePlayer)) {
                        piece.tickBuffs();
                    }
                }
            }
        }
    }

    private void evaluateBuff(Square square, BuffType buff) {
        if (buff == BuffType.Confusion) {
            evaluateConfusion(square);
        }
    }

    private void evaluateConfusion(Square square) {
        HashSet<MoveEffect> mes =
                new MoveEvaluator(chessboard).getValidTargetSquaresToSavePiece(square.getPiece(), chessboard.getCrucialPieces(square.getPiece().getPlayer()));

        MoveEffect temp = (MoveEffect) mes.toArray()[new Random().nextInt(mes.size())];
        MoveEffect randomMove = new MoveEffectsBuilder(temp).setFromMove(false).build();
        chessboard.apply(randomMove);

        history.addMove(randomMove, true, false);
    }
}
