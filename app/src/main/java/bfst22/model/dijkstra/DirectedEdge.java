package bfst22.model.dijkstra;

import bfst22.model.OSMNode;
import bfst22.model.WayType;
import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class DirectedEdge implements Drawable{

    private final OSMNode start; // En vertex (start)
    private final OSMNode end; // En anden vertex (slut)
    private final double weight; // Vægten
    private final ArrayList<OSMNode> nodesInEdge = new ArrayList<>();

    public DirectedEdge(OSMNode start, OSMNode end, double weight, boolean canGoByCar, boolean canGoByBike, boolean canGoByFoot) {
        if (start == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (end == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");

        this.start = start;
        this.end = end;
        this.weight = weight;
        this.canGoByCar = canGoByCar;
        this.canGoByBike = canGoByBike;
        this.canGoByFoot = canGoByFoot;
    }

    // Returnerer den vertex der er "from"
    public OSMNode getStart() {
        return start;
    }
    // Returnerer den vertex der er "to"
    public OSMNode getEnd() {
        return end;
    }
    // Returnerer vægten af edge
    public double weight() {
        return weight;
    }
    @Override
    public float getMiddleX() {
        return (start.getLon() + end.getLon()) / 2;
    }
    @Override
    public float getMiddleY() {
        return (start.getLat() + end.getLat()) / 2;
    }
    @Override
    public float getCurrentMinX() {
        return start.getLon();
    }
    @Override
    public float getCurrentMaxX() {
        return end.getLon();
    }
    @Override
    public float getCurrentMinY() {
        return start.getLat();
    }
    @Override
    public float getCurrentMaxY() {
        return end.getLat();
    }

    @Override
    public WayType getType() {
        return null;
    } // Edges har ikke en type
    @Override
    public ArrayList<OSMNode> getNodes(){
        nodesInEdge.add(getStart());
        nodesInEdge.add(getEnd());
        return nodesInEdge;
    }

}

