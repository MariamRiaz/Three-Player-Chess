package jchess.move.effects;

import java.util.ArrayList;
import java.util.Collections;

import jchess.JChessApp;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.model.RoundChessboardModel;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;
import jchess.pieces.PieceLoader;
import jchess.view.RoundChessboardView;

public class MoveEffects {
	public final Piece moving;
	public final Square trigger;
	public final Move move;
	public final MoveType flag;
	
	private final ArrayList<PositionChange> positionChanges, pcReverse;
	private final ArrayList<StateChange> stateChanges, scReverse;
	
	protected MoveEffects(Piece moving, Square trigger, Move move, MoveType flag, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			ArrayList<PositionChange> pcReverse, ArrayList<StateChange> scReverse) {
		this.moving = moving;
		this.trigger = trigger;
		this.move = move;
		this.flag = flag;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		
		Collections.reverse(pcReverse);
		Collections.reverse(scReverse);
		
		this.pcReverse = pcReverse;
		this.scReverse = scReverse;
	}
	
    public void apply(RoundChessboardModel model, RoundChessboardView view) {
    	if (model == null)
    		return;
    	
    	for (PositionChange ent : positionChanges) {
    		if (view != null)
    			view.removeVisual(model.getSquare(ent.piece));
    		model.setPieceOnSquare(ent.piece, ent.square);
    		if (view != null)
    			view.setVisual(ent.square.getPiece(), ent.square);
    	}
    	
    	for (StateChange ent : stateChanges) {
    		final Square sq = model.getSquare(ent.id);
    		if (sq == null)
    			continue;

    		if (ent.state.getDefinition() == PieceDefinition.PLACEHOLDER)
    			ent.state.setDefinition(PieceLoader.getPieceDefinition(
    					JChessApp.jcv.showPawnPromotionBox(Player.colorToLetter(ent.state.player.color))));
    		
    		model.setPieceOnSquare(ent.state, sq);
    		if (view != null)
    			view.setVisual(ent.state, sq.getPozX(), sq.getPozY());
    	}
    }
    
    public void reverse(RoundChessboardModel model, RoundChessboardView view) {
    	if (model == null)
    		return;
    	
    	for (StateChange ent : scReverse) {
    		final Square sq = model.getSquare(ent.id);
    		if (sq == null)
    			continue;
    		
    		model.setPieceOnSquare(ent.state, sq);
    		if (view != null)
    			view.setVisual(ent.state, sq.getPozX(), sq.getPozY());
    	}
    	
    	for (PositionChange ent : pcReverse) {
    		if (view != null)
    			view.removeVisual(model.getSquare(ent.piece));
    		model.setPieceOnSquare(ent.piece, ent.square);
    		if (view != null)
    			view.setVisual(ent.square.getPiece(), ent.square);
    	}
    }
}
