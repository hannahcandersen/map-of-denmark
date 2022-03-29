package bfst22.model;

import java.awt.geom.Point2D;


public class KdNode<T> {

    protected Point2D.Double coords;

    protected KdNode<T> left;

    protected KdNode<T> right;

    protected T object;

    public KdNode() {

    }

    public KdNode(T object, double x, double y) {
        this.coords = new Point2D.Double(x,y);
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Point2D.Double getCoords() {
        return coords;
    }

    public Double getX() {
        return coords.x;
    }

    public Double getY() {
        return coords.y;
    }


    @Override
    public String toString() {
        return "Node{" +
                "coords=" + coords +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
