package bfst22.model.dijkstra;

// Repræsenterer en Directed Edge i en EdgeWeightedGraph. Hver edge indeholder to vertices (to og from) og en vægt

public class DirectedEdge {

    private final int v; // En vertex (from)
    private final int w; // En anden vertex (to)
    private final double weight; // Vægten


    public DirectedEdge(int v, int w, double weight) {
        if (v < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        if (w < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    // Returnerer den vertex der er "from"
    public int from() {
        return v;
    }

    // Returnerer den vertex der er "to"
    public int to() {
        return w;
    }

    // Returnerer vægten af edge
    public double weight() {
        return weight;
    }

    // to-string metode
    public String toString() {
        return v + "->" + w + " " + String.format("%5.2f", weight);
    }
}
