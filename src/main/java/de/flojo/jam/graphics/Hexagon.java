package de.flojo.jam.graphics;

import java.awt.*;

public class Hexagon extends Polygon {

    public static final int SIDES = 6;
    private static final long serialVersionUID = 1L;
    private final Point[] points = new Point[SIDES];
    private Point center;
    private int radius;
    private int shiftX = 0;
    private int shiftY = 0;

    public Hexagon(Point center, int radius) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;

        updatePoints();
    }


    public Hexagon(int x, int y, int radius) {
        this(new Point(x, y), radius);
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

    public static double getWidthOf(int radius) {
        Point point0 = findPoint(0, 0, radius, findAngle(0d));
        Point point1 = findPoint(0, 0, radius, findAngle(0.5));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getSegmentWidthOf(int radius) {
        Point point0 = findPoint(0, 0, radius, findAngle(0d));
        Point point1 = findPoint(0, 0, radius, findAngle(1 / 6d));
        return Math.abs(point0.getX() - point1.getX());
    }

    public static double getHeightOf(int radius) {
        Point point0 = findPoint(0, 0, radius, findAngle(1 / 6d));
        Point point1 = findPoint(0, 0, radius, findAngle(5 / 6d));
        return Math.abs(point0.getY() - point1.getY());
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updatePoints();
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
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

    public void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    public synchronized void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint((int) center.getX() + shiftX, (int) center.getY() + shiftY, radius, angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
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