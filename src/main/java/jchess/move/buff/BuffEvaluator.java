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
                            applyBuffEffect(evaluateBuff(square, buff));
            }
            
        for (Square square : chessboard.getSquares())
            if (square != null) {
                Piece piece = square.getPiece();
                if (piece != null)
                    if (piece.getPlayer().equals(activePlayer))
                        piece.tickBuffs();
            }
    }

    private MoveEffect evaluateBuff(Square square, Buff buff) {
        if (buff.getType().equals(BuffType.Confusion))
            return evaluateConfusion(square);
        else if (buff.getType().equals(BuffType.ImminentExplosion))
        	return evaluateImminentExplosion(square, buff);
        
        return null;
    }
    
    private void applyBuffEffect(MoveEffect buffEffect) {
    	if (buffEffect == null)
    		return;
    	
        chessboard.apply(buffEffect);
        history.addMove(buffEffect, true, false);
    }

    private MoveEffect evaluateConfusion(Square square) {
        HashSet<MoveEffect> mes = moveEvaluator.getValidTargetSquaresToSavePiece(square.getPiece(), chessboard.getCrucialPieces(square.getPiece().getPlayer()));
        MoveEffect temp = (MoveEffect) mes.toArray()[new Random().nextInt(mes.size())];
        MoveEffect randomMove = new MoveEffectsBuilder(temp).setFromMove(false).build();
        
        return randomMove;
    }
    
    private MoveEffect evaluateImminentExplosion(Square square, Buff buff) {
    	if (buff.getRemainingTicks() > 1)
    		return null;
    	
    	MoveEffectsBuilder meb = new MoveEffectsBuilder(square.getPiece(), square, square, null, false);

    	final HashSet<Square> squares = chessboard.getModel().getSquaresBetween(chessboard.getSquare(square.getPozX() + 1,  square.getPozY() + 1),
    			chessboard.getSquare(square.getPozX() - 1, square.getPozY() - 1));
    	for (Square sq : squares) 
    		if (sq.getPiece() != null && !chessboard.getCrucialPieces().contains(sq.getPiece()))
    			meb.addPosChange(sq, null);
    	
    	return meb.build();
    }
}
