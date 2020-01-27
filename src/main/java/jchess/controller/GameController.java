/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.controller;

import jchess.entities.Square;
import jchess.helper.Log;
import jchess.helper.RoundChessboardLoader;
import jchess.model.GameModel;
import jchess.model.IGameModel;
import jchess.move.buff.BuffEvaluator;
import jchess.pieces.Piece;
import jchess.view.AbstractGameView;
import jchess.view.GameView;

import javax.swing.*;
import java.util.HashSet;
import java.util.Observable;

/**
 * Class that represents a chess game. It is responsible for starting and ending games
 * and for storing the currently active player.
 * It also provides functionality for saving and loading games.
 */
public class GameController implements IGameController {

    private AbstractGameView gameView;

    private IGameModel gameModel;
    private IChessboardController chessboardController;
    private IMoveHistoryController moveHistoryController;
    private IGameClock gameClock;
    private final int chessboardSize = 800;
    private RoundChessboardLoader chessboardLoader;


    public GameController() {

        gameModel = new GameModel();
        chessboardLoader = new RoundChessboardLoader();
        initializeControllers();
        gameView = new GameView(this, chessboardController.getView(), moveHistoryController.getScrollPane(), gameClock.getGameClockView());
        gameModel.setBlockedChessboard(false);
        gameModel.setActivePlayer(gameModel.getPlayerWhite());
    }

    private void initializeControllers() {
        moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());
        chessboardController = new RoundChessboardController(chessboardLoader, chessboardSize, this.gameModel, this.moveHistoryController);
        gameClock = new GameClock(this);
        chessboardController.addSelectSquareObserver(this);
    }

    public IGameModel getGameModel() {
        return gameModel;
    }

    public IGameClock getGameClock() {
        return gameClock;
    }

    /**
     * Method to end game
     *
     * @param message The message that is shown at the end of the game to each player
     */
    public void endGame(String message) {
        gameModel.setBlockedChessboard(true);
        Log.log(message);
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Method to switch active players after move
     */
    private void switchActive(boolean forward) {
        if (forward) {
            if (gameModel.getActivePlayer() == gameModel.getPlayerWhite())
                gameModel.setActivePlayer(gameModel.getPlayerBlack());
            else if (gameModel.getActivePlayer() == gameModel.getPlayerBlack())
                gameModel.setActivePlayer(gameModel.getPlayerGray());
            else if (gameModel.getActivePlayer() == gameModel.getPlayerGray())
                gameModel.setActivePlayer(gameModel.getPlayerWhite());

        } else {
            if (gameModel.getActivePlayer() == gameModel.getPlayerWhite())
                gameModel.setActivePlayer(gameModel.getPlayerGray());
            else if (gameModel.getActivePlayer() == gameModel.getPlayerBlack())
                gameModel.setActivePlayer(gameModel.getPlayerWhite());
            else if (gameModel.getActivePlayer() == gameModel.getPlayerGray())
                gameModel.setActivePlayer(gameModel.getPlayerBlack());
        }
        this.gameModel.setBlockedChessboard(false);
        this.gameClock.switchPlayers(forward);
        this.moveHistoryController.switchColumns(forward);
    }

    /**
     * Checks if the current state can be undone
     *
     * @return True if the current state of the game is undoable
     */
    public boolean undo() {
        boolean status;

        status = chessboardController.undo();
        if (status)
            this.switchActive(false);
        return status;
    }

    /**
     * Checks if the current game can be rewound to the start
     *
     * @return True if the game can be rewound
     */
    public boolean rewindToBegin() {
        boolean result = false;

        while (chessboardController.undo()) {
            result = true;
        }
        return result;
    }

    /**
     * Checks if the current game can be rewound to the end
     *
     * @return Returns true if the current game can be rewound to the end
     * @throws UnsupportedOperationException When the game cannot be rewound
     */
    public boolean rewindToEnd() throws UnsupportedOperationException {
        boolean result = false;

        while (chessboardController.redo()) {
            result = true;
        }
        return result;
    }

    /**
     * Checks if a move can be redone from the current state of the game (undo an undo)
     *
     * @return True if the most recent undone move can be redone
     */
    public boolean redo() {
        boolean status = chessboardController.redo();
        if (status)
            switchActive(true);

        return status;
    }

    /**
     * Includes logic that happens every time a player selects a square
     *
     * @param square The selected square
     */
    private void selectedSquare(Square square) {
        if (gameModel.isBlockedChessboard()) return;
        if (square == null) return;
        if (chessboardController.getActiveSquare() != null) {
            if (didSelectSameSquare(square)) {
                chessboardController.unselect();
            } else if (didSelectOwnPiece(square)) {
                chessboardController.unselect();
                chessboardController.select(square);
            } else if (didSelectPossibleMove(square)) {
                chessboardController.move(chessboardController.getActiveSquare(), square, true, true);
                chessboardController.unselect();
                applyBuffs();
                switchActive(true);
                if (isCheckMate()) {
                    this.endGame("Checkmate! " + gameModel.getActivePlayer().getName() + " player won!");
                }
            }
        } else if (didSelectOwnPiece(square)) {
            chessboardController.select(square);
        }
    }

    private void applyBuffs() {
        BuffEvaluator evaluator;
        evaluator = new BuffEvaluator(chessboardController, moveHistoryController, gameModel.getActivePlayer());
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

    private boolean didSelectSameSquare(Square square) {
        return square.equals(chessboardController.getActiveSquare());
    }

    private boolean didSelectPossibleMove(Square square) {
        return chessboardController.moveIsPossible(chessboardController.getActiveSquare(), square);
    }

    /**
     * Listens for events that come every time a square is selected
     *
     * @param o   The observable square that generates the events
     * @param arg The new generated Square
     */
    public void update(Observable o, Object arg) {
        Square square = (Square) arg;
        selectedSquare(square);
    }

    public AbstractGameView getView() {
        return this.gameView;
    }
}
