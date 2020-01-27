package jchess.move.buff;

import jchess.game.chessboard.controller.IChessboardController;
import jchess.game.chessboard.model.Square;
import jchess.game.history.IMoveHistoryController;
import jchess.game.player.Player;
import jchess.move.IMoveEvaluator;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.Random;

public class BuffEvaluator implements IBuffEvaluator {
    private final IChessboardController chessboard;
    private final IMoveHistoryController history;
    private final Player activePlayer;
    private IMoveEvaluator moveEvaluator;

    public BuffEvaluator(IChessboardController controller, IMoveHistoryController history, IMoveEvaluator moveEvaluator, Player activePlayer) {
        this.chessboard = controller;
        this.history = history;
        this.activePlayer = activePlayer;
        this.moveEvaluator = moveEvaluator;
    }

    public void evaluate() {
        for (Square square : chessboard.getSquares())
            if (square != null) {
                Piece piece = square.getPiece();
                if (piece != null)
                    if (piece.getPlayer().equals(activePlayer))
                        for (Buff buff : square.getPiece().getActiveBuffs())
                            evaluateBuff(square, buff);
            }
            
        for (Square square : chessboard.getSquares())
            if (square != null) {
                Piece piece = square.getPiece();
                if (piece != null)
                    if (piece.getPlayer().equals(activePlayer))
                        piece.tickBuffs();
            }
    }

    private void evaluateBuff(Square square, Buff buff) {
        if (buff.getType().equals(BuffType.Confusion))
            evaluateConfusion(square);
        else if (buff.getType().equals(BuffType.ImminentExplosion))
        	evaluateImminentExplosion(square, buff);
    }

    private void evaluateConfusion(Square square) {
        HashSet<MoveEffect> mes = moveEvaluator.getValidTargetSquaresToSavePiece(square.getPiece(), chessboard.getCrucialPieces(square.getPiece().getPlayer()));
        MoveEffect temp = (MoveEffect) mes.toArray()[new Random().nextInt(mes.size())];
        MoveEffect randomMove = new MoveEffectsBuilder(temp).setFromMove(false).build();
        chessboard.apply(randomMove);

        history.addMove(randomMove, true, false);
    }
    
    private void evaluateImminentExplosion(Square square, Buff buff) {
    	if (buff.getRemainingTicks() > 1)
    		return;
    	
    }
}
