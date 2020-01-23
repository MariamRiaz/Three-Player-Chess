package jchess.view;

import org.jdesktop.application.ResourceMap;

public class OptionsMenuView {
    javax.swing.JMenu optionsMenu;
    javax.swing.JMenuItem themeSettingsMenu;

    public OptionsMenuView(ResourceMap resourceMap){
        optionsMenu = new javax.swing.JMenu();
        themeSettingsMenu = new javax.swing.JMenuItem();

        optionsMenu.setText(resourceMap.getString("optionsMenu.text")); // NOI18N
        optionsMenu.setName("optionsMenu"); // NOI18N

        themeSettingsMenu.setText(resourceMap.getString("themeSettingsMenu.text")); // NOI18N
        themeSettingsMenu.setName("themeSettingsMenu"); // NOI18N
        optionsMenu.add(themeSettingsMenu);
    }
}
