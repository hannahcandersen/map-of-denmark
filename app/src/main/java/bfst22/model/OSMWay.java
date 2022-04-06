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

    public OSMWay(ArrayList<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    public static ArrayList<OSMNode> getNodes() {
        return nodes;
    }
}