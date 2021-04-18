package de.flojo.jam.graphics;

import de.gurkenlabs.litiengine.gui.GuiComponent;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Button extends GuiComponent {

    private final Font font;
    private int margin = 0;

    public Button(final String text, Font font) {
        this(0, 0, text, font, 0);
    }

    public Button(final String text, Font font, int margin) {
        this(0, 0, text, font, margin);
    }

    public Button(double x, double y, final String text, Font font, int margin) {
        super(x, y);
        setText(text);
        this.setFont(font);
        this.font = font;
        this.margin = margin;
        updateDimensions();
    }

    private void updateDimensions() {
        if (this.font == null)
            return;
        FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle2D dimRect = this.font.getStringBounds(this.getText(), context);
        this.setDimension(dimRect.getWidth() * 1.15f + margin, dimRect.getHeight() + margin);
    }

    public void setColors(Color normal, Color hovered) {
        getAppearance().setForeColor(normal);
        getAppearanceHovered().setForeColor(hovered);
    }

    @Override
    public void setText(String arg0) {
        super.setText(arg0);
        updateDimensions();
    }

}
