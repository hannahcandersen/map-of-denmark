package bfst22.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

/*
    Multipolygon ligner polyline, forskellen er at den består af relations, som har members
        - dvs. det er en figur der kan have fx. huller i sig
 */

public class MultiPolygon implements Drawable, Serializable {
    public static final long serialVersionUID = 1325234;
    private  List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes = new ArrayList<>(); // Alle nodes der er i multipolygonet

    private float totalX;
    private float totalY;
    private float currentMinX = 0;
    private float currentMaxX = 0;
    private float currentMinY = 0;
    private float currentMaxY = 0;
    private WayType wayType;


    //Konstruktør. Gemmer alle drawables i "parts" og alle OSMNodes i "multiPolygenNodes" og WayType
    public MultiPolygon(ArrayList<OSMWay> rel, WayType wayType) {
        this.wayType = wayType;

        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes())); //Tilføjer OSMWay som en Polyline til listen "parts"
            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();
            multiPolygonNodes.addAll(way.getNodes()); } // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            calculateMinAndMax();
    }

    //Bruges til at finde min og max af x og y. Skal bruges til range search
    private void calculateMinAndMax() {
        for (OSMNode node : multiPolygonNodes) {

            currentMinX = multiPolygonNodes.get(0).getLon();
            currentMaxX = multiPolygonNodes.get(0).getLon();
            currentMinY = multiPolygonNodes.get(0).getLat();
            currentMaxY = multiPolygonNodes.get(0).getLat();

            if (node.getLat() > currentMaxY) {currentMaxY = node.getLat();}
            else if (node.getLat() < currentMinY) {currentMinY = node.getLat();}
            else if (node.getLon() > currentMaxX) {currentMaxX = node.getLon();}
            else if (node.getLon() < currentMinX) {currentMinX = node.getLon();}
        }
    }

    @Override
    public void trace(GraphicsContext gc) {
        for (var part : parts){
            gc.beginPath();
            part.trace(gc);
            gc.setFillRule(FillRule.EVEN_ODD);
            gc.fill();}
    }



    ///////////////// GETTER METODER /////////////////

    //Hvis det fucker skal metoden calculateMinAndMax(); tilbage i
    @Override
    public float getCurrentMinX() {return currentMinX;}
    @Override
    public float getCurrentMaxX() {return currentMaxX;}
    @Override
    public float getCurrentMinY() {return currentMinY;}
    @Override
    public float getCurrentMaxY() {return currentMaxY;}
    @Override
    public ArrayList<OSMNode> getNodes(){return multiPolygonNodes;}
    @Override
    public float getMiddleX(){return (totalX/parts.size());}
    @Override
    public float getMiddleY(){return (totalY/parts.size());}
    @Override
    public WayType getType(){return this.wayType;}

}





