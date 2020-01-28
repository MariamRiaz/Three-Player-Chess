package jchess.game.history;


import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Interface that must be implemented by a MoveHistoryView.
 */
public interface IMoveHistoryView {

    /**
     * getter for the Views ScrollPane
     *
     * @return ScrollPane of View
     */
    JScrollPane getScrollPane();

    /**
     * getter for the Table of the MoveHistoryView
     *
     * @return JTable of View
     */
    JTable getTable();
}
