package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point2D> points = new ArrayList<Point2D>();

    public Polygon(List<Point2D> points) {
        this.points = points;
    }

    public Point2D first() {
        return points.get(0);
    }

    public Point2D last() {
        return points.get(points.size() - 1);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public boolean tryUnion(Polygon other) {
        return tryMerge(points, other.points) ||
                tryMerge(points, reverse(other.points)) ||
                tryMerge(reverse(points), other.points) ||
                tryMerge(reverse(points), reverse(other.points)); // other combinations are just the reverse of one of these
    }

    public boolean tryMerge(List<Point2D> listA, List<Point2D> listB) {
        if (listA.size() < listB.size()) {
            List<Point2D> temp = listA;
            listA = listB;
            listB = temp;
        }

        for (int i = 1; i <= listB.size(); i++) {
            boolean mergeable = true;
            for (int j = 0; j < i; j++) {
                if (!equals(listA.get(listA.size() - (i - j)), listB.get(j))) {
                   mergeable = false;
                }
            }

            if (mergeable) {
                for (int j = 0; j < i; j++) {
                    listA.remove(listA.get(listA.size() - 1));
                }
                listA.addAll(listB);
                points = listA;
                return true;
            }
        }

        return false;
    }

    private List<Point2D> reverse(List<Point2D> x) {
        List<Point2D> output = new ArrayList<>(x.size());
        for (int i = 1; i <= x.size(); i++) {
            output.add(x.get(x.size() - i));
        }

        return output;
    }

    private boolean equals(Point2D pointA, Point2D pointB) {
        return Math.abs(pointA.getX() - pointB.getX()) < 1 && Math.abs(pointA.getY() - pointB.getY()) < 1;
    }

    @Override
    public String toString() {
        String output = "";
        for (Point2D point : points) {
            output = output + point.getX() + " " + point.getY() + " | ";
        }

        return output;
    }

    public String svgPoints() {
        String output = "";
        for (Point2D point : points) {
            output = output + point.getX() + "," + point.getY() + " ";
        }

        return output; // + points.get(0).getX() + "," + points.get(0).getY();
    }
}
