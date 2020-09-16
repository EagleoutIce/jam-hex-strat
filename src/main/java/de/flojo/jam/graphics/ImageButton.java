package de.flojo.jam.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.ImageScaleMode;
import de.gurkenlabs.litiengine.resources.Resources;

public class ImageButton extends ImageComponent {

    private BooleanSupplier shouldBeEnabled = null;

    public ImageButton(final String path, final String text, Font font) {
        this(0, 0, Resources.images().get(path), text, font);
    }


    public void setEnabledSupplier(BooleanSupplier shouldBeEnabled) {
        this.shouldBeEnabled = shouldBeEnabled;
    }


    public ImageButton(double width, double height, double x, double y,final BufferedImage img, final String text, Font font) {
        super(x, y, width, height, img);
        setText(text);
        this.setFont(font);
        postsetup();
    }

    @Override
    public void prepare() {
        super.prepare();
        if(shouldBeEnabled != null) {
            this.setEnabled(shouldBeEnabled.getAsBoolean());
        }
    }

    private void postsetup() {
        this.setImageScaleMode(ImageScaleMode.FIT);
        this.setImageValign(Valign.MIDDLE);
        this.setImageAlign(Align.RIGHT);
        this.setTextAlign(Align.LEFT);
    }

    public ImageButton(double x, double y,final BufferedImage img, final String text, Font font) {
        super(x, y, img);
        setText(text);
        this.setFont(font);
        FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle2D dimRect = font.getStringBounds(text, context);
        this.setDimension(dimRect.getWidth() * 1.15f, dimRect.getHeight());
        postsetup();
    }

    public void setColors(Color normal, Color hovered) {
        getAppearance().setForeColor(normal);
        getAppearanceHovered().setForeColor(hovered);
    }
}