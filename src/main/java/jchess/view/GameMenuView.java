package jchess.view;

import jchess.Game;
import jchess.helper.GUI;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

public class GameMenuView {
    javax.swing.JMenu gameMenu;
    private javax.swing.JMenuItem moveBackItem;
    private javax.swing.JMenuItem moveForwardItem;
    private javax.swing.JMenuItem rewindToBegin;
    private javax.swing.JMenuItem rewindToEnd;
    private GUI gui = null;
    private javax.swing.JTabbedPane gamesPane;
    public GameMenuView(ResourceMap resourceMap, GUI gui, JTabbedPane gamesPane) {
        this.gameMenu = new javax.swing.JMenu();
        this.gui = gui;
        this.gamesPane = gamesPane;
        moveBackItem = new javax.swing.JMenuItem();
        moveForwardItem = new javax.swing.JMenuItem();
        rewindToBegin = new javax.swing.JMenuItem();
        rewindToEnd = new javax.swing.JMenuItem();
        gameMenu.setText(resourceMap.getString("gameMenu.text")); // NOI18N
        gameMenu.setName("gameMenu"); // NOI18N
        gameMenu.add(moveBackItem);
        moveBackItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        moveBackItem.setText(resourceMap.getString("moveBackItem.text")); // NOI18N
        moveBackItem.setName("moveBackItem"); // NOI18N
        moveBackItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moveBackItemMouseClicked(evt);
            }
        });
        moveBackItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveBackItemActionPerformed(evt);
            }
        });
        moveForwardItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        moveForwardItem.setText(resourceMap.getString("moveForwardItem.text")); // NOI18N
        moveForwardItem.setName("moveForwardItem"); // NOI18N
        moveForwardItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moveForwardItemMouseClicked(evt);
            }
        });
        moveForwardItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveForwardItemActionPerformed(evt);
            }
        });
        gameMenu.add(moveForwardItem);

        rewindToBegin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        rewindToBegin.setText(resourceMap.getString("rewindToBegin.text")); // NOI18N
        rewindToBegin.setName("rewindToBegin"); // NOI18N
        rewindToBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindToBeginActionPerformed(evt);
            }
        });
        gameMenu.add(rewindToBegin);

        rewindToEnd.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        rewindToEnd.setText(resourceMap.getString("rewindToEnd.text")); // NOI18N
        rewindToEnd.setName("rewindToEnd"); // NOI18N
        rewindToEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindToEndActionPerformed(evt);
            }
        });
        gameMenu.add(rewindToEnd);

    }

    private void moveBackItemMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_moveBackItemMouseClicked
    {//GEN-HEADEREND:event_moveBackItemMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_moveBackItemMouseClicked

    private void moveBackItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveBackItemActionPerformed
    {//GEN-HEADEREND:event_moveBackItemActionPerformed
        if (gui != null && gui.game != null) {
            gui.game.undo();
        } else {
            try {
                Game activeGame = this.getActiveTabGame();
                if (!activeGame.undo()) {
                    JOptionPane.showMessageDialog(null, "Nie da sie cofnac!");
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
                JOptionPane.showMessageDialog(null, "Brak aktywnej karty!");
            } catch (UnsupportedOperationException exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage());
            }
        }

    }//GEN-LAST:event_moveBackItemActionPerformed

    private Game getActiveTabGame() {
        Game activeGame = (Game) this.gamesPane.getComponentAt(this.gamesPane.getSelectedIndex());
        return activeGame;
    }

    private void moveForwardItemMouseClicked(java.awt.event.MouseEvent evt){ }
    private void moveForwardItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveForwardItemActionPerformed
    {
        if (gui != null && gui.game != null) {
            gui.game.redo();
        } else {
            try {
                Game activeGame = this.getActiveTabGame();
                if (!activeGame.redo()) {
                    JOptionPane.showMessageDialog(null, "W pamieci brak ruchow do przodu!");
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
                JOptionPane.showMessageDialog(null, "Brak aktywnej karty!");
            } catch (UnsupportedOperationException exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage());
            }
        }
    }//GEN-LAST:event_moveForwardItemActionPerformed
    private void rewindToBeginActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rewindToBeginActionPerformed
    {//GEN-HEADEREND:event_rewindToBeginActionPerformed
        try {
            Game activeGame = this.getActiveTabGame();
            if (!activeGame.rewindToBegin()) {
                JOptionPane.showMessageDialog(null, "W pamieci brak ruchow do przodu!");
            }
        } catch (ArrayIndexOutOfBoundsException exc) {
            JOptionPane.showMessageDialog(null, "Brak aktywnej karty!");
        } catch (UnsupportedOperationException exc) {
            JOptionPane.showMessageDialog(null, exc.getMessage());
        }
    }//GEN-LAST:event_rewindToBeginActionPerformed
    private void rewindToEndActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rewindToEndActionPerformed
    {//GEN-HEADEREND:event_rewindToEndActionPerformed
        try {
            Game activeGame = this.getActiveTabGame();
            if (!activeGame.rewindToEnd()) {
                JOptionPane.showMessageDialog(null, "W pamieci brak ruchow wstecz!");
            }
        } catch (ArrayIndexOutOfBoundsException exc) {
            JOptionPane.showMessageDialog(null, "Brak aktywnej karty!");
        } catch (UnsupportedOperationException exc) {
            JOptionPane.showMessageDialog(null, exc.getMessage());
        }
    }//GEN-LAST:event_rewindToEndActionPerformed
}