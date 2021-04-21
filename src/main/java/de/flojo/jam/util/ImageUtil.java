package de.flojo.jam.util;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {
    private ImageUtil() {
    }

    public static BufferedImage modifyRGBA(BufferedImage image, float r, float g, float b, float a) {
        BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        new RescaleOp(new float[]{r, g, b, a}, new float[4], null).filter(image, target);
        return target;
    }

    public static void modifyRGBA(BufferedImage image, BufferedImage target, float r, float g, float b, float a) {
        new RescaleOp(new float[]{r, g, b, a}, new float[4], null).filter(image, target);
    }

    public static boolean isTransparent(BufferedImage image, int x, int y) {
        int pixel = image.getRGB(x, y);
        return (pixel >> 24) == 0x00;
    }
}
