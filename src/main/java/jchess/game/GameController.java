package jchess.game;

import jchess.game.chessboard.RoundChessboardLoader;
import jchess.game.chessboard.controller.IChessboardController;
import jchess.game.chessboard.controller.RoundChessboardController;
import jchess.game.chessboard.model.Square;
import jchess.game.clock.GameClock;
import jchess.game.clock.IGameClock;
import jchess.game.history.IMoveHistoryController;
import jchess.game.history.MoveHistoryController;
import jchess.game.player.Player;
import jchess.logging.Log;
import jchess.move.IMoveEvaluator;
import jchess.move.MoveEvaluator;
import jchess.move.buff.BuffEvaluator;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.Observable;
import javax.swing.*;

/**
 * Class that represents the Controller of the Game Component,
 * which includes a Chessboard, MoveHistory and GameClock Component.
 */
public class GameController implements IGameController {

    private AbstractGameView gameView;

    private IGameModel gameModel;
    private IChessboardController chessboardController;
    private IMoveHistoryController moveHistoryController;
    private IGameClock gameClock;
    private RoundChessboardLoader chessboardLoader;
    private IMoveEvaluator moveEvaluator;

    /**
     * Constructor of GameController Class.
     */
    public GameController() {

        gameModel = new GameModel();
        chessboardLoader = new RoundChessboardLoader();
        initializeControllers();
        gameView = new GameView(
                this,
                chessboardController.getView(),
                moveHistoryController.getScrollPane(),
                gameClock.getGameClockView());
        gameModel.setBlockedChessboard(false);
        gameModel.setActivePlayer(gameModel.getPlayerWhite());
    }

    private void initializeControllers() {
        moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());
        int chessboardSize = 800;
        chessboardController = new RoundChessboardController(
                chessboardLoader,
                chessboardSize, this.gameModel,
                this.moveHistoryController);
        gameClock = new GameClock(this);
        moveEvaluator = new MoveEvaluator((RoundChessboardController) chessboardController);
        chessboardController.addSelectSquareObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    public IGameModel getGameModel() {
        return gameModel;
    }

    /**
     * {@inheritDoc}
     */
    public IGameClock getGameClock() {
        return gameClock;
    }

    /**
     * {@inheritDoc}
     */
    public void endGame(String message) {
        gameModel.setBlockedChessboard(true);
        Log.log(message);
        JOptionPane.showMessageDialog(null, message);
    }

    private void switchActive(boolean forward) {
        if (forward) {
            gameModel.setActivePlayer(getNext());
        } else {
            gameModel.setActivePlayer(getPrevious());
        }

        this.gameModel.setBlockedChessboard(false);
        this.gameClock.switchPlayers(forward);
        this.moveHistoryController.switchColumns(forward);
    }

    private Player getNext() {
        if (gameModel.getActivePlayer() == gameModel.getPlayerWhite()) {
            return gameModel.getPlayerBlack();
        }
        if (gameModel.getActivePlayer() == gameModel.getPlayerBlack()) {
            return gameModel.getPlayerGray();
        }
        if (gameModel.getActivePlayer() == gameModel.getPlayerGray()) {
            return gameModel.getPlayerWhite();
        }
        return null;
    }

    private Player getPrevious() {
        if (gameModel.getActivePlayer() == gameModel.getPlayerWhite()) {
            return gameModel.getPlayerGray();
        }
        if (gameModel.getActivePlayer() == gameModel.getPlayerBlack()) {
            return gameModel.getPlayerWhite();
        }
        if (gameModel.getActivePlayer() == gameModel.getPlayerGray()) {
            return gameModel.getPlayerBlack();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean undo() {
        boolean status;

        status = chessboardController.undo();
        if (status) {
            this.switchActive(false);
        }
        return status;
    }

    /**
     * {@inheritDoc}
     */
    public boolean rewindToBegin() {
        boolean result = false;

        while (chessboardController.undo()) {
            result = true;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean rewindToEnd() throws UnsupportedOperationException {
        boolean result = false;

        while (chessboardController.redo()) {
            result = true;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean redo() {
        boolean status = chessboardController.redo();
        if (status) {
            switchActive(true);
        }

        return status;
    }

    private void selectedSquare(Square square) {
        if (gameModel.isBlockedChessboard()) {
            return;
        }
        if (square == null) {
            return;
        }
        if (chessboardController.getActiveSquare() != null) {
            if (didSelectPossibleMove(square)) {
                chessboardController.move(chessboardController.getActiveSquare(), square, true, true);
                chessboardController.unselect();
                applyBuffs();

                switchActive(true);
                if (isCheckMate()) {
                    this.endGame("Checkmate! " + gameModel.getActivePlayer().getName() + " player won!");
                }
            } else if (didSelectOwnPiece(square)) {
                chessboardController.unselect();
                chessboardController.select(square);
            }
        } else if (didSelectOwnPiece(square)) {
            chessboardController.select(square);
        }
    }

    private void applyBuffs() {
        BuffEvaluator evaluator = new BuffEvaluator(
                chessboardController,
                moveHistoryController,
                moveEvaluator,
                getNext());
        evaluator.evaluate();
    }

    private boolean isCheckMate() {
        HashSet<Piece> crucialPieces = chessboardController.getCrucialPieces(gameModel.getActivePlayer());
        return crucialPieces.stream().anyMatch(p -> chessboardController.pieceIsUnsavable(p));
    }

    private boolean didSelectOwnPiece(Square square) {
        if (square.getPiece() == null) {
            return false;
        }
        return square.getPiece().getPlayer().equals(gameModel.getActivePlayer());
    }

    private boolean didSelectPossibleMove(Square square) {
        return chessboardController.moveIsPossible(chessboardController.getActiveSquare(), square);
    }

    /**
     * {@inheritDoc}
     */
    public void update(Observable o, Object arg) {
        Square square = (Square) arg;
        selectedSquare(square);
    }

    /**
     * getter for the View of the Game Component.
     *
     * @return View of the Game Component
     */
    public AbstractGameView getView() {
        return this.gameView;
    }
}
