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
package jchess;

import jchess.controller.GameClock;
import jchess.controller.MoveHistoryController;
import jchess.controller.RoundChessboardController;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.exceptions.ReadGameError;
import jchess.helper.Images;
import jchess.helper.Log;
import jchess.helper.RoundChessboardLoader;
import jchess.model.RoundChessboardModel;
import jchess.pieces.Piece;
import jchess.view.RoundChessboardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

/**
 * Class that represents a chess game. It is responsible for starting and ending games
 * and for storing the currently active player.
 * It also provides functionality for saving and loading games.
 */
public class Game extends JPanel implements Observer, ComponentListener {

    public Settings settings;
    private boolean blockedChessboard;
    private RoundChessboardController chessboardController;
    private Player activePlayer;
    private GameClock gameClock;
    private MoveHistoryController moveHistoryController;
    private final int chessboardSize = 800;
    private RoundChessboardLoader chessboardLoader;


    public Game() {
        this.setLayout(null);
        settings = new Settings();

        chessboardLoader = new RoundChessboardLoader();

        RoundChessboardModel model = chessboardLoader.loadDefaultFromJSON(settings);
        RoundChessboardView view =
                new RoundChessboardView(
                        chessboardSize,
                        Images.BOARD,
                        model.getRows(),
                        model.getColumns(),
                        model.squares);

        this.moveHistoryController = new MoveHistoryController(chessboardLoader.getColumnNames());

        chessboardController = new RoundChessboardController(model, view, this.settings, this.moveHistoryController);


        this.add(chessboardController.getView());
        chessboardController.addSelectSquareObserver(this);
        gameClock = new GameClock(this);
        gameClock.gameClockView.setSize(new Dimension(400, 100));
        gameClock.gameClockView.setLocation(new Point(500, 0));
        this.add(gameClock.gameClockView);

        JScrollPane movesHistory = this.moveHistoryController.getScrollPane();
        movesHistory.setSize(new Dimension(245, 350));
        movesHistory.setLocation(new Point(500, 121));
        this.add(movesHistory);

        this.blockedChessboard = false;
        this.setLayout(null);
        this.addComponentListener(this);
        this.setDoubleBuffered(true);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public RoundChessboardController getChessboardController() {
        return chessboardController;
    }

    public MoveHistoryController getMoveHistoryController() {
        return moveHistoryController;
    }

    public GameClock getGameClock() {
        return gameClock;
    }

    /**
     * Method to save current state of the game to disk
     *
     * @param path address of place where game will be saved
     */
    public void saveGame(File path) {
        FileWriter fileW = null;
        try {
            fileW = new FileWriter(path);
        } catch (java.io.IOException exc) {
            System.err.println("error creating fileWriter: " + exc);
            JOptionPane.showMessageDialog(this, Settings.getTexts("error_writing_to_file") + ": " + exc);
            return;
        }
        Calendar cal = Calendar.getInstance();
        String info = new String("[Event \"Game\"]\n[Date \"" + cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "."
                + cal.get(Calendar.DAY_OF_MONTH) + "\"]\n" + "[White \"" + this.settings.getPlayerWhite().getName() + "\"]\n[Black \""
                + this.settings.getPlayerBlack().getName() + "\"]\n\n");
        String str = new String("") + info + this.moveHistoryController.getMovesInString();
        try {
            fileW.write(str);
            fileW.flush();
            fileW.close();
        } catch (java.io.IOException exc) {
            Log.log(Level.SEVERE, "error writing to file: " + exc);
            JOptionPane.showMessageDialog(this, Settings.getTexts("error_writing_to_file") + ": " + exc);
            return;
        }
        JOptionPane.showMessageDialog(this, Settings.getTexts("game_saved_properly"));
    }

    /**
     * Loading game method(loading game state from the earlier saved file)
     *
     * @param file File where is saved game
     */
    static public void loadGame(File file) {
        FileReader fileR = null;
        try {
            fileR = new FileReader(file);
        } catch (java.io.IOException exc) {
            Log.log(Level.SEVERE, "Something wrong reading file: " + exc);
            return;
        }
        BufferedReader br = new BufferedReader(fileR);
        String tempStr;
        String blackName, whiteName;
        try {
            tempStr = getLineWithVar(br, new String("[White"));
            whiteName = getValue(tempStr);
            tempStr = getLineWithVar(br, new String("[Black"));
            blackName = getValue(tempStr);
            tempStr = getLineWithVar(br, new String("1."));
        } catch (ReadGameError err) {
            Log.log(Level.SEVERE, "Error reading file: " + err);
            return;
        }
        Game newGUI = JChessApp.jcv.addNewTab(whiteName + " vs. " + blackName);
        Settings locSetts = newGUI.settings;
        locSetts.getPlayerBlack().setName(blackName);
        locSetts.getPlayerWhite().setName(whiteName);
        locSetts.gameMode = Settings.gameModes.loadGame;

        newGUI.newGame();
        newGUI.blockedChessboard = true;
        newGUI.moveHistoryController.setMoves(newGUI, tempStr);
        newGUI.blockedChessboard = false;
    }

    /**
     * Plausibility check for loading a saved game
     *
     * @param br     BufferedReader object used to read from the saved file
     * @param srcStr The source string
     * @return Returns content of the read file as a string
     * @throws ReadGameError The error thrown if the file cannot be read
     */
    private static String getLineWithVar(BufferedReader br, String srcStr) throws ReadGameError {
        String str = "";
        while (true) {
            try {
                str = br.readLine();
            } catch (java.io.IOException exc) {
                Log.log(Level.SEVERE, "Something wrong reading file: " + exc);
            }
            if (str == null) {
                throw new ReadGameError();
            }
            if (str.startsWith(srcStr)) {
                return str;
            }
        }
    }

    /**
     * Method to get value from loaded text line
     *
     * @param line Line which is read
     * @return result String with loaded value
     * @throws ReadGameError object class when something goes wrong
     */
    private static String getValue(String line) throws ReadGameError {
        int from = line.indexOf("\"");
        int to = line.lastIndexOf("\"");
        int size = line.length() - 1;
        String result;
        if (to < from || from > size || to > size || to < 0 || from < 0) {
            throw new ReadGameError();
        }
        try {
            result = line.substring(from + 1, to);
        } catch (java.lang.StringIndexOutOfBoundsException exc) {
            Log.log(Level.SEVERE, "error getting value: " + exc);
            return "none";
        }
        return result;
    }

    /**
     * Method to Start new game
     */
    public void newGame() {
        activePlayer = settings.getPlayerWhite();

        Game activeGame = JChessApp.jcv.getActiveTabGame();
        if (activeGame != null && JChessApp.jcv.getNumberOfOpenedTabs() == 0) {
            activeGame.repaint();
        }
        this.repaint();
    }

    /**
     * Method to end game
     *
     * @param message The message that is shown at the end of the game to each player
     */
    public void endGame(String message) {
        this.blockedChessboard = true;
        Log.log(message);
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Method to switch active players after move
     */
    public void switchActive(boolean forward) {
        if (forward) {
            if (activePlayer == settings.getPlayerWhite())
                    activePlayer = settings.getPlayerBlack();
            else if (activePlayer == settings.getPlayerBlack())
                    activePlayer = settings.getPlayerGray();
            else if (activePlayer == settings.getPlayerGray())
                    activePlayer = settings.getPlayerWhite();
                }
        else {
            if (activePlayer == settings.getPlayerWhite())
                    activePlayer = settings.getPlayerGray();
            else if (activePlayer == settings.getPlayerBlack())
                    activePlayer = settings.getPlayerWhite();
            else if (activePlayer == settings.getPlayerGray())
                    activePlayer = settings.getPlayerBlack();
                }
            this.gameClock.switchPlayers(forward);
                    if (activePlayer == settings.getPlayerWhite())
                moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player1);
            else if (activePlayer == settings.getPlayerBlack())
                moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player2);
            else if (activePlayer == settings.getPlayerGray())
                moveHistoryController.setActivePlayForColumn(MoveHistoryController.PlayerColumn.player3);
    }

    /**
     * Method of getting the currently active player
     *
     * @return player The active player
     */
    public Player getActivePlayer() {
        return this.activePlayer;
    }

    /**
     * Method to go to next move
     */
    private void nextMove() {
        switchActive(true);
            this.blockedChessboard = false;
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
        if (!blockedChessboard) {
            if ((square == null && square.getPiece() == null && chessboardController.getActiveSquare() == null)
                    || (this.chessboardController.getActiveSquare() == null && square.getPiece() != null
                    && square.getPiece().getPlayer() != this.activePlayer)) {
                return;
            }
            if (square.getPiece() != null && square.getPiece().getPlayer() == this.activePlayer && square != chessboardController.getActiveSquare()) {
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
                this.nextMove();

                HashSet<Piece> cp = chessboardController.getCrucialPieces(this.activePlayer);
                for (Piece piece : cp)
                    if (chessboardController.pieceIsUnsavable(piece))
                        this.endGame("Checkmate! " + piece.getPlayer().getColor().name() + " player lose!");
            }
        } else if (blockedChessboard) {
            Log.log("Chessboard is blocked");
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int height = this.getHeight() >= this.getWidth() ? this.getWidth() : this.getHeight();
        int chess_height;
        chess_height = this.chessboardController.getHeight();
        this.moveHistoryController.getScrollPane().setLocation(new Point(chess_height + 5, 100));
        this.moveHistoryController.getScrollPane().setSize(this.moveHistoryController.getScrollPane().getWidth(), chess_height - 100);
        this.gameClock.gameClockView.setLocation(new Point(chess_height + 5, 0));
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

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
}
