package de.flojo.jam.graphics;

import de.flojo.jam.util.ImageUtil;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.ImageScaleMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

public class ImageButton extends ImageComponent {

    private BufferedImage enabled;
    private BufferedImage disabled;
    private BooleanSupplier shouldBeEnabled = null;

    public ImageButton(double width, double height, double x, double y, final BufferedImage img, final String text, Font font) {
        super(x, y, width, height, img);
        updateImage(img);
        setText(text);
        this.setFont(font);
        postSetup();
    }

    public void updateImage(BufferedImage img) {
        this.enabled = img;
        BufferedImage couldBeDisabled;
        try {
            couldBeDisabled = ImageUtil.modifyRGBA(this.enabled, 0.3f, 0.3f, 0.3f, 1);
        } catch (IllegalArgumentException ex) {
            couldBeDisabled = null;
        }
        disabled = couldBeDisabled;
        this.setImage(enabled);
    }

    public void setEnabledSupplier(BooleanSupplier shouldBeEnabled) {
        this.shouldBeEnabled = shouldBeEnabled;
    }

    @Override
    public void prepare() {
        super.prepare();
        if (shouldBeEnabled != null) {
            this.setEnabled(shouldBeEnabled.getAsBoolean());
        }
        updateEnabledImageState();
    }

    private void updateEnabledImageState() {
        if (this.isEnabled()) {
            this.setImage(enabled);
        } else if (disabled != null) {
            this.setImage(disabled);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateEnabledImageState();
    }

    private void postSetup() {
        this.setImageScaleMode(ImageScaleMode.FIT);
        this.setImageValign(Valign.MIDDLE);
        this.setImageAlign(Align.RIGHT);
        this.setTextAlign(Align.LEFT);
    }

    public void setColors(Color normal, Color hovered) {
        getAppearance().setForeColor(normal);
        getAppearanceHovered().setForeColor(hovered);
    }
}