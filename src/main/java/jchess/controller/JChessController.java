package jchess.controller;

import jchess.Game;
import jchess.JChessApp;
import jchess.Settings;
import jchess.helper.Log;
import jchess.view.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

public class JChessController implements ActionListener {

    private JChessView jChessView;


    public JChessController(JChessView jcv){
        this.jChessView = jcv;
        jChessView.newGameItem.addActionListener(this);
        jChessView.loadGameItem.addActionListener(this);
        jChessView.saveGameItem.addActionListener(this);
        jChessView.themeSettingsMenu.addActionListener(this);
    }


    public void actionPerformed(ActionEvent event) {
        Object target = event.getSource();
        if (target == jChessView.newGameItem) {
            jChessView.newGameFrame = new NewGameWindow();
            JChessApp.getApplication().show(jChessView.newGameFrame);
        } else if (target == jChessView.saveGameItem) { //saveGame
            if (jChessView.gamesPane.getTabCount() == 0) {
                JOptionPane.showMessageDialog(null, Settings.lang("save_not_called_for_tab"));
                return;
            }
            while (true) {//until
                JFileChooser fc = new JFileChooser();
                int retVal = fc.showSaveDialog(jChessView.gamesPane);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selFile = fc.getSelectedFile();
                    Game tempGUI = (Game) jChessView.gamesPane.getComponentAt(jChessView.gamesPane.getSelectedIndex());
                    if (!selFile.exists()) {
                        try {
                            selFile.createNewFile();
                        } catch (java.io.IOException exc) {
                            Log.log(Level.SEVERE, "error creating file: " + exc);
                        }
                    } else if (selFile.exists()) {
                        int opt = JOptionPane.showConfirmDialog(tempGUI, Settings.lang("file_exists"), Settings.lang("file_exists"), JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.NO_OPTION)//if user choose to now overwrite
                        {
                            continue; // go back to file choose
                        }
                    }
                    if (selFile.canWrite()) {
                        tempGUI.saveGame(selFile);
                    }
                    Log.log(fc.getSelectedFile().isFile());
                    break;
                } else if (retVal == JFileChooser.CANCEL_OPTION) {
                    break;
                }
                ///JChessView.gui.game.saveGame(fc.);
            }
        } else if (target == jChessView.loadGameItem) { //loadGame
            JFileChooser fc = new JFileChooser();
            int retVal = fc.showOpenDialog(jChessView.gamesPane);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.exists() && file.canRead()) {
                    Game.loadGame(file);
                }
            }
        } else if (target == jChessView.themeSettingsMenu) {
            try {
                ThemeChooseWindow choose = new ThemeChooseWindow(jChessView.getFrame());
                JChessApp.getApplication().show(choose);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(
                        JChessApp.getApplication().getMainFrame(),
                        exc.getMessage()
                );
                Log.log(Level.SEVERE, "Something wrong creating window - perhaps themeList is null");
            }
        }
    }

    public Game addNewTab(String title) {
        Game newGUI = new Game();
        jChessView.gamesPane.addTab(title, newGUI);
        return newGUI;
    }

//    @Action
//    public void showAboutBox() {
//        if (jChessView.aboutBox == null) {
//            JFrame mainFrame = JChessApp.getApplication().getMainFrame();
//            aboutBox = new JChessAboutBox(mainFrame);
//            aboutBox.setLocationRelativeTo(mainFrame);
//        }
//        JChessApp.getApplication().show(aboutBox);
//    }

}
