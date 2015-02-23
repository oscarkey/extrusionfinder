package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            return x == ((Point) o).getX() && y == ((Point) o).getY();
        } else {
            return false;
        }
    }

}
