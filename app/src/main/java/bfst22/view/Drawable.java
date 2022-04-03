package bfst22.view;

import javafx.scene.canvas.GraphicsContext;
//Canvas = lærred
//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html
//Klassen Drawable er et interface som bruger GraphicsContext til at tegne scenen/lærred.
public interface Drawable {
    //Default svarer til et "else" statement hvilket betyder at den udfører den handling uanset hvad.
    //Metoden draw
    default void draw(GraphicsContext gc) {
        gc.beginPath(); //Starter med at tegne
        trace(gc);
        gc.stroke(); //Udfører tegneoperationerne
    }
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }
    void trace(GraphicsContext gc);
}