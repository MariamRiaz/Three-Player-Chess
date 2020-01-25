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
import jchess.helper.Log;
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
import java.io.File;
import jchess.view.PawnPromotionWindow;
import jchess.view.FileMenuView;
import jchess.view.OptionsMenuView;
import jchess.view.HelpMenuView;
import java.util.logging.Level;
/**
 * The application's main frame.
 */

public class JChessView extends FrameView implements ActionListener {
    // Variables declaration - do not modify//GEN-BEGIN:variables

    public javax.swing.JTabbedPane gamesPane;
    public javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    //private JTabbedPaneWithIcon gamesPane;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private PawnPromotionWindow promotionBox;
    public JDialog newGameFrame;
    private FileMenuView fileMenuView;
    private OptionsMenuView optionsMenuView;
    private HelpMenuView helpMenuView;

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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        gamesPane = new JChessTabbedPane();
        menuBar = new javax.swing.JMenuBar();


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

        fileMenuView = new FileMenuView(resourceMap);
        menuBar.add(fileMenuView.fileMenu);

        GameMenuView gameMenuView = new GameMenuView(resourceMap, gui, gamesPane);
        menuBar.add(gameMenuView.gameMenu);

        optionsMenuView = new OptionsMenuView(resourceMap);
        menuBar.add(optionsMenuView.optionsMenu);

        helpMenuView = new HelpMenuView(resourceMap);
        menuBar.add(helpMenuView.helpMenu);

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

        fileMenuView.newGameItem.addActionListener(this);
        fileMenuView.loadGameItem.addActionListener(this);
        fileMenuView.saveGameItem.addActionListener(this);
        optionsMenuView.themeSettingsMenu.addActionListener(this);
    }// </editor-fold>//GEN-END:initComponents
    public Game getActiveTabGame() throws ArrayIndexOutOfBoundsException {
        Game activeGame = (Game) this.gamesPane.getComponentAt(this.gamesPane.getSelectedIndex());
        return activeGame;
    }
    public int getNumberOfOpenedTabs() {
        return this.gamesPane.getTabCount();
    }
    public void actionPerformed(ActionEvent event) {
        Object target = event.getSource();
        if (target == fileMenuView.newGameItem) {
            this.newGameFrame = new NewGameWindow();
            JChessApp.getApplication().show(this.newGameFrame);
        } else if (target == fileMenuView.saveGameItem) { //saveGame
            if (this.gamesPane.getTabCount() == 0) {
                JOptionPane.showMessageDialog(null, Settings.lang("save_not_called_for_tab"));
                return;
            }
            while (true) {//until
                JFileChooser fc = new JFileChooser();
                int retVal = fc.showSaveDialog(this.gamesPane);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selFile = fc.getSelectedFile();
                    Game tempGUI = (Game) this.gamesPane.getComponentAt(this.gamesPane.getSelectedIndex());
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
        } else if (target == fileMenuView.loadGameItem) { //loadGame
            JFileChooser fc = new JFileChooser();
            int retVal = fc.showOpenDialog(this.gamesPane);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.exists() && file.canRead()) {
                    Game.loadGame(file);
                }
            }
        } else if (target == optionsMenuView.themeSettingsMenu) {
            try {
                ThemeChooseWindow choose = new ThemeChooseWindow(this.getFrame());
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
}