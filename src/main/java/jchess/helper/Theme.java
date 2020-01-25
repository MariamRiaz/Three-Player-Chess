package jchess.helper;

public enum Theme {

    DEFAULT(Images.DEFAULT_THEME),
    MATLAK(Images.MATLAK_THEME),
    HUNTER(Images.HUNTER_THEME);

    private String theme;

    private Theme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }
}
