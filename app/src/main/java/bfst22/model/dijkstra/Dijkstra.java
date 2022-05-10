package bfst22.model.dijkstra;


import bfst22.model.Model;
import bfst22.model.OSMNode;
import bfst22.model.PolyLine;
import bfst22.model.algslib.IndexMinPQ;
import bfst22.model.algslib.Stack;

import java.util.ArrayList;
import java.util.Map;


// Finder shortest path fra en vertex til alle andre vertices i grafen
// !!!!! Skal laves om til at tage et start og et slut punkt, så den stopper med at lede efter vejen, når den finder slutpunkt !!!!!

public class Dijkstra {
    private double[] distTo;          // Holder korteste distance til alle vertices. distTo[v] = distance (vægt) for korteste vej fra Start-vertex til vertex v
    private DirectedEdge[] edgeTo;    // Holder den sidste edge på korteste vej til hver vertex. edgeTo[v] = sidste edge på korteste vej fra Start-vertex til vertex v
    private IndexMinPQ<Double> pq;    // Minimums-priority queue med vertices

    private Stack<PolyLine> polylinesInPath = new Stack<>();


    public Dijkstra(EdgeWeightedDigraph graph, int startVertex, int endVertex) {

        // Tjekker at en edge ikke har negativ vægt
        for (DirectedEdge edge : graph.allEdgesInGraph()) {
            if (edge.weight() < 0)
                throw new IllegalArgumentException("edge " + edge + " has negative weight");
        }

        // sætter distTo til et nyt double-array med samme antal pladser, som der er vertices i grafen
        distTo = new double[graph.getNumberOfVertices()];
        // sætter edgeTo til et nyt DirectedEdge-array med samme antal pladser, som der er vertices i grafen
        edgeTo = new DirectedEdge[graph.getNumberOfVertices()];

        // Tjekker at startVertex er gyldig
        validateVertex(startVertex);
        validateVertex(endVertex);

        // Sætter distTo[v] til uendelig for alle andre vertices end startVertex. distTo for startVertex er 0
        for (int vertexIndex = 0; vertexIndex < graph.getNumberOfVertices(); vertexIndex++) {
            distTo[vertexIndex] = Double.POSITIVE_INFINITY;
        }
        distTo[startVertex] = 0.0;

        // relax vertices in order of distance from startpunkt - ved brug af minimum priority queue
        pq = new IndexMinPQ<Double>(graph.getNumberOfVertices());
        pq.insert(startVertex, distTo[startVertex]);
        while (!pq.isEmpty()) {
            //System.out.println(pq.size());
            int vertexLightestWeight = pq.delMin();
            if(vertexLightestWeight == endVertex) {
                break;
            }
            else {
                for (DirectedEdge edge : graph.getEdgesAdjacentToVertex(Model.getKeyfromValueInHashMap(vertexLightestWeight)))
                relax(edge);
            }
        }

        // Check optimality conditions
        assert check(graph, startVertex);
    }


    // Metoden der relaxer en edge og opdaterer priority queuen hvis nødvendigt
    private void relax(DirectedEdge edge) {
        int edgeStart = Model.getMapOfOSMNodesToIndex().get(edge.getStart());
        int edgeEnd = Model.getMapOfOSMNodesToIndex().get(edge.getEnd());
        if (distTo[edgeEnd] > distTo[edgeStart] + edge.weight()) {
            distTo[edgeEnd] = distTo[edgeStart] + edge.weight();
            edgeTo[edgeEnd] = edge;
            if (pq.contains(edgeEnd)) pq.decreaseKey(edgeEnd, distTo[edgeEnd]);
            else                pq.insert(edgeEnd, distTo[edgeEnd]);
        }
    }


    // Returnerer vægten på den korteste vej fra start vertex til en vertex v
    public double distTo(int vertex) {
        validateVertex(vertex);
        return distTo[vertex];
    }

    // Returnerer true hvis der er en vej mellem start vertex til vertex v
    public boolean hasPathTo(int vertex) {
        validateVertex(vertex);
        return distTo[vertex] < Double.POSITIVE_INFINITY;
    }

    // Returnerer den korteste vej fra start vertex til en vertex v
    public Iterable<DirectedEdge> pathTo(int vertex) { // som vi bruger det, er vertex vores slut-vertex
        validateVertex(vertex);
        if (!hasPathTo(vertex)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge edge = edgeTo[vertex]; edge != null; edge = edgeTo[Model.getMapOfOSMNodesToIndex().get(edge.getStart())]) {
            path.push(edge);
        }
        return path;
    }

    /*
    public void caluclatePolylinesInPath(int vertex){
        validateVertex(vertex);
        ArrayList<OSMNode> nodesInPolyLine = new ArrayList<>();

        for (DirectedEdge edge = edgeTo[vertex]; edge != null; edge = edgeTo[Model.getMapOfOSMNodesToIndex().get(edge.getStart())]) {
            nodesInPolyLine.add(edge.getStart());
            nodesInPolyLine.add(edge.getEnd());
            PolyLine polyLine = new PolyLine(nodesInPolyLine);
            polylinesInPath.push(polyLine);
        }
    }

    public Iterable<PolyLine> getPolylinesInPath(int vertex){
        if (!hasPathTo(vertex)) return null;
        caluclatePolylinesInPath(vertex);
        return polylinesInPath;
    }
     */





    ///////////// METODER TIL VALIDERING ///////////////////

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
            for (DirectedEdge e : G.getEdgesAdjacentToVertex(Model.getKeyfromValueInHashMap(v))) {
                int w = Model.getMapOfOSMNodesToIndex().get(e.getEnd());
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
            int v = Model.getMapOfOSMNodesToIndex().get(e.getStart());
            if (w != Model.getMapOfOSMNodesToIndex().get(e.getEnd())) return false;
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
