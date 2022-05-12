package bfst22.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Indeholder linjesegmenter
/*
    Fungerer som edge i en graf
 */

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    private ArrayList<OSMNode> nodes;
    private float currentMinX, currentMaxX, currentMinY, currentMaxY = 0;

    public OSMWay(ArrayList<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }



    ///////////////// GETTER METODER /////////////////

    //Metoder der bruges i vores KdTree
    public float getMiddleY () {return (currentMinY + currentMaxY) / 2;}
    public float getMiddleX () {return (currentMinX + currentMaxX) / 2;}
    public ArrayList<OSMNode> getNodes() {return nodes;}
}




