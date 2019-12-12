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
package jchess.pieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.table.*;

import jchess.Game;
import jchess.Log;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Chessboard;
import jchess.UI.board.Square;
import jchess.UI.MovesHistoryView;
import jchess.controller.RoundChessboardController;

import javax.swing.JOptionPane;

/**
 * Class representing the players moves, it's also checking that the moves taken
 * by player are correct. All moves which was taken by current player are saving
 * as List of Strings The history of moves is printing in a table
 *
 * @param game The current game
 */
public class MoveHistory extends AbstractTableModel {

    private ArrayList<String> move = new ArrayList<String>();
    private int columnsNum = 3;
    private int rowsNum = 0;
    private String[] names = new String[]{Settings.lang("white"), Settings.lang("black"), Settings.lang("gray")};
    //private MyDefaultTableModel tableModel;
    //private JScrollPane scrollPane;
    //private JTable table;
    private boolean enterBlack = false;
    private Game game;
    protected Stack<PlayedMove> moveBackStack = new Stack<PlayedMove>();
    protected Stack<PlayedMove> moveForwardStack = new Stack<PlayedMove>();
    private MovesHistoryView movesHistoryView;


    public enum castling {
        none, shortCastling, longCastling
    }

    public MoveHistory(Game game) {
        super();
        this.movesHistoryView = new MovesHistoryView();

        //this.tableModel = new MyDefaultTableModel();
        //this.table = new JTable(this.tableModel);
        //this.scrollPane = new JScrollPane(this.table);
        //this.scrollPane.setMaximumSize(new Dimension(100, 100));
        //this.table.setMinimumSize(new Dimension(100, 100));
        this.game = game;

        this.movesHistoryView.addColumn(this.names[0]);
        this.movesHistoryView.addColumn(this.names[1]);
        this.movesHistoryView.addColumn(this.names[2]);
        this.addTableModelListener(null);
        //this.tableModel.addTableModelListener(null);
        //this.scrollPane.setAutoscrolls(true);
    }

    public void draw() {
    }

    @Override
    public String getValueAt(int x, int y) {
        return this.move.get((y * 2) - 1 + (x - 1));
    }

    @Override
    public int getRowCount() {
        return this.rowsNum;
    }

    @Override
    public int getColumnCount() {
        return this.columnsNum;
    }

	/*protected void addRow() {
		this.tableModel.addRow(new String[2]);
	}*/
    
    protected void addCastling(String move) {
        this.move.remove(this.move.size() - 1);// remove last element (move of Rook)
        if (!this.enterBlack) {
            this.movesHistoryView.setValueAt(move, this.movesHistoryView.getRowCount() - 1, 1);// replace last value
        } else {
            this.movesHistoryView.setValueAt(move, this.movesHistoryView.getRowCount() - 1, 0);// replace last value
        }
        this.move.add(move);// add new move (O-O or O-O-O)
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }

    /**
     * Method of adding new moves to the table
     *
     * @param str String which in is saved player move
     */
    protected void addMove2Table(String str) {
        try {
            if (!this.enterBlack) {
                this.movesHistoryView.addRow();
                this.rowsNum = this.movesHistoryView.getRowCount() - 1;
                this.movesHistoryView.setValueAt(str, rowsNum, 0);
            } else {
                this.movesHistoryView.setValueAt(str, rowsNum, 1);
                this.rowsNum = this.movesHistoryView.getRowCount() - 1;
            }
            this.enterBlack = !this.enterBlack;
            this.movesHistoryView.table.scrollRectToVisible(this.movesHistoryView.table.getCellRect(this.movesHistoryView.table.getRowCount() - 1, 0, true));// scroll to down

        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            if (this.rowsNum > 0) {
                this.rowsNum--;
                addMove2Table(str);
            }
        }
    }

    /**
     * Method of adding new move
     *
     * @param move String which in is capt player move
     */

    public void addMove(Square begin, Square end, Piece beginPiece, Piece beginState, Piece endPiece, boolean registerInHistory, castling castlingMove,
                        boolean wasEnPassant, Piece promotedPiece) {

        boolean wasCastling = castlingMove != castling.none;
        String locMove = new String(beginState.symbol);

        if (game.settings.upsideDown) {
            locMove += Character.toString((char) ((RoundChessboardController.bottom - begin.getPozX()) + 97));// add letter of Square from
            // which move was made
            locMove += Integer.toString(begin.getPozY() + 1);// add number of Square from which move was made
        } else {
            locMove += Character.toString((char) (begin.getPozX() + 97));// add letter of Square from which move was made
            locMove += Integer.toString(8 - begin.getPozY());// add number of Square from which move was made
        }

        if (endPiece != null) {
            locMove += "x";// take down opponent piece
        } else {
            locMove += "-";// normal move
        }

        if (game.settings.upsideDown) {
            locMove += Character.toString((char) ((RoundChessboardController.bottom - end.getPozX()) + 97));// add letter of Square to which
            // move was made
            locMove += Integer.toString(end.getPozY() + 1);// add number of Square to which move was made

        } else {
            locMove += Character.toString((char) (end.getPozX() + 97));// add letter of Square to which move was made
            locMove += Integer.toString(8 - end.getPozY());// add number of Square to which move was made
        }

        if (beginState.symbol.equals("") && begin.getPozX() - end.getPozX() != 0 && wasEnPassant) {
            locMove += "(e.p)";// pawn take down opponent en passant
            wasEnPassant = true;
        }
        if ((!this.enterBlack && this.game.chessboardController.pieceThreatened(this.game.chessboardController.getKingWhite()))
                || (this.enterBlack && this.game.chessboardController.pieceThreatened(this.game.chessboardController.getKingBlack()))) {// if checked

            if ((!this.enterBlack && this.game.chessboardController.pieceUnsavable(this.game.chessboardController.getKingWhite())) // TODO
                    || (this.enterBlack && this.game.chessboardController.pieceUnsavable(this.game.chessboardController.getKingBlack()))) {// check if checkmated
                locMove += "#";// check mate
            } else {
                locMove += "+";// check
            }
        }

        if (castlingMove == castling.shortCastling) {
        } else if (castlingMove == castling.longCastling) {
        } else {
            this.move.add(locMove);
            this.addMove2Table(locMove);
        }
        //this.scrollPane.scrollRectToVisible(new Rectangle(0, this.scrollPane.getHeight() - 2, 1, 1));
        if (registerInHistory) {
            this.moveBackStack.add(new PlayedMove(begin, end, beginPiece, beginState, endPiece, castlingMove,
                    wasEnPassant, promotedPiece));
        }
    }

    public void clearMoveForwardStack() {
        this.moveForwardStack.clear();
    }

    public JScrollPane getScrollPane() {
        return this.movesHistoryView.scrollPane;
    }

    public ArrayList<String> getMoves() {
        return this.move;
    }

    public synchronized PlayedMove getLastMoveFromHistory() {
        try {
            PlayedMove last = this.moveBackStack.get(this.moveBackStack.size() - 1);
            return last;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    public synchronized PlayedMove getNextMoveFromHistory() {
        try {
            PlayedMove next = this.moveForwardStack.get(this.moveForwardStack.size() - 1);
            return next;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }

    }

    public synchronized PlayedMove undo() {
    	if (this.moveBackStack.isEmpty()) {
    		this.enterBlack = false;
    		return null;
    	}
    	
        try {
            PlayedMove last = this.moveBackStack.pop();
            if (last != null) {
                if (this.game.settings.gameType == Settings.gameTypes.local) // moveForward / redo available only for
                // local game
                    this.moveForwardStack.push(last);
                    
                if (this.enterBlack) {
                    this.movesHistoryView.setValueAt("", this.movesHistoryView.getRowCount() - 1, 0);
                    this.movesHistoryView.removeRow(this.movesHistoryView.getRowCount() - 1);

                    if (this.rowsNum > 0)
                        this.rowsNum--;
                } else if (this.movesHistoryView.getRowCount() > 0)
                	this.movesHistoryView.setValueAt("", this.movesHistoryView.getRowCount() - 1, 1);
                
                this.move.remove(this.move.size() - 1);
                this.enterBlack = !this.enterBlack;
            }
            return last;
        } 
        catch (java.lang.ArrayIndexOutOfBoundsException exc) {
        	Log.log(Level.SEVERE, "Inconsistency in MoveHistory.move and MoveHistory.moveBackStack. Index out of bounds.");
            return null;
        }
    }

    public synchronized PlayedMove redo() {
        try {
            if (this.game.settings.gameType == Settings.gameTypes.local) {
                PlayedMove first = this.moveForwardStack.pop();
                this.moveBackStack.push(first);

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
    static public boolean isMoveCorrect(String move) {
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

    public void addMove(String move) {
        if (isMoveCorrect(move)) {
            this.move.add(move);
            this.addMove2Table(move);
            this.moveForwardStack.clear();
        }
    }

    public void addMoves(ArrayList<String> list) {
        for (String singleMove : list) {
            if (isMoveCorrect(singleMove)) {
                this.addMove(singleMove);

            }
        }
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
        ArrayList<String> tempArray = new ArrayList();
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
                    if (this.game.getActivePlayer().color == Player.colors.black) // if black turn
                    {
                        values = new int[]{4, 0, 2, 0};// move value for castling (King move)
                    } else {
                        values = new int[]{4, 7, 2, 7};// move value for castling (King move)
                    }
                } else if (locMove.equals("O-O")) // if short castling
                {
                    if (this.game.getActivePlayer().color == Player.colors.black) // if black turn
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
            boolean pieceFound = false;
            if (locMove.length() <= 3) {
                List<Square> squares = this.game.chessboardController.getSquares();
                xTo = locMove.charAt(from) - 97;// from ASCII
                yTo = RoundChessboardController.bottom - (locMove.charAt(from + 1) - 49);// from ASCII
                for(Square square: squares) {
                    if (square.getPiece() == null
                            || this.game.getActivePlayer().color != square.getPiece().player.color) {
                        continue;
                    }
                    HashSet<Square> pieceMoves = this.game.chessboardController.getValidTargetSquares(square.getPiece());
                    for (Object oldSquare : pieceMoves) {
                        Square currSquare = (Square) oldSquare;
                        if (currSquare.getPozX() == xTo && currSquare.getPozY() == yTo) {
                            xFrom = square.getPozX();
                            yFrom = square.getPozY();
                            pieceFound = true;
                        }
                    }
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
                this.game.chessboardController.setActiveSquare(null);
                return;// finish reading game and show message
            }
        }
    }

    public List<PlayedMove> getLastMoveOfEachPlayer() {
    		List<PlayedMove> ret = new ArrayList<PlayedMove>();
    		if (moveBackStack.isEmpty())
    			return ret;
    		
    		Stack<PlayedMove> temp = new Stack<PlayedMove>();
    		HashMap<Player, Boolean> players = new HashMap<Player, Boolean>();
    		
    		while (!moveBackStack.isEmpty() && !players.containsKey(moveBackStack.lastElement().movedPieceState.player)) {
    			temp.push(moveBackStack.pop());
    			ret.add(temp.lastElement());
    			players.put(temp.lastElement().movedPieceState.player, true);
    		}
    		
    		while (!temp.isEmpty())
    			moveBackStack.push(temp.pop());
    		
    		return ret;
    	}
}
/*
 * Overriding DefaultTableModel and isCellEditable method (history cannot be
 * edited by player)
 */

/*
class MyDefaultTableModel extends DefaultTableModel {

	MyDefaultTableModel() {
		super();
	}

	@Override
	public boolean isCellEditable(int a, int b) {
		return false;
	}
}*/
