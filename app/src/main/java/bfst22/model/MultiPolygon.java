package bfst22.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;
import bfst22.model.OSMWay;

public class MultiPolygon implements Drawable, Serializable {
    /*
        SerialVersionUID is a unique identifier for each class, JVM uses it to compare the versions of the
        class ensuring that the same class was used during Serialization is loaded during Deserialization.
        Specifying one gives more control, though JVM does generate one if you don't specify.

        Det er ligemeget hvilke tal vi giver den
     */

    float totalX;
    float totalY;

    public static final long serialVersionUID = 1325234;
    List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes;

    //Gemmer en liste med OSM way
    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes())); //Her får vi x-,y-kooordinaterne fra PolyLine klassen
            totalX = 0;
            totalY = 0;

            totalX += way.getMiddleX();
            totalY += way.getMiddleY();

        }
    }


    @Override
    //Tegner
    public void trace(GraphicsContext gc) {
        for (var part : parts) part.trace(gc);
    }

    @Override
    public float getMiddleX(){

        //Måske bare size
        return (totalX/parts.size());
    }

    @Override
    public float getMiddleY(){

        //Måske bare size
        return (totalY/parts.size());
    }



}
