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

public class MoveEffect {
	private final Piece moving;
	private final Square trigger, from;
	private final Move move;
	private final MoveType flag;
	
	private final ArrayList<PositionChange> positionChanges, pcReverse;
	private final ArrayList<StateChange> stateChanges, scReverse;
	
	protected MoveEffect(Piece moving, Square trigger, Square from, Move move, MoveType flag, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			ArrayList<PositionChange> pcReverse, ArrayList<StateChange> scReverse) {
		this.moving = moving;
		this.trigger = trigger;
		this.from = from;
		this.move = move;
		this.flag = flag;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		
		Collections.reverse(pcReverse);
		Collections.reverse(scReverse);
		
		this.pcReverse = pcReverse;
		this.scReverse = scReverse;
	}
	
	/**
	 * @return The Piece initiating this MoveEffect.
	 */
	public Piece getMoving() {
		return moving;
	}
	
	/**
	 * @return The Square, which was clicked to initiate this MoveEffect.
	 */
	public Square getTrigger() {
		return trigger;
	}
	
	/**
	 * @return The Square, on which the Piece initiating the move was located.
	 */
	public Square getFrom() {
		return from;
	}
	
	/**
	 * @return The played Move.
	 */
	public Move getMove() {
		return move;
	}
	
	/**
	 * @return The leading MoveType of the Move, e.g. Castling, if this Move has numerous MoveTypes in its conditions that define it, such as Castling and OnlyMove.
	 */
	public MoveType getFlag() {
		return flag;
	}
	
	/**
	 * Applies this MoveEffect to the given board model and view: moves Pieces and changes their states.
	 * @param model The board model.
	 * @param view The board view.
	 */
    public void apply(RoundChessboardModel model, RoundChessboardView view) {
    	if (model == null)
    		return;
    	
    	for (PositionChange ent : positionChanges) {
    		if (view != null)
    			view.removeVisual(model.getSquare(ent.getPiece()));
    		model.setPieceOnSquare(ent.getPiece(), ent.getSquare());
    		if (view != null)
    			view.setVisual(ent.getSquare().getPiece(), ent.getSquare());
    	}
    	
    	for (StateChange ent : stateChanges) {
    		final Square sq = model.getSquare(ent.getID());
    		if (sq == null)
    			continue;

    		if (ent.getState().getDefinition() == PieceDefinition.PLACEHOLDER)
    			ent.getState().setDefinition(PieceLoader.getPieceDefinition(
    					JChessApp.jcv.showPawnPromotionBox(Player.colorToLetter(ent.getState().getPlayer().color))));
    		
    		model.setPieceOnSquare(ent.getState(), sq);
    		if (view != null)
    			view.setVisual(ent.getState(), sq.getPozX(), sq.getPozY());
    	}
    }
    
    /**
     * Reverses this MoveEffect on the given board model and view: moves Pieces back and returns them to their previous states. 
     * Assumes this MoveEffect is the last one that was applied to the given board. Behavior undefined otherwise.
     * @param model The board model.
     * @param view The board view.
     */
    public void reverse(RoundChessboardModel model, RoundChessboardView view) {
    	if (model == null)
    		return;
    	
    	for (StateChange ent : scReverse) {
    		final Square sq = model.getSquare(ent.getID());
    		if (sq == null)
    			continue;
    		
    		model.setPieceOnSquare(ent.getState(), sq);
    		if (view != null)
    			view.setVisual(ent.getState(), sq.getPozX(), sq.getPozY());
    	}
    	
    	for (PositionChange ent : pcReverse) {
    		if (view != null)
    			view.removeVisual(model.getSquare(ent.getPiece()));
    		model.setPieceOnSquare(ent.getPiece(), ent.getSquare());
    		if (view != null)
    			view.setVisual(ent.getSquare().getPiece(), ent.getSquare());
    	}
    }
}
