package jchess.view;

import jchess.model.AbstractMoveHistoryModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/*
 * All moves which was taken by current player will be displayed in the table generated by this class
 * it a view for Move History class.
 * */

public class MoveHistoryView implements IMoveHistoryView {

    private JScrollPane scrollPane;
    private JTable table;
    private ResourceMap resources = new ResourceMap(Application.getInstance().getContext().getResourceMap(),
            MoveHistoryView.class.getClassLoader(), "MoveHistoryView");

    public MoveHistoryView(AbstractMoveHistoryModel model) {
        int dimension = resources.getInteger("dimension");
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setMaximumSize(new Dimension(dimension, dimension));
        this.table.setMinimumSize(new Dimension(dimension, dimension));
        this.scrollPane.setAutoscrolls(true);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTable getTable() {
        return table;
    }
}
