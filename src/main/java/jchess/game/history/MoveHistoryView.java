package jchess.game.history;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;


import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Class that represents the View of the MoveHistory Component.
 */
public class MoveHistoryView implements IMoveHistoryView {

    private JScrollPane scrollPane;
    private JTable table;
    private ResourceMap resources = new ResourceMap(Application.getInstance().getContext().getResourceMap(),
            MoveHistoryView.class.getClassLoader(), "MoveHistoryView");

    /**
     * Constructor of the MovesHistoryView Class
     *
     * @param model Model of the MoveHistory Component
     */
    public MoveHistoryView(AbstractMoveHistoryModel model) {
        int dimension = resources.getInteger("dimension");
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setMaximumSize(new Dimension(dimension, dimension));
        this.table.setMinimumSize(new Dimension(dimension, dimension));
        this.scrollPane.setAutoscrolls(true);
    }

    /**
     * {@inheritDoc}
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * {@inheritDoc}
     */
    public JTable getTable() {
        return table;
    }
}
