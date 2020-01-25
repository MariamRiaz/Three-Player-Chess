package jchess.move.buff;

import java.util.List;

import jchess.controller.MoveHistory;
import jchess.entities.Square;
import jchess.model.RoundChessboardModel;

public class BuffEvaluator {
	private final RoundChessboardModel model;
	private final MoveHistory history;
	
	public BuffEvaluator(RoundChessboardModel model, MoveHistory history) {
		if (model == null)
			throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
		this.model = model;
		
		if (history == null)
			throw new NullPointerException("'model' of BuffEvaluator cannot be null.");
		this.history = history;
	}
	
	public void evaluate(MoveHistory history) {
		for (Square square : model.getSquares()) {
			if (square == null || square.getPiece() == null)
				continue;
			
			List<BuffType> buffs = square.getPiece().getActiveBuffs();
			
			
			
			square.getPiece().tickBuffs();
		}
	}
}
