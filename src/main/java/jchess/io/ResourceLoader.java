package jchess.io;

import jchess.JChessApp;
import jchess.logging.Log;
import jchess.pieces.Piece;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Class representing the game interface which is seen by a player and where are
 * lockated available for player opptions, current games and where can he start
 * a new game (load it or save it)
 */
public class ResourceLoader {

    public static final String THEME_PROPERTY = "THEME";

    private static final String RESOURCE_BUNDLE_KEY = "i18n.main";

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

    public static String getTexts(String key) {

        ResourceBundle bundle = PropertyResourceBundle.getBundle(RESOURCE_BUNDLE_KEY);
        String result;
        try {
            result = bundle.getString(key);
        } catch (java.util.MissingResourceException exc) {
            result = key;
        }
        return result;
    }

    public static Image loadPieceImage(Piece piece) {
        Image img = null;
        Toolkit tk = Toolkit.getDefaultToolkit();
        try {
            String imageLink = Images.THEME_FOLDER + "/"
                    + "default" + "/" + Images.IMAGES_FOLDER +
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