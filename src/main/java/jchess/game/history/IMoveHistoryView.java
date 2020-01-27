package jchess.game.history;

import javax.swing.*;

public interface IMoveHistoryView {

    JScrollPane getScrollPane();

    JTable getTable();
}
