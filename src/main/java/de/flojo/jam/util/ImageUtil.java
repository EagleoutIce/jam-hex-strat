package de.flojo.jam.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {
    private ImageUtil() {
    }

    public static BufferedImage modifyRGBA(BufferedImage image, float r, float g, float b, float a) {
        final var target = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

    public static Image scale(BufferedImage image, float scale) {
        return image.getScaledInstance((int) (image.getWidth() * scale), (int) (image.getHeight() * scale),
                                       Image.SCALE_SMOOTH);
    }
}
