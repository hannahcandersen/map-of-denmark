package bfst22.view;

import bfst22.model.OSMNode;
import bfst22.model.WayType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;


import java.util.ArrayList;

//Klassen Drawable er et interface som bruger GraphicsContext til at tegne scenen/lærred.
//Default svarer til et "else" statement hvilket betyder at den udfører den handling uanset hvad.
public interface Drawable {

    default void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }
    default void drawWayType(GraphicsContext gc, WayType wt) {
        gc.beginPath();
        trace(gc);
        gc.setStroke(wt.getStrokeColor());
        gc.stroke();
    }
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }
    default void fillWayType(GraphicsContext gc, WayType wt) {
        gc.beginPath();
        gc.setFillRule(FillRule.EVEN_ODD);
        trace(gc);
        gc.setFill(wt.getFillColor());
        gc.fill();
    }

    void trace(GraphicsContext gc);
    //OSMWay midtpunkt
    float getMiddleX();
    float getMiddleY();

    public float getCurrentMinX();
    public float getCurrentMaxX();
    public float getCurrentMinY();
    public float getCurrentMaxY();

    public WayType getType();
    public ArrayList<OSMNode> getNodes();
}