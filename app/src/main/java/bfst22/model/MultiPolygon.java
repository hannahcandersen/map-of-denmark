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
    float currentMinX = 0;
    float currentMaxX = 0;
    float currentMinY = 0;
    float currentMaxY = 0;

    public static final long serialVersionUID = 1325234;
    List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes;

    //Gemmer en liste med OSM way
    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes())); //Her får vi x-,y-kooordinaterne fra PolyLine klassen

            for (OSMNode node : OSMWay.getNodes()){
                multiPolygonNodes.add(node);
                //Opdatere currentMaxY EVT skriver: <
                if (node.getLat() > currentMaxY){
                    currentMaxY = node.getLat();
                    System.out.println("MultiPolygonCurrentMaxY: " + currentMaxY);
                }
                else if(node.getLat() < currentMinY){
                    currentMinY = node.getLat();
                    System.out.println("MultiPolygonCurrentMinY: " + currentMinY);
                }
                else if(node.getLon() > currentMaxX){
                    currentMaxX = node.getLon();
                    System.out.println("MultiPolygonCurrentMaxX: " + currentMaxX);
                }
                else if(node.getLon() < currentMinX){
                    currentMinX = node.getLon();
                    System.out.println("MultiPolygonCurrentMinX: " + currentMinX);
                }
            }
        }
    }

    @Override
    //Tegner
    public void trace(GraphicsContext gc) {
        for (var part : parts) part.trace(gc);
    }

    @Override
    public float getMiddleX(){
        return (currentMaxX + currentMinX) / 2 ;
    }

    @Override
    public float getMiddleY(){
        return (currentMaxY + currentMinY) / 2;
    }



}
