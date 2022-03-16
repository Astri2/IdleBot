package me.astri.idleBot.GameBot.entities;

import java.awt.*;

public enum ColorEnum {
    BLACK(Color.BLACK),
    BLUE(Color.BLUE),
    CYAN(Color.CYAN),
    DARK_GRAY(Color.DARK_GRAY),
    GRAY(Color.GRAY),
    GREEN(Color.GREEN),
    LIGHT_GRAY(Color.LIGHT_GRAY),
    MAGENTA(Color.MAGENTA),
    ORANGE(Color.ORANGE),
    PINK(Color.PINK),
    RED(Color.RED),
    WHITE(Color.WHITE),
    YELLOW(Color.YELLOW),
    PURPLE(new Color(0x9b59b6));

    private final Color color;

    ColorEnum(Color a) {
        color = a;
    }

    public Color getColor() {
        return color;
    }
}
