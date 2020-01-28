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
package jchess.io;

import jchess.JChessApp;
import jchess.pieces.Piece;
import jchess.logging.Log;

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

    public static final String THEME_PROPERTY = "THEME";

    /*
     * Method load image by a given name with extension
     *
     * @name : string of image to load for ex. "chessboard.jpg"
     *
     * @returns : image or null if cannot load
     */
    public static Image loadImage(String name) {
        Image img = null;
        Toolkit tk = Toolkit.getDefaultToolkit();
        try {
            String imageLink = Images.THEME_FOLDER + "/"
                    + "default" + "/" +
                    Images.IMAGES_FOLDER + "/" + name;
            URL url = JChessApp.class.getClassLoader().getResource(imageLink);
            img = tk.getImage(url);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "some error loading image!");
            e.printStackTrace();
        }
        return img;
    }

    public static File getResource(String resourcePath) {
        URL url = JChessApp.class.getClassLoader().getResource(resourcePath);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            Log.log(Level.WARNING, "Cannot get resource for " + url.toString());
            return new File("");
        }
    }

    public static Image loadPieceImage(Piece piece) {
        Image img = null;
        Toolkit tk = Toolkit.getDefaultToolkit();
        try {
            String imageLink = Images.THEME_FOLDER + "/"
                    + "default"  + "/" + Images.IMAGES_FOLDER +
                    "/" + piece.getDefinition().getType()
                    + piece.getPlayer().getColor().getColor() + Images.PNG_EXTENSION;
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
        } catch (IOException e) {
            Log.log(Level.WARNING, "Config file " + configFilePath + " could not be found");
            confFile.setProperty(ResourceLoader.THEME_PROPERTY, Images.DEFAULT_THEME);
        }
        return confFile;
    }
}