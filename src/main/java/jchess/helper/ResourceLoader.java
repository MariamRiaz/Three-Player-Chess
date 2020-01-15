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
package jchess.helper;

import jchess.JChessApp;

import java.awt.*;
import java.net.*;
import java.io.*;

import java.util.Properties;
import java.util.logging.Level;

/**
 * Class representing the game interface which is seen by a player and where are
 * lockated available for player opptions, current games and where can he start
 * a new game (load it or save it)
 */
public class ResourceLoader {

    static final public Properties configFile = ResourceLoader.getConfigFile();

    /*
     * Method load image by a given name with extension
     *
     * @name : string of image to load for ex. "chessboard.jpg"
     *
     * @returns : image or null if cannot load
     */
    public static Image loadImage(String name) {
        if (configFile == null) {
            return null;
        }
        Image img = null;
        Toolkit tk = Toolkit.getDefaultToolkit();
        try {
            String imageLink = "theme/" + configFile.getProperty("THEME") + "/images/" + name;
            URL url = JChessApp.class.getClassLoader().getResource(imageLink);
            img = tk.getImage(url);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "some error loading image!");
            e.printStackTrace();
        }
        return img;
    }

    public static Properties getConfigFile() {
        Properties confFile = new Properties();
        String configFilePath = "JChessApp.properties";
        try {
            confFile.load(ResourceLoader.class.getClassLoader().getResourceAsStream(configFilePath));
        } catch(IOException e) {
            Log.log(Level.WARNING, "Config file " + configFilePath + " could not be found");
            confFile.setProperty("THEME", "default");
        }
        return confFile;
    }
}
