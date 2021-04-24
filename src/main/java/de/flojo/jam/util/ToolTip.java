package de.flojo.jam.util;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ToolTip<T extends GuiComponent> implements IRenderable {
    public static final Font TOOL_TIP_FONT = new JLabel().getFont().deriveFont(20f);
    private static final Color DEF_BLACK = new Color(0f, 0f, 0f, .65f);
    private static final int WIDTH = 275;
    private static final int PADDING = 3;
    private final Color color;

    private final T host;
    private final AtomicBoolean display = new AtomicBoolean();
    private final AtomicReference<Point2D> mousePosition = new AtomicReference<>();
    private final Supplier<String> textSupplier;

    public ToolTip(T component, String text) {
        this(component, () -> text);
    }


    public ToolTip(T component, String text, Color color) {
        this(component, () -> text, color);
    }


    public ToolTip(T component, Supplier<String> textSupplier) {
        this(component, textSupplier, Color.CYAN);
    }

    public ToolTip(T component, Supplier<String> textSupplier, Color color) {
        this.host = component;
        this.color = color;
        component.onMouseEnter(e -> display.set(true));
        component.onMouseLeave(e -> display.set(false));
        component.onMouseMoved(e -> mousePosition.set(e.getEvent().getPoint()));
        this.textSupplier = textSupplier;
    }

    public static float getHeightWithLinebreaks(Graphics2D g, final String text, final double width) {
        if (text == null || text.isEmpty()) {
            return 0f;
        }
        final var frc = g.getFontRenderContext();
        var textHeight = 0f;
        for (var s : text.split("\n")) {
            final var styledText = new AttributedString(s);
            styledText.addAttribute(TextAttribute.FONT, g.getFont());
            final var iterator = styledText.getIterator();
            final var measurer = new LineBreakMeasurer(iterator, frc);
            while (true) {
                final var nextLayout = measurer.nextLayout((float) width);
                if (nextLayout == null)
                    break;
                textHeight += nextLayout.getAscent() + nextLayout.getDescent();
                if (measurer.getPosition() >= text.length()) {
                    break;
                }
                textHeight += nextLayout.getLeading();
            }
        }
        return textHeight;
    }

    // modified variant, shipped seems to be buggy
    public static void renderWithLinebreaks(final Graphics2D g, final String text, final double x, final double y,
                                            final double width, final double height, final Color firstColor) {
        if (text == null || text.isEmpty()) {
            return;
        }
        final var valign = Valign.TOP;
        final var originalHints = g.getRenderingHints();
        final var color = g.getColor();
        TextRenderer.enableTextAntiAliasing(g);

        final var frc = g.getFontRenderContext();
        List<TextLayout> lines = new ArrayList<>();
        var textHeight = 0f;
        for (var s : text.split("\n")) {
            final var styledText = new AttributedString(s);
            styledText.addAttribute(TextAttribute.FONT, g.getFont());
            final var iterator = styledText.getIterator();
            final var measurer = new LineBreakMeasurer(iterator, frc);
            while (true) {
                final var nextLayout = measurer.nextLayout((float) width);
                if (nextLayout == null)
                    break;
                lines.add(nextLayout);
                textHeight += nextLayout.getAscent() + nextLayout.getDescent();
                if (measurer.getPosition() >= text.length()) {
                    break;
                }
                textHeight += nextLayout.getLeading();
            }
        }
        var textY = (float) (y + valign.getLocation(height, textHeight));
        var first = true;
        for (TextLayout layout : lines) {
            if (first) {
                first = false;
                g.setColor(firstColor);
            } else {
                g.setColor(color);
            }
            textY += layout.getAscent();
            layout.draw(g, (float) x, textY);
            textY += layout.getDescent() + layout.getLeading();
        }
        g.setRenderingHints(originalHints);
    }

    @Override
    public void render(final Graphics2D g) {
        if (!display.get())
            return;
        if (!host.isEnabled() || host.isSuspended()) {
            display.set(false);
            return;
        }
        final String text = textSupplier.get();
        final Point2D position = mousePosition.get();
        g.setColor(DEF_BLACK);
        g.setFont(TOOL_TIP_FONT);
        final double textWidth = TextRenderer.getWidth(g, text);
        final double effectiveWidth = textWidth < WIDTH ? textWidth : WIDTH;

        final double guessHeight = getHeightWithLinebreaks(g, text, effectiveWidth); // guess leading
        // too high
        final double heightOff = position.getY() - guessHeight;
        if (heightOff < 0) {
            position.setLocation(position.getX(), position.getY() - heightOff + 2 * PADDING); // mirror below
        }
        // too right
        final double widthOff = Game.window().getWidth() - (position.getX() + effectiveWidth);
        if (widthOff < 0) {
            position.setLocation(position.getX() + widthOff - 2 * PADDING, position.getY()); // mirror below
        }
        ShapeRenderer.render(g, new Rectangle2D.Double(position.getX() - PADDING,
                                                       position.getY() - PADDING - guessHeight,
                                                       effectiveWidth + 2d * PADDING, guessHeight + 2d * PADDING));
        g.setColor(Color.WHITE);
        renderWithLinebreaks(g, text, position.getX(), position.getY() - guessHeight, effectiveWidth, guessHeight,
                             color);
    }
}
