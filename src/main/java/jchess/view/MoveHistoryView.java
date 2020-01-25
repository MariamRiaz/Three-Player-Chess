package jchess.view;

import jchess.model.MoveHistoryModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/*
 * All moves which was taken by current player will be displayed in the table generated by this class
 * it a view for Move History class.
 * */

public class MoveHistoryView {

    public JScrollPane scrollPane;
    public JTable table;
    ResourceMap resources = new ResourceMap(Application.getInstance().getContext().getResourceMap(),
            MoveHistoryView.class.getClassLoader(), "MoveHistoryView");

    public MoveHistoryView(MoveHistoryModel model) {
        int dimension = resources.getInteger("dimension");
        this.table = new JTable(model);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setMaximumSize(new Dimension(dimension, dimension));
        this.table.setMinimumSize(new Dimension(dimension, dimension));
        this.scrollPane.setAutoscrolls(true);
    }
}
