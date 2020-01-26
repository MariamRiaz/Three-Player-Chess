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

import jchess.model.GameModel;
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

    private String[] names = new String[]{GameModel.getTexts("white"), GameModel.getTexts("black"), GameModel.getTexts("gray")};
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

    Queue<MoveEffect> undo() {
        Queue<MoveEffect> retVal = new PriorityQueue<>();

        MoveEffect toAdd = null;
        while ((toAdd = undoOne()) != null) {
            retVal.add(toAdd);
            if (toAdd.isFromMove())
                break;
        }

        return retVal;
    }

    MoveEffect undoOne() {
        MoveEffect last = null;

        if (!moveHistoryModel.moveBackStack.isEmpty()) {
            last = moveHistoryModel.moveBackStack.pop();
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

    Queue<MoveEffect> redo() {
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

    MoveEffect redoOne() {
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

    public void setActivePlayForColumn(PlayerColumn column) {
        moveHistoryModel.activePlayerColumn = column;
    }
}