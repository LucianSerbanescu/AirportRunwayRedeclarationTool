package com.group17.seg.utility;

import javafx.scene.effect.ColorAdjust;

public class Grayscale {
    public static ColorAdjust grayscale = new ColorAdjust(0, 0, 0, 0);
    public static boolean isGrayscale = false;

    public static void setGrayscale(boolean bool) {
        isGrayscale = bool;
        if (bool) {
            grayscale.setSaturation(-1);
        } else {
            grayscale.setSaturation(0);
        }
    }
}
