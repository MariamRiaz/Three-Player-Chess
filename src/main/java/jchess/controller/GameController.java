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

import jchess.JChessApp;
import jchess.entities.Square;
import jchess.helper.Log;
import jchess.helper.RoundChessboardLoader;
import jchess.model.GameModel;
import jchess.move.buff.BuffEvaluator;
import jchess.pieces.Piece;
import jchess.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * Class that represents a chess game. It is responsible for starting and ending games
 * and for storing the currently active player.
 * It also provides functionality for saving and loading games.
 */
public class GameController implements Observer {

    private GameView gameView;

    private GameModel gameModel;
    private RoundChessboardController chessboardController;
    private MoveHistoryController moveHistoryController;
    private GameClock gameClock;
    private final int chessboardSize = 800;
    private RoundChessboardLoader chessboardLoader;


    public GameController() {

        gameModel = new GameModel();
        chessboardLoader = new RoundChessboardLoader();
        initializeControllers();
        initializeView();
        gameModel.setBlockedChessboard(false);
        gameModel.setActivePlayer(gameModel.getPlayerWhite());
    }

    private void initializeControllers() {
        moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());
        chessboardController = new RoundChessboardController(chessboardLoader, chessboardSize, this.gameModel, this.moveHistoryController);
        gameClock = new GameClock(this);
        chessboardController.addSelectSquareObserver(this);
    }

    private void initializeView() {
        gameView = new GameView(this, chessboardController.getView(), moveHistoryController.getScrollPane(), gameClock.getGameClockView());
        gameView.add(chessboardController.getView());

        gameClock.getGameClockView().setSize(new Dimension(400, 100));
        gameClock.getGameClockView().setLocation(new Point(500, 0));
        gameView.add(gameClock.getGameClockView());

        JScrollPane movesHistoryScrollPane = this.moveHistoryController.getScrollPane();
        movesHistoryScrollPane.setSize(new Dimension(245, 350));
        movesHistoryScrollPane.setLocation(new Point(500, 121));
        gameView.add(movesHistoryScrollPane);

        gameView.setDoubleBuffered(true);
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public GameClock getGameClock() {
        return gameClock;
    }

    /**
     * Method to end game
     *
     * @param message The message that is shown at the end of the game to each player
     */
    void endGame(String message) {
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
        this.gameClock.switchPlayers(forward);
        if (gameModel.getActivePlayer() == gameModel.getPlayerWhite())
            moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player1);
        else if (gameModel.getActivePlayer() == gameModel.getPlayerBlack())
            moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player2);
        else if (gameModel.getActivePlayer() == gameModel.getPlayerGray())
            moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player3);
    }

    /**
     * Method to go to next move
     */
    private void nextMove() {

        switchActive(true);
        gameModel.setBlockedChessboard(false);
    }

    /**
     * Method to simulate Move to check if it's correct etc.
     *
     * @param beginX the initial x coordinate of the square where the move starts
     * @param beginY the initial y coordinate of the square where the move starts
     * @param endX   the final x coordinate of the square where the move ends
     * @param endY   the final y coordinate of the square where the move ends
     * @return Returns true if the move is valid
     */
    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {
        boolean moveCorrect = chessboardController.moveIsPossible(beginX, beginY, endX, endY);
        nextMove();
        return moveCorrect;
    }

    /**
     * Checks if the current state can be undone
     *
     * @return True if the current state of the game is undoable
     */
    public boolean undo() {
        boolean status;

        status = chessboardController.undo(true);
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

        while (chessboardController.undo(true)) {
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

        while (chessboardController.redo(true)) {
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
        boolean status = chessboardController.redo(true);
        if (status)
            this.nextMove();

        return status;
    }

    /**
     * Includes logic that happens every time a player selects a square
     *
     * @param square The selected square
     */
    private void selectedSquare(Square square) {
//        TODO add action listener for buttons
        if (!gameModel.isBlockedChessboard()) {
            if ((square == null && square.getPiece() == null && chessboardController.getActiveSquare() == null)
                    || (this.chessboardController.getActiveSquare() == null && square.getPiece() != null
                    && square.getPiece().getPlayer() != gameModel.getActivePlayer())) {
                return;
            }
            if (square.getPiece() != null && square.getPiece().getPlayer() == gameModel.getActivePlayer() && square != chessboardController.getActiveSquare()) {
                chessboardController.unselect();
                chessboardController.select(square);
            } else if (chessboardController.getActiveSquare() == square) // unselect
            {
                chessboardController.unselect();
            } else if (chessboardController.getActiveSquare() != null && chessboardController.getActiveSquare().getPiece() != null
                    && chessboardController.moveIsPossible(chessboardController.getActiveSquare(), square)) // move
            {
                chessboardController.move(chessboardController.getActiveSquare(), square, true, true);
                chessboardController.unselect();
                new BuffEvaluator(chessboardController, moveHistoryController).evaluate();

                this.nextMove();

                HashSet<Piece> cp = chessboardController.getCrucialPieces(gameModel.getActivePlayer());
                for (Piece piece : cp)
                    if (chessboardController.pieceIsUnsavable(piece))
                        this.endGame("Checkmate! " + piece.getPlayer().getColor().name() + " player lose!");
            }
        } else {
            Log.log("Chessboard is blocked");
        }
    }

    /**
     * Listens for events that come every time a square is selected
     *
     * @param o   The observable square that generates the events
     * @param arg The new generated Square
     */
    @Override
    public void update(Observable o, Object arg) {
        Square square = (Square) arg;
        selectedSquare(square);
    }

    public GameView getView() {
        return this.gameView;
    }
}
