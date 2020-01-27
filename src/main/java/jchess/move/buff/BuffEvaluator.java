package jchess.move.buff;

import jchess.controller.IChessboardController;
import jchess.controller.IMoveHistoryController;
import jchess.entities.Square;
import jchess.move.effects.MoveEffect;
import jchess.move.IMoveEvaluator;
import jchess.move.effects.MoveEffectsBuilder;

import java.util.HashSet;
import java.util.Random;

public class BuffEvaluator implements IBuffEvaluator {
    private final IChessboardController chessboard;
    private final IMoveHistoryController history;
    private IMoveEvaluator moveEvaluator;

    public BuffEvaluator(IChessboardController controller, IMoveHistoryController history, IMoveEvaluator moveEvaluator) {
        if (controller == null)
            throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
        this.chessboard = controller;

        if (history == null)
            throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
        this.history = history;

        this.moveEvaluator = moveEvaluator;
    }

    public void evaluate() {
        for (Square square : chessboard.getSquares()) {
            if (square == null || square.getPiece() == null)
                continue;

            for (BuffType buff : square.getPiece().getActiveBuffs())
                evaluateBuff(square, buff);

            square.getPiece().tickBuffs();
        }
    }

    private void evaluateBuff(Square square, BuffType buff) {
        if (buff == BuffType.Confusion)
            evaluateConfusion(square);
    }

    private void evaluateConfusion(Square square) {
        HashSet<MoveEffect> mes = moveEvaluator.getValidTargetSquaresToSavePiece(square.getPiece(), chessboard.getCrucialPieces(square.getPiece().getPlayer()));
        MoveEffect temp = (MoveEffect) mes.toArray()[new Random().nextInt(mes.size())];
        MoveEffect randomMove = new MoveEffectsBuilder(temp).setFromMove(false).build();
        chessboard.apply(randomMove);

        history.addMove(randomMove, true, false);
    }
}
