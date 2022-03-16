package bfst22.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Indeholder linjesegmenter

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;

    //List af OSMNodes.
    List<OSMNode> nodes;
    public OSMWay(List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }
}