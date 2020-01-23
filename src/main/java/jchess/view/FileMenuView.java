package jchess.view;

import jchess.Game;
import jchess.JChessApp;
import jchess.Settings;
import jchess.helper.Log;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

public class FileMenuView {

    javax.swing.JMenuItem newGameItem;
    javax.swing.JMenuItem loadGameItem;
    javax.swing.JMenuItem saveGameItem;
    javax.swing.JMenu fileMenu;
    public FileMenuView(ResourceMap resourceMap){
        fileMenu = new javax.swing.JMenu();
        newGameItem = new javax.swing.JMenuItem();
        loadGameItem = new javax.swing.JMenuItem();
        saveGameItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        newGameItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newGameItem.setText(resourceMap.getString("newGameItem.text")); // NOI18N
        newGameItem.setName("newGameItem"); // NOI18N
        fileMenu.add(newGameItem);
        loadGameItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        loadGameItem.setText(resourceMap.getString("loadGameItem.text")); // NOI18N
        loadGameItem.setName("loadGameItem"); // NOI18N
        fileMenu.add(loadGameItem);
        saveGameItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveGameItem.setText(resourceMap.getString("saveGameItem.text")); // NOI18N
        saveGameItem.setName("saveGameItem"); // NOI18N
        fileMenu.add(saveGameItem);
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(jchess.JChessApp.class).getContext().getActionMap(FileMenuView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);
    }
}
