package bfst22.model;

import java.util.ArrayList;
import java.util.List;

public class OSMNodeCollection {
    private List<OSMNode> OSMNodes;

    public OSMNodeCollection(){
        OSMNodes = new ArrayList<>();
    }

    public void addOSMNode(long id, float lat, float lon){
        OSMNode OSMNode = new OSMNode(id, lat, lon);
        OSMNodes.add(OSMNode);
    }

    public List<OSMNode> getOSMNodes() {
        return OSMNodes;
    }

}
