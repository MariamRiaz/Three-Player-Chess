package jchess.move.buff;

import jchess.game.chessboard.controller.IChessboardController;
import jchess.game.chessboard.model.Square;
import jchess.game.history.IMoveHistoryController;
import jchess.game.player.Player;
import jchess.move.IMoveEvaluator;
import jchess.move.effects.BoardTransition;
import jchess.move.effects.BoardTransitionBuilder;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.Random;

public class BuffEvaluator implements IBuffEvaluator {
    private final IChessboardController chessboard;
    private final IMoveHistoryController history;
    private final Player activePlayer;
    private IMoveEvaluator moveEvaluator;

    /**
     * Constructor.
     * @param controller The chessboard controller.
     * @param history The controller of the game's history.
     * @param moveEvaluator The move evaluator to use.
     * @param activePlayer The currently active Player.
     */
    public BuffEvaluator(IChessboardController controller, IMoveHistoryController history, IMoveEvaluator moveEvaluator, Player activePlayer) {
        this.chessboard = controller;
        this.history = history;
        this.activePlayer = activePlayer;
        this.moveEvaluator = moveEvaluator;
    }

    /**
     * Evaluates all buffs on the currently active Player's Pieces, triggers their effects, and removes expiring buffs.
     */
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

    private BoardTransition evaluateBuff(Square square, Buff buff) {
        if (buff.getType().equals(BuffType.Confusion))
            return evaluateConfusion(square);
        else if (buff.getType().equals(BuffType.ImminentExplosion))
        	return evaluateImminentExplosion(square, buff);
        
        return null;
    }
    
    private void applyBuffEffect(BoardTransition buffEffect) {
    	if (buffEffect == null)
    		return;
    	
        chessboard.applyBoardTransition(buffEffect);
        history.addMove(buffEffect);
    }

    private BoardTransition evaluateConfusion(Square square) {
        HashSet<BoardTransition> mes = moveEvaluator.getPieceTargetToSavePieces(square.getPiece(), chessboard.getCrucialPieces(square.getPiece().getPlayer()));
        
        if (mes.size() == 0)
        	return null;
        
        BoardTransition temp = (BoardTransition) mes.toArray()[new Random().nextInt(mes.size())];
        BoardTransition randomMove = new BoardTransitionBuilder(temp).setMoveHistoryEntry(null).build();
        
        return randomMove;
    }
    
    private BoardTransition evaluateImminentExplosion(Square square, Buff buff) {
    	if (buff.getRemainingTicks() > 1)
    		return null;
    	
    	BoardTransitionBuilder meb = new BoardTransitionBuilder();

    	final HashSet<Square> squares = chessboard.getModel().getSquaresBetween(chessboard.getSquare(square.getPozX() + 1,  square.getPozY() + 1),
    			chessboard.getSquare(square.getPozX() - 1, square.getPozY() - 1));
    	for (Square sq : squares) 
    		if (sq.getPiece() != null && !chessboard.getCrucialPieces().contains(sq.getPiece()))
    			meb.addPosChange(sq, null);
    	
    	return meb.build();
    }
}
