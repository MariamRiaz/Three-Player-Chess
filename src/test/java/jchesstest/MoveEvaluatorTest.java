package jchesstest;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.HashSet;

import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.model.RoundChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.player.Player;
import jchess.move.MoveDefinition;
import jchess.move.MoveEvaluator;
import jchess.move.MoveType;
import jchess.move.Orientation;
import jchess.move.effects.BoardTransition;
import jchess.pieces.Piece;
import jchess.pieces.PieceDefinition;

public class MoveEvaluatorTest {
	private RoundChessboardController rcc;
	private RoundChessboardModel rcm;
	
	private MoveEvaluator moveEvaluator;

	/**
	 * Test whether the MoveEvaluator correctly generates the corresponding BoardTransition objects when a Piece can attack a Piece in front of it.
	 */
	@Test
	public void testAttackMoveEvaluation() {
		//Arrange
		generate4by1AttackerDefenderScenario(0, 3);

		//Act
		HashSet<BoardTransition> moves = moveEvaluator.getPieceTargetToSavePieces(rcc.getSquare(0, 0).getPiece(), null);

		//Assert
		assertEquals(1, moves.size()); // assertions
		assertNotNull(moves.iterator().next());
		assertNotNull(moves.iterator().next().getMoveHistoryEntry());
		assertEquals(new Square(0, 0, null), moves.iterator().next().getMoveHistoryEntry().getFromSquare());
		assertEquals(new Square(3, 0, null), moves.iterator().next().getMoveHistoryEntry().getToSquare());
	}
	
	/**
	 * Test whether a Piece can move forward given another Piece in front of it.
	 */
	@Test
	public void testNonAttackMoveEvaluation() {
		//Arrange
		generate4by1AttackerDefenderScenario(1, 2);

		//Act
		HashSet<BoardTransition> moves = moveEvaluator.getPieceTargetToSavePieces(rcc.getSquare(2, 0).getPiece(), null);

		//Assert
		assertEquals(0, moves.size()); // assertions
	}
	
	/**
	 * Test whether a Piece can move past another Piece that is blocking its way a number of Squares away from it.
	 */
	@Test
	public void testMoveBlockEvaluation() {
		//Arrange
		generate4by1AttackerDefenderScenario(1, 3);

		//Act
		HashSet<BoardTransition> moves = moveEvaluator.getPieceTargetToSavePieces(rcc.getSquare(3, 0).getPiece(), null);

		//Assert
		assertEquals(1,  moves.size()); // assertions
		
		for (BoardTransition sq : moves) {
			assertNotNull(sq.getMoveHistoryEntry());
			assertNotEquals(new Square(0, 0, null), sq.getMoveHistoryEntry().getToSquare());
		}
	}
	
	/**
	 * Tests whether Castling is properly evaluated and the proper amount of position changes are generated.
	 */
	@Test
	public void testCastlingEvaluation() {
		//Arrange
		generateCastlingScenario();

		//Act
		HashSet<BoardTransition> moves = moveEvaluator.getPieceTargetToSavePieces(rcc.getSquare(0, 0).getPiece(), null);

		//Assert
		assertEquals(1, moves.size()); // assertions
		assertNotNull(moves.iterator().next());
		assertNotNull(moves.iterator().next().getMoveHistoryEntry());
		assertEquals(new Square(0, 0, null), moves.iterator().next().getMoveHistoryEntry().getFromSquare());
		assertEquals(new Square(0, 2, null), moves.iterator().next().getMoveHistoryEntry().getToSquare());
		assertEquals(2, moves.iterator().next().getPositionChanges().size());
	}
	
	private void generate4by1AttackerDefenderScenario(int attackerX, int defenderX) {
		mockNbyMBoard(4, 1);
		moveEvaluator = new MoveEvaluator(rcc);
		
		Piece attacker = mockDefaultPieceWithOneMove(1, 0, MoveType.OnlyAttack, mock(Player.class)),
				defender = mockDefaultPieceWithOneMove(-1, 0, MoveType.OnlyMove, mock(Player.class));
		setPieceOnSquare(attacker, attackerX, 0);
		setPieceOnSquare(defender, defenderX, 0); // setup ends
	}
	
	private void generateCastlingScenario() {
		mockNbyMBoard(1, 4);
		moveEvaluator = new MoveEvaluator(rcc);
		
		Player playerMock = mock(Player.class);
		Piece king = mockDefaultPieceWithOneMove(0, 2, MoveType.Castling, playerMock),
				rook = mockDefaultPieceWithOneMove(0, 0, null, playerMock);
		
		setPieceOnSquare(king, 0, 0);
		setPieceOnSquare(rook, 0, 3);
	}
	
	private void mockNbyMBoard(int rows, int columns) {
		rcc = mock(RoundChessboardController.class);
		rcm = mock(RoundChessboardModel.class);
		
		ArrayList<Square> squares = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Square sq = new Square(i, j, null);
				
				squares.add(sq);
				when(rcm.getSquare(i, j)).thenReturn(sq);
				when(rcc.getSquare(i, j)).thenReturn(sq);
			}
		}
		
		when(rcc.getSquares()).thenReturn(squares);

		when(rcc.getModel()).thenReturn(rcm);
		when(rcm.getInnerRimConnected()).thenReturn(false);
		when(rcm.isInPromotionArea(any(Square.class))).thenReturn(false);
		when(rcm.getSquaresBetween(any(Square.class), any(Square.class))).thenCallRealMethod();
	}
	
	private Piece mockDefaultPieceWithOneMove(int x, int y, MoveType moveType, Player piecePlayer) {
		PieceDefinition mockDef = mock(PieceDefinition.class);
		MoveDefinition mockMove = mock(MoveDefinition.class);

		when(mockMove.getX()).thenReturn(x);
		when(mockMove.getY()).thenReturn(y);
		when(mockMove.getLimit()).thenReturn(null);
		when(mockMove.getConditions()).thenReturn(new HashSet<MoveType>() {{ add(moveType); }});
		
		when(mockDef.getMoves()).thenReturn(new HashSet<MoveDefinition>() {{ add(mockMove); }});
		when(mockDef.getType()).thenReturn("");
		
		return new Piece(mockDef, piecePlayer, new Orientation());
	}
	
	private void setPieceOnSquare(Piece piece, int x, int y) {
		rcc.getSquare(x, y).setPiece(piece);
		Square sq = rcc.getSquare(x, y);
		when(rcc.getSquare(piece)).thenReturn(sq);
	}
}
