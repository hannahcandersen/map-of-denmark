package bfst22.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
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

    private float totalX;
    private float totalY;
    private float currentMinX = 0;
    private float currentMaxX = 0;
    private float currentMinY = 0;
    private float currentMaxY = 0;
    private WayType wayType;


    public static final long serialVersionUID = 1325234;
    private  List<Drawable> parts = new ArrayList<>();
    private ArrayList<OSMNode> multiPolygonNodes = new ArrayList<>(); // Alle nodes der er i multipolygonet
    private static ArrayList<PolyLine> multiPolygonPolylines = new ArrayList<>();
    private static List<PolyLine> listOfMultipolygonLines = new ArrayList<>();

    //Konstruktør. Gemmer alle drawables i "parts" og alle OSMNodes i "multiPolygenNodes" Og WayType
    public MultiPolygon(ArrayList<OSMWay> rel, WayType wayType) {
        this.wayType = wayType;
        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes())); //Tilføjer OSMWay til listen parts
            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();
            // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            multiPolygonNodes.addAll(way.getNodes());}

        calculateMinAndMax();




        /*OSMNode startOSMNode = null;
        OSMNode endOSMNode = null;
        OSMNode currentStartOSMNode;
        OSMNode currentEndOSMNode;
        PolyLine currentWay = null;

        ArrayList<OSMNode> listOfNodesInMultipolygon = new ArrayList<>();


        while(!rel.isEmpty()){
            ArrayList<OSMWay> buildUpList = new ArrayList<>();
        for (OSMWay way : rel) {

            if(listOfNodesInMultipolygon.size() == 0){
                listOfNodesInMultipolygon.addAll(way.getNodes());
                startOSMNode = way.getNodes().get(0);
                endOSMNode = way.getNodes().get(way.getNodes().size()-1);
                currentWay = new PolyLine(listOfNodesInMultipolygon);
                continue;
            }

            currentStartOSMNode = way.getNodes().get(0);
            currentEndOSMNode = way.getNodes().get(way.getNodes().size()-1);

            //Situation 1
            if(currentStartOSMNode.getLat() == endOSMNode.getLat() && currentStartOSMNode.getLon() == endOSMNode.getLon()){
                listOfNodesInMultipolygon.addAll(way.getNodes());
                endOSMNode = currentEndOSMNode;
                currentWay = new PolyLine(listOfNodesInMultipolygon);
                //System.out.println("Vi er ved situation 1");
            }

            //Situation 2
            else if (endOSMNode.getLat() == currentEndOSMNode.getLat() && endOSMNode.getLon() == currentEndOSMNode.getLon()){
                Collections.reverse(way.getNodes());
                listOfNodesInMultipolygon.addAll(way.getNodes());
                endOSMNode = currentEndOSMNode;
                currentWay = new PolyLine(listOfNodesInMultipolygon);
                System.out.println("Vi er ved situation 2");
            }

            // SITUATION 3: Begge pile væk fra midten // Start og start mødes //Første pil drejet rundt
            else if (currentStartOSMNode.getLat() == startOSMNode.getLat() && currentStartOSMNode.getLon() == startOSMNode.getLon()) {
                Collections.reverse(way.getNodes());
                listOfNodesInMultipolygon.addAll(way.getNodes());
                startOSMNode = listOfNodesInMultipolygon.get(0);
                currentWay = new PolyLine(listOfNodesInMultipolygon);
                System.out.println("Vi er ved situation 3");
            }
            // SITUATION 4: Begge pile mod venstre // Vores start == nye slut
            else if (currentEndOSMNode.getLat() == startOSMNode.getLat() && currentEndOSMNode.getLon() == startOSMNode.getLon()) {
                listOfNodesInMultipolygon.addAll(0, way.getNodes());
                startOSMNode = listOfNodesInMultipolygon.get(0);
                currentWay = new PolyLine(listOfNodesInMultipolygon);
                System.out.println("Vi er ved situation 4");
            } else {
                buildUpList.add(way);
                System.out.println("Ingen veje passede");

            }

            //Vi er færdig
            if (endOSMNode.getLat() == startOSMNode.getLat() && endOSMNode.getLon() == startOSMNode.getLon()) {
                listOfNodesInMultipolygon.addAll(way.getNodes());
                listOfMultipolygonLines.add(currentWay);
                parts.add(currentWay);
                listOfNodesInMultipolygon.clear();
                System.out.println("Vi er færdige med et multipolygon og har tilføjet den");

            }

            //parts.add(new PolyLine(way.getNodes(), wayType)); //Tilføjer OSMWay til listen parts
            totalX = 0;
            totalY = 0;
            totalX += way.getMiddleX();
            totalY += way.getMiddleY();
            // Tilføjer alle OSMNodes fra hver OSMWay til listen multiPolygonNodes
            multiPolygonNodes.addAll(way.getNodes());
        } //Forloop slutter





            rel = buildUpList;

        }*/ //While loop slutter
            //multiPolygonPolylines.add(new PolyLine(multiPolygonNodes));

        //calculateMinAndMax();

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

    /*@Override
    //Tegner
    public void fill(GraphicsContext gc) {
        for (var part : parts){
            gc.beginPath();
            gc.setFillRule(FillRule.EVEN_ODD);
            part.fill(gc);
            //gc.beginPath();
            //part.trace(gc);
            //gc.fill();
        }
    }*/

    @Override
    public void trace(GraphicsContext gc) {
        //gc.setFillRule(FillRule.EVEN_ODD);
        for (var part : parts){
            gc.beginPath();
            part.trace(gc);
            gc.setFillRule(FillRule.EVEN_ODD);
            gc.fill();
        }
    }

    @Override
    public void fillWayType(GraphicsContext gc, WayType wt) {
        //gc.beginPath();
        trace(gc);
        gc.setFill(wt.getFillColor());
        gc.setFillRule(FillRule.EVEN_ODD);
       // gc.fill();
    }


    /*@Override
    public void fill(GraphicsContext gc) {
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();
        trace(gc);
        gc.fill();
    }*/

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

    public static List<PolyLine> getListOfMultipolygonLines() {
        return listOfMultipolygonLines;
    }
}





