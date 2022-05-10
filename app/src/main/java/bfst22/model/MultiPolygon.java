package bfst22.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

public class MultiPolygon implements Drawable, Serializable {
    /*
        SerialVersionUID is a unique identifier for each class, JVM uses it to compare the versions of the
        class ensuring that the same class was used during Serialization is loaded during Deserialization.
        Specifying one gives more control, though JVM does generate one if you don't specify.

        Det er ligemeget hvilke tal vi giver den
     */

    float totalX;
    float totalY;
    float currentMinX = 0;
    float currentMaxX = 0;
    float currentMinY = 0;
    float currentMaxY = 0;
    WayType wayType;


    public static final long serialVersionUID = 1325234;
    private  List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes = new ArrayList<>(); // Alle nodes der er i multipolygonet
    private static ArrayList<PolyLine> multiPolygonPolylines = new ArrayList<>();

    //Konstruktør. Gemmer alle drawables i "parts" og alle OSMNodes i "multiPolygenNodes" Og WayType
    public MultiPolygon(ArrayList<OSMWay> rel, WayType wayType) {
        this.wayType = wayType;
        for (OSMWay way : rel) {

            way.getNodes();
            parts.add(new PolyLine(way.getNodes(), wayType)); //Tilføjer OSMWay til listen parts
            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();

            // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            multiPolygonNodes.addAll(way.getNodes());}
            //multiPolygonPolylines.add(new PolyLine(multiPolygonNodes));

        calculateMinAndMax();

    }

    //Konstruktør. Gemmer alle drawables i "parts" og alle OSMNodes i "multiPolygenNodes"
    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes())); //Tilføjer OSMWay til listen parts
            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();
            // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            multiPolygonNodes.addAll(way.getNodes());}
    }

    @Override
    //Tegner
    public void trace(GraphicsContext gc) {
        gc.beginPath();
        gc.setFillRule(FillRule.EVEN_ODD);
        for (var part : parts){
            part.trace(gc);
        }
        gc.fill();
    }







    @Override
    public void fill(GraphicsContext gc) {
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();
        trace(gc);
        gc.fill();
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

    //Bruges til at finde min og max af x og y. Skal bruges til range search
    private void calculateMinAndMax() {
        for (OSMNode node : multiPolygonNodes) {

            currentMinX = multiPolygonNodes.get(0).getLon();
            currentMaxX = multiPolygonNodes.get(0).getLon();
            currentMinY = multiPolygonNodes.get(0).getLat();
            currentMaxY = multiPolygonNodes.get(0).getLat();

            if (node.getLat() > currentMaxY) {
                currentMaxY = node.getLat();
            } else if (node.getLat() < currentMinY) {
                currentMinY = node.getLat();
            } else if (node.getLon() > currentMaxX) {
                currentMaxX = node.getLon();
            } else if (node.getLon() < currentMinX) {
                currentMinX = node.getLon();
            }
        }
    }

    public float getCurrentMinX() {
        calculateMinAndMax();
        return currentMinX;
    }

    public float getCurrentMaxX() {
        calculateMinAndMax();
        return currentMaxX;
    }

    public float getCurrentMinY() {
        calculateMinAndMax();
        return currentMinY;
    }

    public float getCurrentMaxY() {
        calculateMinAndMax();
        return currentMaxY;
    }

    @Override
    public WayType getType(){
        return this.wayType;
    }

    public List<Drawable> getParts() {
        return parts;
    }

    public ArrayList<OSMNode> getNodes(){
        return multiPolygonNodes;
    }
}





