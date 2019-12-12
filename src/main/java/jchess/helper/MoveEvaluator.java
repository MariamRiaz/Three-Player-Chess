package jchess.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jchess.Player;
import jchess.UI.board.Square;
import jchess.model.RoundChessboardModel;
import jchess.pieces.Piece;
import jchess.pieces.PlayedMove;
import jchess.pieces.Piece.Move.MoveType;

public class MoveEvaluator {
	private RoundChessboardModel model;
	
	public MoveEvaluator (RoundChessboardModel model) {
		this.model = model;
	}
	
    private HashSet<Square> getValidTargetSquares(Square square) {
        HashSet<Square> ret = new HashSet<>();

        if (square == null || square.getPiece() == null)
            return ret;//TODO error handling

        HashSet<Piece.Move> moves = square.getPiece().getMoves();
        for (Piece.Move it : moves)
            ret.addAll(evaluateMoveToTargetSquares(it, square));

        return ret;
    }

    private HashSet<Square> evaluateMoveToTargetSquares(Piece.Move move, Square square) {
    	HashSet<Square> ret = new HashSet<Square>();
		
		if (move == null || square == null || square.getPiece() == null)
			return ret;
		
		int count = 0;
		for (Square next = nextSquare(square, move.x, move.y); next != null
				&& (move.limit == null || count < move.limit); next = nextSquare(next, move.x, move.y)) {
			boolean add = true;
			
			if (move.conditions.contains(MoveType.OnlyAttack)) {
				if (next.getPiece() == null || next.getPiece().player == square.getPiece().player)
					add = false;
			} else if (move.conditions.contains(MoveType.OnlyMove)) {
				if (next.getPiece() != null)
					add = false;
			} else if (next.getPiece() != null && next.getPiece().player == square.getPiece().player)
				add = false;
			
			if (move.conditions.contains(MoveType.OnlyWhenFresh) && square.getPiece().hasMoved())
				add = false;

			if (move.conditions.contains(MoveType.EnPassant)) {
				add = false;
				
				Piece target = getPawnSkippedOverSquareEnPassant(next);
				if (target != null && target.player != square.getPiece().player)
					add = true;
			}
			
			if (move.conditions.contains(MoveType.Castling)) {
				Square current = square;
				
				if (squareIsThreatened(current, square.getPiece().player, getPiecesThatMustNotDie()))
					add = false;
				else {
					int start = move.x < 0 ? 0 : current.getPozX() + 1,
							end = move.x < 0 ? current.getPozX() : model.getColumns();
					
					Piece rk = model.getSquare(move.x < 0 ? 0 : end - 1, current.getPozY()).getPiece();
					if (rk == null || rk.hasMoved())
						add = false;
					else for (; start < end; start++) {
							Square sq = model.getSquare(start, current.getPozY());
							if ((sq.getPiece() != null && sq.getPiece() != rk) || squareIsThreatened(sq, square.getPiece().player,
									getPiecesThatMustNotDie())) {
								add = false;
								break;
							}
					}
				}
			}
			
			if (add)
				ret.add(next);
			
			if (!move.conditions.contains(MoveType.Unblockable) && next.getPiece() != null)
				break;
			
			count++;
		}
		
		return ret;
    }

    private Square nextSquare(Square current, int x, int y) {
        return model.getSquare(current.getPozX() + x, current.getPozY() + y);
    }


    public boolean squareUnsavable(Square square) {
        if (square == null || square.getPiece() == null)
            return false;
        for (Square sq : model.squares) {
            if (sq.getPiece() == null || sq.getPiece().player != square.getPiece().player)
                continue;
			
            if (!getValidTargetSquaresToSavePiece(sq, square.getPiece().player, square.getPiece()).isEmpty())
                return false;
        }
        return true;
    }
	
    public HashSet<Square> getValidTargetSquaresToSavePiece(Square moving, Player player, Piece... toSave) {
        HashSet<Square> ret = getValidTargetSquares(moving);
        if (ret.size() == 0 || toSave == null)
            return ret;
		
        for (Iterator<Square> it = ret.iterator(); it.hasNext(); ) {
            Square target = it.next(), start = moving;
            Piece old = target.getPiece();
			
            model.setPieceOnSquare(moving.getPiece(), target);
            for (Piece pc : toSave)
            	if (squareIsThreatened(model.getSquare(pc), player))
                    it.remove();

            model.setPieceOnSquare(old, target);
            model.setPieceOnSquare(moving.getPiece(), start);
        }

        return ret;
    }

    public boolean squareIsThreatened(Square square, Player player, Piece... exclude) {
        if (square == null)
			return false;
		
		java.util.List<Piece> exclusionList = Arrays.asList(exclude);
		
		for (Square el : model.squares) {
            if (el.getPiece() == null || el.getPiece().player == player)
                continue;
			
			if (exclusionList.contains(el.getPiece()) || el.getPiece().player == player)
				continue;

			HashSet<Square> validMoveSquares = getValidTargetSquares(el);
			for (Iterator<Square> it2 = validMoveSquares.iterator(); it2.hasNext();)
				if (it2.next() == square)
					return true;
		}

		return false;
    }

    private List<Piece> getLastPlayedPieces() {
		List<Piece> lastPlayed = new ArrayList<Piece>();
		for (int i = 0; i < model.getColumns(); i++)
			for (int j = 0; j < model.getRows(); j++) {
				Square sq = model.getSquare(i, j);
				if (sq != null && sq.getPiece() != null && sq.getPiece().getWasPlayedLast())
					lastPlayed.add(sq.getPiece());
			}
		return lastPlayed;
    }
    
    private Piece[] getPiecesThatMustNotDie() {
		Piece[] mustNotDie = new Piece[3]; // TODO magic number
		int ind = 0;
		for (int i = 0; i < model.getColumns(); i++)
			for (int j = 0; j < model.getRows(); j++) {
				Square sq = model.getSquare(i, j);
				if (sq != null && sq.getPiece() != null && sq.getPiece().mustNotDie())
					mustNotDie[ind++] = sq.getPiece();
			}
		return mustNotDie;
    }
    
	public Piece getPawnSkippedOverSquareEnPassant(Square square) {
		List<Piece> lastPlayed = getLastPlayedPieces();

		for (Piece piece : lastPlayed) {
			Square to = model.getSquare(piece), from = model.getSquare(to.getPozX() - piece.getLastMove().x, to.getPozY() - piece.getLastMove().y);
			if (piece.getLastMove().conditions.contains(Piece.Move.MoveType.EnPassant) &&
					square == model.getSquare(to.getPozX(), from.getPozY() + (to.getPozY() - from.getPozY()) / 2))
				return piece;
		}
		return null;
	}
}
