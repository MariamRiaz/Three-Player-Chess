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
package jchess.game.history;

import jchess.game.chessboard.model.Square;
import jchess.game.GameModel;
import jchess.io.ResourceLoader;
import jchess.io.Texts;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.move.effects.MoveEffect;
import org.apache.commons.text.StringSubstitutor;

import javax.swing.*;
import java.util.*;


/**
 * Class that holds all the move history of the game, and all the necessary methods to undo and redo a move
 */
public class MoveHistoryController implements IMoveHistoryController {
    public enum PlayerColumn {
        player1,
        player2,
        player3
    }

    public List<Integer> column = new ArrayList<>();

    private String[] names = new String[]{ResourceLoader.getTexts(Texts.WHITE_KEY), ResourceLoader.getTexts(Texts.BLACK_KEY), ResourceLoader.getTexts(Texts.GRAY_KEY)};
    private IMoveHistoryView moveHistoryView;
    private AbstractMoveHistoryModel moveHistoryModel;
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
            if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player1)) {
                moveHistoryModel.addRow(new String[2]);
                moveHistoryModel.rowsNum = this.moveHistoryModel.getRowCount() - 1;
                this.moveHistoryModel.setValueAt(str, moveHistoryModel.rowsNum, 0);

            } else if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player2)) {
                this.moveHistoryModel.setValueAt(str, moveHistoryModel.rowsNum, 1);
                moveHistoryModel.rowsNum = this.moveHistoryModel.getRowCount() - 1;

            } else if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player3)) {
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
                put(Move.formatStringPiece, moveEffects.getPiece().getDefinition().getSymbol());
                put(Move.formatStringFrom, getPosition(moveEffects.getFromSquare()));
                put(Move.formatStringTo, getPosition(moveEffects.getToSquare()));
            }};

            String formatString = moveEffects.getMove().getFormatString(moveEffects.getMoveType());
            if (formatString == null)
                formatString = moveEffects.getMove().getFormatString(MoveType.OnlyMove);
            if (formatString == null)
                formatString = moveEffects.getMove().getDefaultFormatString();
            if (formatString == null)
                formatString = "-";

            addMove(new StringSubstitutor(values).replace(formatString));
        }

        if (registerInHistory)
            moveHistoryModel.getMoveBackStack().add(moveEffects);
    }

    private String getPosition(Square square) {
        return columnNames.get(square.getPozX()) // add letter of Square from which move was made
                + Integer.toString(square.getPozY() + 1);// add number of Square from which move was made
    }

    public void clearMoveForwardStack() {
        moveHistoryModel.getMoveForwardStack().clear();
    }

    public JScrollPane getScrollPane() {
        return this.moveHistoryView.getScrollPane();
    }

    public List<String> getMoves() {
        return moveHistoryModel.getMove();
    }

    public Queue<MoveEffect> undo() {
        Queue<MoveEffect> retVal = new LinkedList<>();

        MoveEffect toAdd = null;
        while ((toAdd = undoOne()) != null) {
            retVal.add(toAdd);
            if (toAdd.isFromMove())
                break;
        }

        return retVal;
    }

    public MoveEffect undoOne() {
        MoveEffect last = null;

        if (!moveHistoryModel.getMoveBackStack().isEmpty()) {
            last = moveHistoryModel.getMoveBackStack().pop();
        }

        if (last != null) {
            moveHistoryModel.getMoveForwardStack().push(last);

            if (last.isFromMove()) {
                if (moveHistoryModel.getActivePlayerColumn().equals(MoveHistoryController.PlayerColumn.player1)) {
                    if (moveHistoryModel.getRowCount() > 0)
                        moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 2);

                } else if (moveHistoryModel.getActivePlayerColumn().equals(MoveHistoryController.PlayerColumn.player2)) {
                    moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 0);
                    moveHistoryModel.removeRow(moveHistoryModel.getRowCount() - 1);
                    if (moveHistoryModel.rowsNum > 0)
                        moveHistoryModel.rowsNum--;

                } else {
                    if (moveHistoryModel.getRowCount() > 0)
                        moveHistoryModel.setValueAt("", moveHistoryModel.getRowCount() - 1, 1);

                }
                moveHistoryModel.getMove().remove(moveHistoryModel.getMove().size() - 1);
            }
        }

        return last;
    }

    public Queue<MoveEffect> redo() {
        Queue<MoveEffect> retVal = new LinkedList<>();

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

    public MoveEffect redoOne() {
        try {
            MoveEffect first = moveHistoryModel.getMoveForwardStack().pop();
            addMove(first, true, first.isFromMove());
            return first;
        } catch (java.util.EmptyStackException exc) {
            return null;
        }
    }

    private void addMove(String move) {
        moveHistoryModel.getMove().add(move);
        this.addMoveToTable(move);
    }

    @Override
    public void switchColumns(boolean forward) {
        if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player1))
            moveHistoryModel.setActivePlayerColumn(forward ? PlayerColumn.player2 : PlayerColumn.player3);
        else if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player2))
            moveHistoryModel.setActivePlayerColumn(forward ? PlayerColumn.player3 : PlayerColumn.player1);
        else if (moveHistoryModel.getActivePlayerColumn().equals(PlayerColumn.player3))
            moveHistoryModel.setActivePlayerColumn(forward ? PlayerColumn.player1 : PlayerColumn.player2);
    }
}