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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javax.swing.JScrollPane;

import jchess.entities.PlayerColor;
import org.apache.commons.text.StringSubstitutor;

import jchess.Game;
import jchess.helper.Log;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.move.effects.MoveEffect;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.view.MoveHistoryView;

import javax.swing.JOptionPane;


/**
 * Class that holds all the move history of the game, and all the necessary methods to undo and redo a move
 */
public class MoveHistory {
    public static enum PlayerColumn {
        player1,
        player2,
        player3
    }

    private ArrayList<String> move = new ArrayList<>();
    private int columnsNum = 3;
    private int rowsNum = 0;
    private String[] names = new String[]{Settings.lang("white"), Settings.lang("black"), Settings.lang("gray")};
    private PlayerColumn activePlayerColumn = PlayerColumn.player1;
    private Game game;
    private Stack<MoveEffect> moveBackStack = new Stack<>();
    private Stack<MoveEffect> moveForwardStack = new Stack<>();
    private MoveHistoryView moveHistoryView;

    public enum castling {
        none, shortCastling, longCastling
    }
    public MoveHistory(Game game) {
        super();
        this.moveHistoryView = new MoveHistoryView();
        this.game = game;
        this.moveHistoryView.addColumn(this.names[0]);
        this.moveHistoryView.addColumn(this.names[1]);
        this.moveHistoryView.addColumn(this.names[2]);
    }

    /**
     * Method of adding new moves to the table
     *
     * @param str String which in is saved player move
     */
    private void addMove2Table(String str) {
        try {
            if (activePlayerColumn.equals(PlayerColumn.player1)) {
                this.moveHistoryView.addRow();
                this.rowsNum = this.moveHistoryView.getRowCount() - 1;
                this.moveHistoryView.setValueAt(str, rowsNum, 0);

            } else if (activePlayerColumn.equals(PlayerColumn.player2)) {
                this.moveHistoryView.setValueAt(str, rowsNum, 1);
                this.rowsNum = this.moveHistoryView.getRowCount() - 1;

            } else if (activePlayerColumn.equals(PlayerColumn.player3)) {
                this.moveHistoryView.setValueAt(str, rowsNum, 2);
                this.rowsNum = this.moveHistoryView.getRowCount() - 1;
            }

            this.moveHistoryView.table.scrollRectToVisible(this.moveHistoryView.table.getCellRect(this.moveHistoryView.table.getRowCount() - 1, 0, true));// scroll to down

        } catch (
                java.lang.ArrayIndexOutOfBoundsException exc) {
            if (this.rowsNum > 0) {
                this.rowsNum--;
                addMove2Table(str);
            }
        }

    }

    /**
     * Method of adding new move
     */
    public void addMove(MoveEffect moveEffects, boolean registerInHistory) {
    	HashMap<String, String> values = new HashMap<String, String>() {{ 
    		put(Move.formatStringPiece, moveEffects.getMoving().getDefinition().getSymbol());
    		put(Move.formatStringFrom, getPosition(moveEffects.getFrom()));
    		put(Move.formatStringTo, getPosition(moveEffects.getTrigger()));
    	}};
    	
    	String formatString = moveEffects.getMove().getFormatString(moveEffects.getFlag());
    	if (formatString == null)
    		formatString = moveEffects.getMove().getFormatString(MoveType.OnlyMove);
    	if (formatString == null)
    		formatString = moveEffects.getMove().getDefaultFormatString();
    	if (formatString == null)
    		formatString = "-";
    	
    	addMove(new StringSubstitutor(values).replace(formatString));
    	
        if (registerInHistory)
            this.moveBackStack.add(moveEffects);
    }
    
    private String getPosition(Square square) {
        return Character.toString((char) (square.getPozX() + 97)) // add letter of Square from which move was made
        	+ Integer.toString(square.getPozY() + 1);// add number of Square from which move was made
    }

    public void clearMoveForwardStack() {
        this.moveForwardStack.clear();
    }

    public JScrollPane getScrollPane() {
        return this.moveHistoryView.scrollPane;
    }

    public ArrayList<String> getMoves() {
        return this.move;
    }

    public synchronized MoveEffect getLastMoveFromHistory() {
        try {
            MoveEffect last = this.moveBackStack.get(this.moveBackStack.size() - 1);
            return last;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    public synchronized MoveEffect getNextMoveFromHistory() {
        try {
        	MoveEffect next = this.moveForwardStack.get(this.moveForwardStack.size() - 1);
            return next;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }

    }

    public synchronized MoveEffect undo() {
    	MoveEffect last = null;
        try {
        	last = this.moveBackStack.pop();
        } catch (java.util.EmptyStackException exc) {
        	exc.printStackTrace();
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            exc.printStackTrace();
        }
        
        if (last != null) {
            if (this.game.getSettings().gameType == Settings.gameTypes.local) // moveForward / redo available only for local game 
                this.moveForwardStack.push(last);
            
            if (activePlayerColumn.equals(PlayerColumn.player1)) {
                if (this.moveHistoryView.getRowCount() > 0)
                    this.moveHistoryView.setValueAt("", this.moveHistoryView.getRowCount() - 1, 2);
            } else if (activePlayerColumn.equals(PlayerColumn.player2)) {
                this.moveHistoryView.setValueAt("", this.moveHistoryView.getRowCount() - 1, 0);
                this.moveHistoryView.removeRow(this.moveHistoryView.getRowCount() - 1);
                if (this.rowsNum > 0)
                    this.rowsNum--;
            } else {
                if (this.moveHistoryView.getRowCount() > 0)
                    this.moveHistoryView.setValueAt("", this.moveHistoryView.getRowCount() - 1, 1);
            }
            this.move.remove(this.move.size() - 1);
        }

        return last;
    }

    public synchronized MoveEffect redo() {
        try {
            if (this.game.getSettings().gameType == Settings.gameTypes.local) {
            	MoveEffect first = this.moveForwardStack.pop();
                this.moveBackStack.push(first);
                
                addMove(first, false);
                
                return first;
            }
            return null;
        } catch (java.util.EmptyStackException exc) {
            return null;
        }

    }

    /**
     * Method with is checking is the move is correct
     *
     * @param move String which in is capt player move
     * @return boolean 1 if the move is correct, else 0
     */
    private static boolean isMoveCorrect(String move) {
        if (move.equals("O-O") || move.equals("O-O-O")) {
            return true;
        }
        try {
            int from = 0;
            int sign = move.charAt(from);// get First
            switch (sign) // if sign of piece, get next
            {
                case 66: // B like Bishop
                case 75: // K like King
                case 78: // N like Knight
                case 81: // Q like Queen
                case 82:
                    from = 1;
                    break; // R like Rook
            }
            sign = move.charAt(from);
            Log.log(sign);
            if (sign < 97 || sign > 104) // if lower than 'a' or higher than 'h'
            {
                return false;
            }
            sign = move.charAt(from + 1);
            if (sign < 49 || sign > 56) // if lower than '1' or higher than '8'
            {
                return false;
            }
            if (move.length() > 3) // if is equal to 3 or lower, than it's in short notation, no more checking
            // needed
            {
                sign = move.charAt(from + 2);
                if (sign != 45 && sign != 120) // if isn't '-' and 'x'
                {
                    return false;
                }
                sign = move.charAt(from + 3);
                if (sign < 97 || sign > 104) // if lower than 'a' or higher than 'h'
                {
                    return false;
                }
                sign = move.charAt(from + 4);
                if (sign < 49 || sign > 56) // if lower than '1' or higher than '8'
                {
                    return false;
                }
            }
        } catch (java.lang.StringIndexOutOfBoundsException exc) {
            return false;
        }

        return true;
    }

    private void addMove(String move) {
        //if (isMoveCorrect(move)) {
            this.move.add(move);
            this.addMove2Table(move);
        //}
    }

    public void addMoves(ArrayList<String> list) {
        for (String singleMove : list)
            if (isMoveCorrect(singleMove))
                this.addMove(singleMove);
    }

    /**
     * Method of getting the moves in string
     *
     * @return str String which in is capt player move
     */
    public String getMovesInString() {
        int n = 1;
        int i = 0;
        String str = new String();
        for (String locMove : this.getMoves()) {
            if (i % 2 == 0) {
                str += n + ". ";
                n += 1;
            }
            str += locMove + " ";
            i += 1;
        }
        return str;
    }

    /**
     * Method to set all moves from String with validation test (usefoul for network
     * game)
     *
     * @param moves String to set in String like PGN with full-notation format
     */
    public void setMoves(String moves) {
        int from = 0;
        int to = 0;
        int n = 1;
        ArrayList<String> tempArray = new ArrayList<>();
        int tempStrSize = moves.length() - 1;
        while (true) {
            from = moves.indexOf(" ", from);
            to = moves.indexOf(" ", from + 1);
            // System.out.println(from+">"+to);
            try {
                tempArray.add(moves.substring(from + 1, to).trim());
            } catch (java.lang.StringIndexOutOfBoundsException exc) {
                System.out.println("error parsing file to load: " + exc);
                break;
            }
            if (n % 2 == 0) {
                from = moves.indexOf(".", to);
                if (from < to) {
                    break;
                }
            } else {
                from = to;
            }
            n += 1;
            if (from > tempStrSize || to > tempStrSize) {
                break;
            }
        }
        for (String locMove : tempArray) // test if moves are written correctly
        {
            if (!MoveHistory.isMoveCorrect(locMove.trim())) // if not
            {
                JOptionPane.showMessageDialog(this.game, Settings.lang("invalid_file_to_load") + move);
                return;// show message and finish reading game
            }
        }
        boolean canMove = false;
        for (String locMove : tempArray) {
            if (locMove.equals("O-O-O") || locMove.equals("O-O")) // if castling
            {
                int[] values = new int[4];
                if (locMove.equals("O-O-O")) {
                    if (this.game.getActivePlayer().getColor() == PlayerColor.BLACK) // if black turn
                    {
                        values = new int[]{4, 0, 2, 0};// move value for castling (King move)
                    } else {
                        values = new int[]{4, 7, 2, 7};// move value for castling (King move)
                    }
                } else if (locMove.equals("O-O")) // if short castling
                {
                    if (this.game.getActivePlayer().getColor() == PlayerColor.BLACK) // if black turn
                    {
                        values = new int[]{4, 0, 6, 0};// move value for castling (King move)
                    } else {
                        values = new int[]{4, 7, 6, 7};// move value for castling (King move)
                    }
                }
                canMove = this.game.simulateMove(values[0], values[1], values[2], values[3]);

                if (!canMove) // if move is illegal
                {
                    JOptionPane.showMessageDialog(this.game, Settings.lang("illegal_move_on") + locMove);
                    return;// finish reading game and show message
                }
                continue;
            }
            from = 0;
            int num = locMove.charAt(from);
            if (num <= 90 && num >= 65) {
                from = 1;
            }
            int xFrom = 9; // set to higher value than chessboard has fields, to cause error if piece won't
            // be found
            int yFrom = 9;
            int xTo = 9;
            int yTo = 9;
            
            if (locMove.length() <= 3) {
                List<Square> squares = this.game.getChessboardController().getSquares();
                xTo = locMove.charAt(from) - 97;// from ASCII
                yTo = RoundChessboardController.bottom - (locMove.charAt(from + 1) - 49);// from ASCII
                for (Square square : squares) {
                    if (square.getPiece() == null
                            || this.game.getActivePlayer().getColor() != square.getPiece().getPlayer().getColor()) {
                        continue;
                    }
                    /*HashSet<Square> pieceMoves = this.game.chessboardController.getValidTargetSquares(square);
                    for (Object oldSquare : pieceMoves) {
                        Square currSquare = (Square) oldSquare;
                        if (currSquare.getPozX() == xTo && currSquare.getPozY() == yTo) {
                            xFrom = square.getPozX();
                            yFrom = square.getPozY();
                            pieceFound = true;
                        }
                    }*/
                }
            } else {
                xFrom = locMove.charAt(from) - 97;// from ASCII
                yFrom = RoundChessboardController.bottom - (locMove.charAt(from + 1) - 49);// from ASCII
                xTo = locMove.charAt(from + 3) - 97;// from ASCII
                yTo = RoundChessboardController.bottom - (locMove.charAt(from + 4) - 49);// from ASCII
            }
            canMove = this.game.simulateMove(xFrom, yFrom, xTo, yTo);
            if (!canMove) // if move is illegal
            {
                JOptionPane.showMessageDialog(this.game, Settings.lang("illegal_move_on") + locMove);
                this.game.getChessboardController().setActiveSquare(null);
                return;// finish reading game and show message
            }
        }
    }
    
    public void setActivePlayerColumn(PlayerColumn column) {
    	this.activePlayerColumn = column;
    }
}