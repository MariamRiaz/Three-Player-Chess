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

import jchess.Game;
import jchess.Settings;
import jchess.entities.Player;
import jchess.entities.Square;
import jchess.helper.Log;
import jchess.model.MoveHistoryModel;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.move.effects.MoveEffect;
import jchess.view.MoveHistoryView;
import org.apache.commons.text.StringSubstitutor;

import javax.swing.*;
import java.util.*;


/**
 * Class that holds all the move history of the game, and all the necessary methods to undo and redo a move
 */
public class MoveHistoryController {
    public enum PlayerColumn {
        player1,
        player2,
        player3
    }

    private String[] names = new String[]{Settings.lang("white"), Settings.lang("black"), Settings.lang("gray")};
    private MoveHistoryView moveHistoryView;
    private MoveHistoryModel moveHistoryModel;
    private ArrayList<Character> columnNames;


    public enum castling {
        none, shortCastling, longCastling
    }

    public MoveHistoryController(ArrayList<Character> columns) {
        super();
        this.moveHistoryModel = new MoveHistoryModel();
        this.moveHistoryView = new MoveHistoryView(moveHistoryModel);
        this.moveHistoryModel.addColumn(this.names[0]);
        this.moveHistoryModel.addColumn(this.names[1]);
        this.moveHistoryModel.addColumn(this.names[2]);
        this.columnNames = columns;
    }

    /**
     * Method of adding new moves to the table
     *
     * @param str String which in is saved player move
     */
    private void addMoveToTable(String str) {
        try {
            if (moveHistoryModel.activePlayerColumn.equals(PlayerColumn.player1)) {
                moveHistoryModel.addRow(new String[2]);
                moveHistoryModel.rowsNum = this.moveHistoryModel.getRowCount() - 1;
                this.moveHistoryModel.setValueAt(str, moveHistoryModel.rowsNum, 0);

            } else if (moveHistoryModel.activePlayerColumn.equals(PlayerColumn.player2)) {
                this.moveHistoryModel.setValueAt(str, moveHistoryModel.rowsNum, 1);
                moveHistoryModel.rowsNum = this.moveHistoryModel.getRowCount() - 1;

            } else if (moveHistoryModel.activePlayerColumn.equals(PlayerColumn.player3)) {
                this.moveHistoryModel.setValueAt(str, moveHistoryModel.rowsNum, 2);
                moveHistoryModel.rowsNum = this.moveHistoryModel.getRowCount() - 1;
            }

            this.moveHistoryView.getTable().scrollRectToVisible(this.moveHistoryView.getTable().getCellRect(
                    this.moveHistoryView.getTable().getRowCount() - 1, 0, true));// scroll to down

        } catch (
                java.lang.ArrayIndexOutOfBoundsException exc) {
            if (moveHistoryModel.rowsNum > 0) {
                this.moveHistoryModel.rowsNum--;
                addMoveToTable(str);
            }
        }

    }

    /**
     * Method of adding new move
     */
    public void addMove(MoveEffect moveEffects, boolean registerInHistory, boolean registerInTable) {
    	if (registerInTable) {
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
    	}
        
        if (registerInHistory)
            moveHistoryModel.moveBackStack.add(moveEffects);
    }

    private String getPosition(Square square) {
        return columnNames.get(square.getPozX()) // add letter of Square from which move was made
                + Integer.toString(square.getPozY() + 1);// add number of Square from which move was made
    }

    void clearMoveForwardStack() {
        moveHistoryModel.moveForwardStack.clear();
    }

    public JScrollPane getScrollPane() {
        return this.moveHistoryView.getScrollPane();
    }

    public ArrayList<String> getMoves() {
        return moveHistoryModel.move;
    }

    synchronized Queue<MoveEffect> undo() {
    	Queue<MoveEffect> retVal = new PriorityQueue<>();
        
    	MoveEffect toAdd = null;
    	while ((toAdd = undoOne()) != null) {
    		retVal.add(toAdd);
    		if (toAdd.isFromMove())
    			break;
    	}
    	
        return retVal;
    }
    
    synchronized MoveEffect undoOne() {
    	MoveEffect last = null;
    	
        try {
            last = moveHistoryModel.moveBackStack.pop();
        } catch (EmptyStackException | ArrayIndexOutOfBoundsException exc) {
            exc.printStackTrace();
        }

        if (last != null) {
            moveHistoryModel.moveForwardStack.push(last);

            if (last.isFromMove()) {
	            if (moveHistoryModel.activePlayerColumn.equals(MoveHistoryController.PlayerColumn.player1)) {
	                if (moveHistoryModel.getRowCount() > 0)
	                    moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 2);
	
	            } else if (moveHistoryModel.activePlayerColumn.equals(MoveHistoryController.PlayerColumn.player2)) {
	                moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 0);
	                moveHistoryModel.removeRow(moveHistoryModel.getRowCount() - 1);
	                if (moveHistoryModel.rowsNum > 0)
	                    moveHistoryModel.rowsNum--;
	
	            } else {
	                if (moveHistoryModel.getRowCount() > 0)
	                    moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 1);
	
	            }
	            moveHistoryModel.move.remove(moveHistoryModel.move.size() - 1);
            }
        }
        
        return last;
    }

    synchronized Queue<MoveEffect> redo() {
    	Queue<MoveEffect> retVal = new PriorityQueue<>();
        
    	MoveEffect toAdd = null;
    	while ((toAdd = redoOne()) != null) {
    		if (toAdd.isFromMove() && retVal.size() != 0) {
    			undoOne();
    			break;
    		}
    		
    		retVal.add(toAdd);
    	}
    	
        return retVal;
    }
    
    synchronized MoveEffect redoOne() {
        try {
            MoveEffect first = moveHistoryModel.moveForwardStack.pop();
            addMove(first, true, first.isFromMove());
            return first;
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
        moveHistoryModel.move.add(move);
        this.addMoveToTable(move);
    }

    /**
     * Method of getting the moves in string
     *
     * @return str String which in is capt player move
     */
    public String getMovesInString() {
        int n = 1;
        int i = 0;
        StringBuilder str = new StringBuilder();
        for (String locMove : this.getMoves()) {
            if (i % 2 == 0) {
                str.append(n).append(". ");
                n += 1;
            }
            str.append(locMove).append(" ");
            i += 1;
        }
        return str.toString();
    }

    /**
     * Method to set all moves from String with validation test (usefoul for network
     * game)
     *
     * @param moves String to set in String like PGN with full-notation format
     */
    public void setMoves(Game game, String moves) {
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
            if (!MoveHistoryController.isMoveCorrect(locMove.trim())) // if not
            {
                JOptionPane.showMessageDialog(game, Settings.lang("invalid_file_to_load") + moveHistoryModel.move);
                return;// show message and finish reading game
            }
        }
        boolean canMove = false;
        for (String locMove : tempArray) {
            if (locMove.equals("O-O-O") || locMove.equals("O-O")) // if castling
            {
                int[] values = new int[4];
                if (locMove.equals("O-O-O")) {
                    if (game.getActivePlayer().color == Player.colors.black) // if black turn
                    {
                        values = new int[]{4, 0, 2, 0};// move value for castling (King move)
                    } else {
                        values = new int[]{4, 7, 2, 7};// move value for castling (King move)
                    }
                } else if (locMove.equals("O-O")) // if short castling
                {
                    if (game.getActivePlayer().color == Player.colors.black) // if black turn
                    {
                        values = new int[]{4, 0, 6, 0};// move value for castling (King move)
                    } else {
                        values = new int[]{4, 7, 6, 7};// move value for castling (King move)
                    }
                }
                canMove = game.simulateMove(values[0], values[1], values[2], values[3]);

                if (!canMove) // if move is illegal
                {
                    JOptionPane.showMessageDialog(game, Settings.lang("illegal_move_on") + locMove);
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
                List<Square> squares = game.getChessboardController().getSquares();
                xTo = locMove.charAt(from) - 97;// from ASCII
                yTo = RoundChessboardController.bottom - (locMove.charAt(from + 1) - 49);// from ASCII
            } else {
                xFrom = locMove.charAt(from) - 97;// from ASCII
                yFrom = RoundChessboardController.bottom - (locMove.charAt(from + 1) - 49);// from ASCII
                xTo = locMove.charAt(from + 3) - 97;// from ASCII
                yTo = RoundChessboardController.bottom - (locMove.charAt(from + 4) - 49);// from ASCII
            }
            canMove = game.simulateMove(xFrom, yFrom, xTo, yTo);
            if (!canMove) // if move is illegal
            {
                JOptionPane.showMessageDialog(game, Settings.lang("illegal_move_on") + locMove);
                game.getChessboardController().setActiveSquare(null);
                return;// finish reading game and show message
            }
        }
    }

    public void setActivePlayForColumn(PlayerColumn column) {
        moveHistoryModel.activePlayerColumn = column;
    }
}