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

import jchess.entities.Player;
import jchess.helper.Log;
import jchess.network.Client;
import jchess.view.Chat;
import jchess.entities.Square;
import jchess.controller.GameClock;
import jchess.controller.RoundChessboardController;
import jchess.model.RoundChessboardModel;
import jchess.controller.MoveHistory;
import jchess.pieces.Piece;
import jchess.view.RoundChessboardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

/**
 * Class responsible for the starts of new games, loading games, saving it, and
 * for ending it. This class is also responsible for appoing player with have a
 * move at the moment
 */
public class Game extends JPanel implements Observer, ComponentListener {

    public Settings settings;
    private boolean blockedChessboard;
    public RoundChessboardController chessboardController;
    private Player activePlayer;
    public GameClock gameClock;
    public Client client;
    public MoveHistory moves;
    public Chat chat;
    private int rows = 24;
    private int squaresPerRow = 6;
    private static final int chessboardSize = 800;


    public Game() {
        this.setLayout(null);
        this.moves = new MoveHistory(this);
        settings = new Settings();

        RoundChessboardModel model = new RoundChessboardModel(rows, squaresPerRow, true, settings);
        RoundChessboardView view = new RoundChessboardView(chessboardSize, "3-player-board.png", rows, squaresPerRow, model.squares);
        chessboardController = new RoundChessboardController(model, view, this.settings, this.moves);

        this.add(chessboardController.getView());
        chessboardController.addSelectSquareObserver(this);
        gameClock = new GameClock(this);
        gameClock.gameClockView.setSize(new Dimension(400, 100));
        gameClock.gameClockView.setLocation(new Point(500, 0));
        this.add(gameClock.gameClockView);

        JScrollPane movesHistory = this.moves.getScrollPane();
        movesHistory.setSize(new Dimension(245, 350));
        movesHistory.setLocation(new Point(500, 121));
        this.add(movesHistory);

        this.chat = new Chat();
        this.chat.setSize(new Dimension(380, 100));
        this.chat.setLocation(new Point(0, 500));
        this.chat.setMinimumSize(new Dimension(400, 100));

        this.blockedChessboard = false;
        this.setLayout(null);
        this.addComponentListener(this);
        this.setDoubleBuffered(true);
    }

    /**
     * Method to save actual state of game
     *
     * @param path address of place where game will be saved
     */
    public void saveGame(File path) {
        File file = path;
        FileWriter fileW = null;
        try {
            fileW = new FileWriter(file);
        } catch (java.io.IOException exc) {
            System.err.println("error creating fileWriter: " + exc);
            JOptionPane.showMessageDialog(this, Settings.lang("error_writing_to_file") + ": " + exc);
            return;
        }
        Calendar cal = Calendar.getInstance();
        String str = new String("");
        String info = new String("[Event \"Game\"]\n[Date \"" + cal.get(cal.YEAR) + "." + (cal.get(cal.MONTH) + 1) + "."
                + cal.get(cal.DAY_OF_MONTH) + "\"]\n" + "[White \"" + this.settings.getPlayerWhite().name + "\"]\n[Black \""
                + this.settings.getPlayerBlack().name + "\"]\n\n");
        str += info;
        str += this.moves.getMovesInString();
        try {
            fileW.write(str);
            fileW.flush();
            fileW.close();
        } catch (java.io.IOException exc) {
            Log.log(Level.SEVERE, "error writing to file: " + exc);
            JOptionPane.showMessageDialog(this, Settings.lang("error_writing_to_file") + ": " + exc);
            return;
        }
        JOptionPane.showMessageDialog(this, Settings.lang("game_saved_properly"));
    }

    /**
     * Loading game method(loading game state from the earlier saved file)
     *
     * @param file File where is saved game
     */

    /*
     * @Override public void setSize(int width, int height) { Dimension min =
     * this.getMinimumSize(); if(min.getHeight() < height && min.getWidth() < width)
     * { super.setSize(width, height); } else if(min.getHeight() < height) {
     * super.setSize(width, (int)min.getHeight()); } else if(min.getWidth() < width)
     * { super.setSize((int)min.getWidth(), height); } else { super.setSize(width,
     * height); } }
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
        String tempStr = new String();
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
        locSetts.getPlayerBlack().name = blackName;
        locSetts.getPlayerWhite().name = whiteName;
        locSetts.getPlayerBlack().setType(Player.playerTypes.localUser);
        locSetts.getPlayerWhite().setType(Player.playerTypes.localUser);
        locSetts.gameMode = Settings.gameModes.loadGame;
        locSetts.gameType = Settings.gameTypes.local;

        newGUI.newGame();
        newGUI.blockedChessboard = true;
        newGUI.moves.setMoves(tempStr);
        newGUI.blockedChessboard = false;
        // newGUI.chessboard.draw();
    }

    /**
     * Method checking in with of line there is an error
     *
     * @param br     BufferedReader class object to operate on
     * @param srcStr String class object with text which variable you want to get in
     *               file
     * @return String with searched variable in file (whole line)
     * @throws ReadGameError class object when something goes wrong when reading
     *                       file
     */
    static public String getLineWithVar(BufferedReader br, String srcStr) throws ReadGameError {
        String str = new String();
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
     * Method to get value from loaded txt line
     *
     * @param line Line which is readed
     * @return result String with loaded value
     * @throws ReadGameError object class when something goes wrong
     */
    static public String getValue(String line) throws ReadGameError {
        // Log.log("getValue called with: "+line);
        int from = line.indexOf("\"");
        int to = line.lastIndexOf("\"");
        int size = line.length() - 1;
        String result = new String();
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
        if (activePlayer.playerType != Player.playerTypes.localUser) {
            this.blockedChessboard = true;
        }
        Game activeGame = JChessApp.jcv.getActiveTabGame();
        if (activeGame != null && JChessApp.jcv.getNumberOfOpenedTabs() == 0) {
//            activeGame.chessboardController.resizeChessboard(); TODO resizing
            activeGame.repaint();
        }
        this.repaint();
    }

    /**
     * Method to end game
     *
     * @param message what to show player(s) at end of the game (for example "draw",
     *                "black wins" etc.)
     */
    public void endGame(String message) {
        this.blockedChessboard = true;
        Log.log(message);
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Method to swich active players after move
     */
    public void switchActive() {
        if (activePlayer == settings.getPlayerWhite()) {
            activePlayer = settings.getPlayerBlack();
        } else if (activePlayer == settings.getPlayerBlack()) {
            activePlayer = settings.getPlayerGray();
        } else if (activePlayer == settings.getPlayerGray()) {
            activePlayer = settings.getPlayerWhite();
        }

        this.gameClock.switch_clocks();
    }

    /**
     * Method of getting accualy active player
     *
     * @return player The player which have a move
     */
    public Player getActivePlayer() {
        return this.activePlayer;
    }

    /**
     * Method to go to next move (checks if game is local/network etc.)
     */
    public void nextMove() {
        switchActive();

        Log.log("next move, active player: " + activePlayer.name + ", color: " + activePlayer.color.name() + ", type: "
                + activePlayer.playerType.name());
        if (activePlayer.playerType == Player.playerTypes.localUser) {
            this.blockedChessboard = false;
        } else if (activePlayer.playerType == Player.playerTypes.networkUser) {
            this.blockedChessboard = true;
        } else if (activePlayer.playerType == Player.playerTypes.computer) {
        }
    }

    /**
     * Method to simulate Move to check if it's correct etc. (usable for network
     * game).
     *
     * @param beginX from which X (on chessboard) move starts
     * @param beginY from which Y (on chessboard) move starts
     * @param endX   to which X (on chessboard) move go
     * @param endY   to which Y (on chessboard) move go
     * @return Returns true if the move is correct
     */
    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {
        boolean moveCorrect = chessboardController.moveIsPossible(beginX, beginY, endX, endY);
        nextMove();
        return moveCorrect;
    }

    // MouseListener:
    public void mouseClicked(MouseEvent arg0) {
    }

    public boolean undo() {
        boolean status = false;

        if (this.settings.gameType == Settings.gameTypes.local) {
            status = chessboardController.undo(true);
            if (status) {
                this.switchActive();
            }
        } else if (this.settings.gameType == Settings.gameTypes.network) {
            this.client.sendUndoAsk();
            status = true;
        }
        return status;
    }

    public boolean rewindToBegin() {
        boolean result = false;

        if (this.settings.gameType == Settings.gameTypes.local) {
            while (chessboardController.undo(true)) {
                result = true;
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }

        return result;
    }

    public boolean rewindToEnd() throws UnsupportedOperationException {
        boolean result = false;

        if (this.settings.gameType == Settings.gameTypes.local) {
            while (chessboardController.redo(true)) {
                result = true;
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }

        return result;
    }

    public boolean redo() {
        boolean status = chessboardController.redo(true);
        if (this.settings.gameType == Settings.gameTypes.local) {
            if (status) {
                this.nextMove();
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }
        return status;
    }

    public void squareSelected(Square sq) {
///if (event.getButton() == MouseEvent.BUTTON3) // right button
//        {
//            this.undo();
//        } else if (event.getButton() == MouseEvent.BUTTON2 && settings.gameType == Settings.gameTypes.local) {
//            this.redo();
//        } else if (event.getButton() == MouseEvent.BUTTON1) // left button
//        { TODO add action listener

        if (!blockedChessboard) {
            if ((sq == null && sq.getPiece() == null && chessboardController.getActiveSquare() == null)
                    || (this.chessboardController.getActiveSquare() == null && sq.getPiece() != null
                    && sq.getPiece().player != this.activePlayer)) {
                return;
            }

            if (sq.getPiece() != null && sq.getPiece().player == this.activePlayer && sq != chessboardController.getActiveSquare()) {
                chessboardController.unselect();
                chessboardController.select(sq);
            } else if (chessboardController.getActiveSquare() == sq) // unselect
            {
                chessboardController.unselect();
            } else if (chessboardController.getActiveSquare() != null && chessboardController.getActiveSquare().getPiece() != null
                    && chessboardController.movePossible(chessboardController.getActiveSquare(), sq)) // move
            {
                if (settings.gameType == Settings.gameTypes.local) {
                    //TODO: exception is caught here --> method returns without switching player
                    chessboardController.move(chessboardController.getActiveSquare(), sq, true, true);
                } else if (settings.gameType == Settings.gameTypes.network) {
                    client.sendMove(chessboardController.getActiveSquare().getPozX(), chessboardController.getActiveSquare().getPozY(), sq.getPozX(),
                            sq.getPozY());
                    chessboardController.move(chessboardController.getActiveSquare(), sq, true, true);
                }

                chessboardController.unselect();

                // switch player
                this.nextMove();

                // checkmate or stalemate
                Piece king;
                if (this.activePlayer == settings.getPlayerWhite()) {
                    king = chessboardController.getKingWhite();
                } else {
                    king = chessboardController.getKingBlack();
                }

                if (chessboardController.pieceIsUnsavable(king))
                    this.endGame("Checkmate! " + king.player.color.toString() + " player lose!");

						/*case 2:
							this.endGame("Stalemate! Draw!");
							break;
						}*/

            }
        } else if (blockedChessboard) {
            Log.log("Chessboard is blocked");
        }
    }


    public void componentResized(ComponentEvent e) {
        int height = this.getHeight() >= this.getWidth() ? this.getWidth() : this.getHeight();
        int chess_height = (int) Math.round((height * 0.8) / 8) * 8;
//        this.chessboardController.resizeChessboard((int) chess_height);
        chess_height = this.chessboardController.getHeight();
        this.moves.getScrollPane().setLocation(new Point(chess_height + 5, 100));
        this.moves.getScrollPane().setSize(this.moves.getScrollPane().getWidth(), chess_height - 100);
        this.gameClock.gameClockView.setLocation(new Point(chess_height + 5, 0));
        if (this.chat != null) {
            this.chat.setLocation(new Point(0, chess_height + 5));
            this.chat.setSize(new Dimension(chess_height, this.getHeight() - (chess_height + 5)));
        }
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void update(Observable o, Object arg) {
        Square square = (Square) arg;
        squareSelected(square);

    }
}

class ReadGameError extends Exception {
}
