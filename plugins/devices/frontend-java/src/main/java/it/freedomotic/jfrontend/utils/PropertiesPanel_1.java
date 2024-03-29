/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PropertiesPanel.java
 *
 * Created on 1-ott-2010, 11.57.05
 */
package it.freedomotic.jfrontend.utils;

import it.freedomotic.app.Freedomotic;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 *
 * @author enrico
 */
public class PropertiesPanel_1
        extends javax.swing.JPanel {

    private ArrayList<Component> table;
    private int rows;
    private int cols;
    private static final int MAX_ROWS = 100;

    /**
     * Creates new form PropertiesPanel
     */
    public PropertiesPanel_1(int rows, int cols) {
        initComponents();
        this.setVisible(true);
        this.setPreferredSize(new Dimension(500, 500));
        this.rows = rows;
        this.cols = cols;
        table = new ArrayList<Component>();
        this.setLayout(new SpringLayout());
    }

    public synchronized void addElement(Component component, final int row, final int col) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot add a null component");
        }

        table.add(component);

        if (col == (cols - 1)) { //is the last col
            component.setMaximumSize(new Dimension(2000, 50));
            component.setPreferredSize(new Dimension(200, 50));
        } else {
            component.setMaximumSize(new Dimension(200, 50));
            component.setPreferredSize(new Dimension(200, 50));
        }

        add(component);
    }

    public void layoutPanel() {
        //Lay out the panel.
        System.out.println("row:" + rows + " cols:" + cols);
        SpringUtilities.makeCompactGrid(this, rows, cols, //rows, cols
                5, 5, //initX, initY
                5, 5); //xPad, yPad
        this.validate();
    }

    public int addRow() {
        return rows++;
    }

    public int addColumn() {
        return cols++;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return cols;
    }

    public String getComponent(int row, int col) {
        Component comp = table.get((row * col) + col);

        if (comp != null) {
            if (comp instanceof JTextField) {
                JTextField jTextField = (JTextField) comp;

                return jTextField.getText();
            } else {
                if (comp instanceof JComboBox) {
                    JComboBox combo = (JComboBox) comp;

                    return combo.getSelectedItem().toString();
                }
            }
        }

        return null;
    }

    @Override
    public void removeAll() {
        super.removeAll();
        table.clear();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents(  )
    {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout( this );
        this.setLayout( layout );
        layout.setHorizontalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                                         .addGap( 0, 400, Short.MAX_VALUE ) );
        layout.setVerticalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING )
                                       .addGap( 0, 296, Short.MAX_VALUE ) );
    } // </editor-fold>//GEN-END:initComponents
      // Variables declaration - do not modify//GEN-BEGIN:variables
      // End of variables declaration//GEN-END:variables
}
