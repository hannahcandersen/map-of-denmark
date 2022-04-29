package bfst22.model.dijkstra;

// Repræsenterer en Directed Edge i en EdgeWeightedGraph. Hver edge indeholder to vertices (to og from) og en vægt

import bfst22.model.OSMNode;
import bfst22.view.Drawable;

public class DirectedEdge {

    private final OSMNode start; // En vertex (start)
    private final OSMNode end; // En anden vertex (slut)
    private final double weight; // Vægten

    // skal evt. tilføjes som parameter for en edge:z
    /*
    private boolean canGoByCar
    private boolean canGoByBike
    private boolean canGoByFoot
     */

    public DirectedEdge(OSMNode start, OSMNode end, double weight) {
        if (start == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (end == null) throw new IllegalArgumentException("Vertex must be non-negative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.start = start;
        this.end = end;
        this.weight = weight;
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

    // to-string metode
    public String toString() {
        return start + "->" + end + " " + String.format("%5.2f", weight);
    }
}
