package jchess.move.buff;

import java.util.HashSet;
import java.util.Random;

import jchess.controller.MoveHistoryController;
import jchess.entities.Square;
import jchess.helper.MoveEvaluator;
import jchess.model.RoundChessboardModel;
import jchess.move.effects.MoveEffect;
import jchess.move.effects.MoveEffectsBuilder;
import jchess.view.RoundChessboardView;

public class BuffEvaluator {
	private final RoundChessboardModel model;
	private final RoundChessboardView view;
	private final MoveHistoryController history;
	
	public BuffEvaluator(RoundChessboardModel model, RoundChessboardView view, MoveHistoryController history) {
		if (model == null)
			throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
		this.model = model;
		
		if (view == null)
			throw new NullPointerException("'view' of BuffEvaluator cannot be null.");
		this.view = view;
		
		if (history == null)
			throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
		this.history = history;
	}
	
	public void evaluate() {
		for (Square square : model.getSquares()) {
			if (square == null || square.getPiece() == null)
				continue;
			
			for (BuffType buff : square.getPiece().getActiveBuffs())
				evaluateBuff(square, buff);
			
			square.getPiece().tickBuffs();
		}
		
		view.updateAfterMove();
	}
	
	private void evaluateBuff(Square square, BuffType buff) {
		if (buff == BuffType.Confusion)
			evaluateConfusion(square);
	}
	
	private void evaluateConfusion(Square square) {
		HashSet<MoveEffect> mes = 
				new MoveEvaluator(model).getValidTargetSquaresToSavePiece(square.getPiece(), model.getCrucialPieces(square.getPiece().getPlayer()));
		
		MoveEffect temp = (MoveEffect) mes.toArray()[new Random().nextInt(mes.size())];
		MoveEffect randomMove = new MoveEffectsBuilder(temp).setFromMove(false).build();
		randomMove.apply(model, view);
		
		history.addMove(randomMove, true, false);
	}
}
