package bfst22.model.dijkstra;


import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;


// Finder shortest path fra en vertex til alle andre vertices i grafen
// Skal laves om til at tage et start og et slut punkt, så den stopper med at lede efter vejen, når den finder slutpunkt

public class Dijkstra {
    private double[] distTo;          // Holder korteste distance til alle vertices. distTo[v] = distance (vægt) for korteste vej fra Start-vertex til vertex v
    private DirectedEdge[] edgeTo;    // Holder den sidste edge på korteste vej til hver vertex. edgeTo[v] = sidste edge på korteste vej fra Start-vertex til vertex v
    private IndexMinPQ<Double> pq;    // Minimums-priority queue med vertices


    public Dijkstra(EdgeWeightedDigraph graph, int startVertex) {

        // Tjekker at en edge ikke har negativ vægt
        for (DirectedEdge e : graph.allEdgesInGraph()) {
            if (e.weight() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        // sætter distTo til et nyt double-array med samme antal pladser, som der er vertices i grafen
        distTo = new double[graph.getNumberOfVertices()];
        // sætter edgeTo til et nyt DirectedEdge-array med samme antal pladser, som der er vertices i grafen
        edgeTo = new DirectedEdge[graph.getNumberOfVertices()];

        // Tjekker at startVertex er gyldig
        validateVertex(startVertex);

        for (int v = 0; v < graph.getNumberOfVertices(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[startVertex] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(graph.getNumberOfVertices());
        pq.insert(startVertex, distTo[startVertex]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (DirectedEdge e : graph.getEdgesAdjacentToVertex(v))
                relax(e);
        }

        // check optimality conditions
        assert check(graph, startVertex);
    }

    // relax edge e and update pq if changed
    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedEdge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedDigraph G, int s) {

        // check that edge weights are non-negative
        for (DirectedEdge e : G.allEdgesInGraph()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.getNumberOfVertices(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.getNumberOfVertices(); v++) {
            for (DirectedEdge e : G.getEdgesAdjacentToVertex(v)) {
                int w = e.to();
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.getNumberOfVertices(); w++) {
            if (edgeTo[w] == null) continue;
            DirectedEdge e = edgeTo[w];
            int v = e.from();
            if (w != e.to()) return false;
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }


}
