package de.flojo.jam.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class Hexagon extends Polygon {

    public static final int SIDES = 6;
    private static final long serialVersionUID = 1L;
    private Point2D.Double center;
    private int radius;
    private int shiftX = 0;
    private int shiftY = 0;

    public Hexagon(Point2D.Double center, int radius) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;

        updatePoints();
    }

    public Hexagon(double x, double y, int radius) {
        this(new Point2D.Double(x, y), radius);
    }

    private static double findAngle(double fraction) {
        return fraction * Math.PI * 2;
    }

    private static Point2D.Double findPoint(double x, double y, double radius, double angle) {
        var factor = 1.4d;
        if (angle == 0.0d || angle == Math.PI)
            factor = 1.25;

        double targetX = x + Math.cos(angle) * radius * factor;
        double targetY = y + Math.sin(angle) * radius;

        return new Point2D.Double(targetX, targetY);
    }

    public static double getWidthOf(int radius) {
        Point2D.Double point0 = findPoint(0, 0, radius, findAngle(0d));
        Point2D.Double point1 = findPoint(0, 0, radius, findAngle(0.5));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getSegmentWidthOf(int radius) {
        Point2D.Double point0 = findPoint(0, 0, radius, findAngle(0d));
        Point2D.Double point1 = findPoint(0, 0, radius, findAngle(1 / 6d));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getHeightOf(int radius) {
        Point2D.Double point0 = findPoint(0, 0, radius, findAngle(1 / 6d));
        Point2D.Double point1 = findPoint(0, 0, radius, findAngle(5 / 6d));
        return Math.abs(point0.getY() - point1.getY());
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updatePoints();
    }

    public Point2D getCenter() {
        return center;
    }

    public void setCenter(Point2D.Double center) {
        this.center = center;
        updatePoints();
    }

    public int getShiftX() {
        return shiftX;
    }

    public int getShiftY() {
        return shiftY;
    }

    public void move(float rx, float ry) {
        this.shiftX += rx;
        this.shiftY += ry;
        updatePoints();
    }

    public void setCenter(double x, double y) {
        setCenter(new Point2D.Double(x, y));
    }

    public synchronized void updatePoints() {
        for (var p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point2D point = findPoint(center.getX() + shiftX, center.getY() + shiftY, radius, angle);
            xpoints[p] = (int) point.getX();
            ypoints[p] = (int) point.getY();
        }
        super.invalidate();
    }

    public void draw(Graphics2D g, int lineThickness, Color color, boolean filled) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);
    }

}