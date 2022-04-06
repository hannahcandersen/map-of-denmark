package bfst22.view;
import bfst22.model.OSMNode;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

//Canvas = lærred
//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html
//Klassen Drawable er et interface som bruger GraphicsContext til at tegne scenen/lærred.
public interface Drawable {
    //Default svarer til et "else" statement hvilket betyder at den udfører den handling uanset hvad.
    //Metoden draw
    default void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }

    void trace(GraphicsContext gc);

    //Bruges til at finde vejens midtpunkt
    public float getMiddleX();
    public float getMiddleY();

}