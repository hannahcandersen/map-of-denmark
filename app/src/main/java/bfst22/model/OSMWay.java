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
    private static ArrayList<OSMNode> nodes;
    float currentMinX = 0;
    float currentMaxX = 0;
    float currentMinY = 0;
    float currentMaxY = 0;

    public OSMWay(ArrayList<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    //Bruges til at finde vejens midtpunkt
    private void calculateMinAndMax() {

        for (OSMNode node : nodes) {
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

    public float getMiddleY () {
        return (currentMinY + currentMaxY) / 2;
    }

    public float getMiddleX () {
        return (currentMinX + currentMaxX) / 2;
    }

    public static ArrayList<OSMNode> getNodes() {
        return nodes;
    }
}




