package bfst22.model;

import bfst22.view.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Line implements Drawable {
    private Point2D from, to;

    float x1;
    float y1;
    float x2;
    float y2;

//Line tager de fire koordinater fra txt line filerne og splitter dem til at kunne blive læst og derefter tegnet.
    public Line(String line) {
        var parts = line.split(" ");
        this.x1 = Float.parseFloat(parts[1]);
        this.y1 = Float.parseFloat(parts[2]);
        this.x2 = Float.parseFloat(parts[3]);
        this.y2 = Float.parseFloat(parts[4]);
        from = new Point2D(x1, y1);
        to = new Point2D(x2, y2);
    }

    public Line(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(from.getX(), from.getY());
        gc.lineTo(to.getX(), to.getY());
        gc.stroke();
    }

    @Override
    public float getMiddleX(){
        return (x1 + x2) / 2;
    }

    @Override
    public float getMiddleY(){
        return (y1 + y2) / 2;
    }

    public float getCurrentMinX() {
        return x1;
    }

    public float getCurrentMinY() {
        return y1;
    }

    public float getCurrentMaxX() {
        return x2;
    }

    public float getCurrentMaxY() {
        return y2;
    }

    @Override
    public WayType getType() {
        return null;
    }
}


