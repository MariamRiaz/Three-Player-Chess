/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jchess.view;

import jchess.*;
import jchess.helper.GUI;
import jchess.helper.Log;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Level;

/**
 * The application's main frame.
 */
public class JChessView extends FrameView {
    // Variables declaration - do not modify//GEN-BEGIN:variables

    public javax.swing.JTabbedPane gamesPane;
    public javax.swing.JMenuItem loadGameItem;
    public javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;

    public javax.swing.JMenuItem newGameItem;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JProgressBar progressBar;

    public javax.swing.JMenuItem saveGameItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    public javax.swing.JMenuItem themeSettingsMenu;
    // End of variables declaration//GEN-END:variables
    //private JTabbedPaneWithIcon gamesPane;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private PawnPromotionWindow promotionBox;
    public JDialog newGameFrame;
    static GUI gui = null;
    GUI activeGUI;//in future it will be reference to active tab

    public JChessView(SingleFrameApplication app) {
        super(app);

        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = new ResourceMap(getResourceMap(), JChessView.class.getClassLoader(), "JChessApp", "JChessView");
        int messageTimeout = Integer.parseInt(resourceMap.getString("StatusBar.messageTimeout"));
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = Integer.parseInt(resourceMap.getString("StatusBar.busyAnimationRate"));
        for (int i = 0; i < busyIcons.length; i++) {
            String path = resourceMap.getString("StatusBar.busyIcons[" + i + "]");
            busyIcons[i] = new ImageIcon(JChessView.class.getClassLoader().getResource(path));
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        String path = resourceMap.getString("StatusBar.idleIcon");
        idleIcon = new ImageIcon(JChessView.class.getClassLoader().getResource(path));
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

    }

    public Game addNewTab(String title) {
        Game newGUI = new Game();
        this.gamesPane.addTab(title, newGUI);
        return newGUI;
    }



    ///--endOf- don't delete, becouse they're interfaces for MouseEvent




    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JChessApp.getApplication().getMainFrame();
            aboutBox = new JChessAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JChessApp.getApplication().show(aboutBox);
    }

    public String showPawnPromotionBox(String color) {
        if (promotionBox == null) {
            JFrame mainFrame = JChessApp.getApplication().getMainFrame();
            promotionBox = new PawnPromotionWindow(mainFrame, color);
            promotionBox.setLocationRelativeTo(mainFrame);
            promotionBox.setModal(true);

        }
        promotionBox.setColor(color);
        JChessApp.getApplication().show(promotionBox);

        return promotionBox.result;
    }

    public String showSaveWindow() {

        return "";
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        gamesPane = new JChessTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newGameItem = new javax.swing.JMenuItem();
        loadGameItem = new javax.swing.JMenuItem();
        saveGameItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();


        optionsMenu = new javax.swing.JMenu();
        themeSettingsMenu = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setMaximumSize(new java.awt.Dimension(800, 600));
        mainPanel.setMinimumSize(new java.awt.Dimension(800, 600));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        gamesPane.setName("gamesPane"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(gamesPane, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(gamesPane, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        //org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(jchess.JChessApp.class).getContext().getResourceMap(JChessView.class);
//        ResourceMap parentMap = new ResourceMap(null, JChessView.class.getClassLoader(), "JChessApp");
        ResourceMap resourceMap = new ResourceMap(getResourceMap(), JChessView.class.getClassLoader(), "JChessView", "JChessApp");
        //new ResourceMap(getResourceMap(), JChessView.class.getClassLoader(), "JChessView");
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


        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(jchess.JChessApp.class).getContext().getActionMap(JChessView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);





        GameMenuView gameMenuView = new GameMenuView(resourceMap, gui, gamesPane);
        menuBar.add(gameMenuView.gameMenu);

        optionsMenu.setText(resourceMap.getString("optionsMenu.text")); // NOI18N
        optionsMenu.setName("optionsMenu"); // NOI18N

        themeSettingsMenu.setText(resourceMap.getString("themeSettingsMenu.text")); // NOI18N
        themeSettingsMenu.setName("themeSettingsMenu"); // NOI18N
        optionsMenu.add(themeSettingsMenu);

        menuBar.add(optionsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(statusMessageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 616, Short.MAX_VALUE)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusAnimationLabel)
                                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(statusMessageLabel)
                                        .addComponent(statusAnimationLabel)
                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    public Game getActiveTabGame() throws ArrayIndexOutOfBoundsException {
        Game activeGame = (Game) this.gamesPane.getComponentAt(this.gamesPane.getSelectedIndex());
        return activeGame;
    }

    public int getNumberOfOpenedTabs() {
        return this.gamesPane.getTabCount();
    }

}
