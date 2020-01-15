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

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;

import jchess.helper.ResourceLoader;
import jchess.JChessApp;
import jchess.helper.Log;
import jchess.Settings;

import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.io.FileOutputStream;

public class ThemeChooseWindow extends JDialog implements ActionListener, ListSelectionListener {

    JList themesList;
    ImageIcon themePreview;
    GridBagLayout gbl;
    public String result;
    GridBagConstraints gbc;
    JButton themePreviewButton;
    JButton okButton;

    public ThemeChooseWindow(Frame parent) throws Exception {
        super(parent);

        File dir = new File(ThemeChooseWindow.class.getClassLoader().getResource("theme/").toURI());

        Log.log("Theme path: " + dir.getPath());

        File[] files = dir.listFiles();
        if (files != null && dir.exists()) {
            this.setTitle(Settings.lang("choose_theme_window_title"));
            Dimension winDim = new Dimension(550, 230);
            this.setMinimumSize(winDim);
            this.setMaximumSize(winDim);
            this.setSize(winDim);
            this.setResizable(false);
            this.setLayout(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            String[] dirNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                dirNames[i] = files[i].getName();
            }
            this.themesList = new JList(dirNames);
            this.themesList.setLocation(new Point(10, 10));
            this.themesList.setSize(new Dimension(100, 120));
            this.add(this.themesList);
            this.themesList.setSelectionMode(0);
            this.themesList.addListSelectionListener(this);
            Properties prp = ResourceLoader.getConfigFile();

            this.gbl = new GridBagLayout();
            this.gbc = new GridBagConstraints();
            try {
                this.themePreview = new ImageIcon(ResourceLoader.loadImage("Preview.png"));// JChessApp.class.getResource("theme/"+GUI.configFile.getProperty("THEME")+"/images/Preview.png"));
            } catch (java.lang.NullPointerException exc) {
                Log.log(Level.SEVERE, "Cannot find preview image: " + exc);
                this.themePreview = new ImageIcon(JChessApp.class.getResource("theme/noPreview.png"));
                return;
            }
            this.result = "";
            this.themePreviewButton = new JButton(this.themePreview);
            this.themePreviewButton.setLocation(new Point(110, 10));
            this.themePreviewButton.setSize(new Dimension(420, 120));
            this.add(this.themePreviewButton);
            this.okButton = new JButton("OK");
            this.okButton.setLocation(new Point(175, 140));
            this.okButton.setSize(new Dimension(200, 50));
            this.add(this.okButton);
            this.okButton.addActionListener(this);
            this.setModal(true);
        } else {
            throw new Exception(Settings.lang("error_when_creating_theme_config_window"));
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        String element = this.themesList.getModel().getElementAt(this.themesList.getSelectedIndex()).toString();
        String path = ThemeChooseWindow.class.getClassLoader().getResource("theme/").toString();
        Log.log(path + element + "/images/Preview.png");
        this.themePreview = new ImageIcon(path + element + "/images/Preview.png");
        this.themePreviewButton.setIcon(this.themePreview);
        repaint();
    }

    /**
     * Method wich is changing a pawn into queen, rook, bishop or knight
     *
     * @param evt The event generated by the component
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.okButton) {
            Properties prp = ResourceLoader.getConfigFile();
            int element = this.themesList.getSelectedIndex();
            String name = this.themesList.getModel().getElementAt(element).toString();
            prp.setProperty("THEME", name);
            try {
            	File properties = new File(ThemeChooseWindow.class.getClassLoader().getResource("JChessApp.properties").toURI());
                FileOutputStream outStream = new FileOutputStream(properties);
                prp.store(outStream, "Stored");
                outStream.flush();
                outStream.close();
            } catch (IOException | URISyntaxException exc) {
            }
            JOptionPane.showMessageDialog(this, Settings.lang("changes_visible_after_restart"));
            this.setVisible(false);
			Log.log(prp.getProperty("THEME"));
        }
    }
}
