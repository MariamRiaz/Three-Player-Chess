package jchess.panels.themechoose;

import jchess.io.Images;

public enum Theme {

    DEFAULT(Images.DEFAULT_THEME),
    MATLAK(Images.MATLAK_THEME),
    HUNTER(Images.HUNTER_THEME);

    private String theme;

    private Theme(String theme) {
        this.theme = theme;
    }


    public static Theme getTheme(String themeString) {
        if(themeString.equals(DEFAULT.getThemeString())) {
            return DEFAULT;
        }
        if(themeString.equals(HUNTER.getThemeString())) {
            return HUNTER;
        }
        if(themeString.equals(MATLAK.getThemeString())) {
            return MATLAK;
        }
        return DEFAULT;
    }

    public String getThemeString() {
        return theme;
    }
}
