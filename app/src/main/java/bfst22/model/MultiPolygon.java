package bfst22.model;

import java.io.Serializable;
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
    private List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes = new ArrayList<>(); // Alle nodes der er i multipolygonet
    private static ArrayList<PolyLine> multiPolygonPolylines = new ArrayList<>();

    //Konstruktør. Gemmer alle drawables i "parts" og alle OSMNodes i "multiPolygenNodes" Og WayType
    public MultiPolygon(ArrayList<OSMWay> rel, WayType wayType) {
        this.wayType = wayType;

        for (var way : rel) {

            //Mangler at tilføj veje i den rigtige rækkefølge
            parts.add(new PolyLine(way.getNodes(), wayType));

            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();

            // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            multiPolygonNodes.addAll(way.getNodes());
        }

        //hvorfor gør vi det her??
        multiPolygonPolylines.add(new PolyLine(multiPolygonNodes));

        // Udregner min x, max x, min y og max y
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
            multiPolygonNodes.addAll(way.getNodes());
        }

        // Udregner min x, max x, min y og max y
        calculateMinAndMax();
    }

    @Override
    //Tegner
    public void trace(GraphicsContext gc) {
       // gc.beginPath();
        gc.setFillRule(FillRule.EVEN_ODD);
        for (var part : parts){
            part.trace(gc);
        }
       // gc.fill();
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

        currentMinX = multiPolygonNodes.get(0).getLon();
        currentMaxX = multiPolygonNodes.get(0).getLon();
        currentMinY = multiPolygonNodes.get(0).getLat();
        currentMaxY = multiPolygonNodes.get(0).getLat();

        for (OSMNode node : multiPolygonNodes) {
            if (node.getLat() > currentMaxY) {
                currentMaxY = node.getLat();
            } if (node.getLat() < currentMinY) {
                currentMinY = node.getLat();
            } if (node.getLon() > currentMaxX) {
                currentMaxX = node.getLon();
            } if (node.getLon() < currentMinX) {
                currentMinX = node.getLon();
            }
        }
    }

    public float getCurrentMinX() {
        return currentMinX;
    }

    public float getCurrentMaxX() {
        return currentMaxX;
    }

    public float getCurrentMinY() {
        return currentMinY;
    }

    public float getCurrentMaxY() {
        return currentMaxY;
    }

    @Override
    public WayType getType(){
        return this.wayType;
    }

    public List<Drawable> getParts() {
        return parts;
    }
}





