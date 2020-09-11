package de.flojo.jam.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.concurrent.atomic.AtomicBoolean;

public class Hexagon extends Polygon {

    private static final long serialVersionUID = 1L;

    public static final int SIDES = 6;

    private Point[] points = new Point[SIDES];
    private Point center = new Point(0, 0);
    private int radius;
    private float scale = 1f;
    private float scaleTarget = 1f;
    private static Point zoomPoint = new Point();
    public AtomicBoolean hover = new AtomicBoolean();

    public Hexagon(Point center, int radius) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;

        updatePoints();
    }

    public void scale(float newScale) {
        this.scaleTarget = newScale;
        updatePoints();
    }

    public Hexagon(int x, int y, int radius) {
        this(new Point(x, y), radius);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;

        updatePoints();
    }

    public static void updateZoomPoint(int x, int y) {
        zoomPoint = new Point(x, y);
    }

    public void setCenter(Point center) {
        this.center = center;

        updatePoints();
    }

    public void move(int rx, int ry) {
        this.center = new Point(this.center.x + rx, this.center.y + ry);
        updatePoints();
    }

    public void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    private static double findAngle(double fraction) {
        return fraction * Math.PI * 2;
    }

    private static Point findPoint(int x, int y, double radius, double angle) {
        double factor = 1.4;
        if (angle == 0.0d || angle == Math.PI)
            factor = 1.25;

        double targetX = x + Math.cos(angle) * radius * factor;
        double targetY = y + Math.sin(angle) * radius;

        return new Point((int) targetX, (int) targetY);
    }

    public synchronized void updatePoints() {
        float relativeScale = Math.signum(scaleTarget - scale) * (scale != scaleTarget ? 0.1f : 0); 
        int scaledCenterX = (int)((center.x - zoomPoint.getX()) * relativeScale + center.x);
        int scaledCenterY = (int)((center.y - zoomPoint.getY()) * relativeScale + center.y);
        if(Math.abs(scale - scaleTarget) < 0.01) {
            scale = scaleTarget;
        } else {
            scale += (scaleTarget - scale)/20;
        }
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(scaledCenterX, scaledCenterY, radius * scale, angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
        }
        super.invalidate();
    }

    public void draw(Graphics2D g, int lineThickness, int colorValue, boolean filled) {
        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);
    }

    public static double getWidthOf(int radius, double scale) {
        Point point0 = findPoint(0, 0, radius * scale, findAngle(0d));
        Point point1 = findPoint(0, 0, radius * scale, findAngle(0.5));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getSegmentWidthOf(int radius, double scale) {
        Point point0 = findPoint(0, 0, radius * scale, findAngle(0d));
        Point point1 = findPoint(0, 0, radius * scale, findAngle(1 / 6d));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getHeightOf(int radius, double scale) {
        Point point0 = findPoint(0, 0, radius * scale, findAngle(1 / 6d));
        Point point1 = findPoint(0, 0, radius * scale, findAngle(5 / 6d));
        return Math.abs(point0.getY() - point1.getY());
    }

}