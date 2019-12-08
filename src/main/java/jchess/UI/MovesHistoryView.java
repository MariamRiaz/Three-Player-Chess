package jchess.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovesHistoryView {

    public MyDefaultTableModel tableModel;
    public JScrollPane scrollPane;
    public JTable table;

    public MovesHistoryView(){
        this.tableModel = new MyDefaultTableModel();
        this.table = new JTable(this.tableModel);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setMaximumSize(new Dimension(100, 100));
        this.table.setMinimumSize(new Dimension(100, 100));
        this.scrollPane.setAutoscrolls(true);
        this.tableModel.addTableModelListener(null);
    }

    public void addColumn(String name){
        this.tableModel.addColumn(name);
    }

    public void addRow(){
        this.tableModel.addRow(new String[2]);
    }

    public void setValueAt(String move, int row, int column){
        this.tableModel.setValueAt(move, row, column);
    }

    public int getRowCount(){
        return this.tableModel.getRowCount();
    }

    public void removeRow(int row){
        this.tableModel.removeRow(row);
    }

}

class MyDefaultTableModel extends DefaultTableModel {

    MyDefaultTableModel() {
        super();
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}
