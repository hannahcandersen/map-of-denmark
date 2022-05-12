package bfst22.model.dijkstra;

import bfst22.model.OSMNode;
import bfst22.model.WayType;
import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;


/*
    Repræsenterer en Directed Edge i en EdgeWeightedGraph.
    Hver edge indeholder to vertices (to og from), en vægt og booleans for om man må køre bil, cykle eller gå
 */

public class DirectedEdge implements Drawable, Serializable {
    public static final long serialVersionUID = 1395234;

    private final OSMNode start; // En vertex (start)
    private final OSMNode end; // En anden vertex (slut)
    private final double weight; // Vægten
    private final ArrayList<OSMNode> nodesInEdge = new ArrayList<>();

    public DirectedEdge(OSMNode start, OSMNode end, double weight) {
        if (start == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (end == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");

        this.start = start;
        this.end = end;
        this.weight = weight;
    }


    @Override
    // To-string metode
    public String toString() {
        return start + "->" + end + " " + String.format("%5.2f", weight);
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(start.getLat(), start.getLon());
        gc.lineTo(end.getLat(), end.getLon());
        gc.stroke();
    }




    ///////////////// GETTER METODER /////////////////

    // Returnerer den vertex der er "from"
    public OSMNode getStart() {
        return start;
    }
    // Returnerer den vertex der er "to"
    public OSMNode getEnd() {
        return end;
    }
    // Returnerer vægten af edge
    public double getWeight() {
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

