package jchess.entities;

import jchess.model.Images;

public enum PlayerColor {

    BLACK(Images.BLACK_COLOR),
    GREY(Images.GREY_COLOR),
    WHITE(Images.WHITE_COLOR);

    private final String color;

    private PlayerColor(String color) {
        this.color = color;
    }


    public static PlayerColor getColor(String colorString) {
        if(colorString.equals(BLACK.color)) {
            return BLACK;
        }
        if(colorString.equals(WHITE.color)) {
            return WHITE;
        }
        if(colorString.equals(GREY.color)) {
            return GREY;
        }
        return WHITE;
    }

    public String getColor() {
        return color;
    }
}
