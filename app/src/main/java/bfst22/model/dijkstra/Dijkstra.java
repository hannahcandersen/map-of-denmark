package bfst22.model.dijkstra;


import bfst22.model.OSMNode;
import bfst22.model.algslib.IndexMinPQ;
import bfst22.model.algslib.Stack;

import java.io.Serializable;
import java.util.HashMap;


/*
    Finder shortest path fra et startPunkt til slutPunkt i grafen
 */

public class Dijkstra implements Serializable {
    public static final long serialVersionUID = 1324234;
    private double[] distTo;          // Holder korteste distance til alle vertices. distTo[v] = distance (vægt) for korteste vej fra Start-vertex til vertex v
    private DirectedEdge[] edgeTo;    // Holder den sidste edge på korteste vej til hver vertex. edgeTo[v] = sidste edge på korteste vej fra Start-vertex til vertex v
    private IndexMinPQ<Double> pq;    // Minimums-priority queue med vertices


    public Dijkstra(EdgeWeightedDigraph graph, int startVertex, int endVertex, OSMNode[] arrayOfOSMNodesInGraph, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {

        // Tjekker at en edge ikke har negativ vægt
        for (DirectedEdge edge : graph.allEdgesInGraph(mapOfOSMNodesToIndex, arrayOfOSMNodesInGraph)) {
            if (edge.getWeight() < 0)
                throw new IllegalArgumentException("edge " + edge + " has negative weight");
        }

        // sætter distTo til et nyt double-array med samme antal pladser, som der er vertices i grafen
        distTo = new double[graph.getNumberOfVertices()];
        // sætter edgeTo til et nyt DirectedEdge-array med samme antal pladser, som der er vertices i grafen
        edgeTo = new DirectedEdge[graph.getNumberOfVertices()];

        // Tjekker at startVertex og endVertex er gyldige
        validateVertex(startVertex);
        validateVertex(endVertex);

        // Sætter distTo[v] til uendelig for alle andre vertices end startVertex
        // Sætter distTo for startVertex til 0
        for (int vertexIndex = 0; vertexIndex < graph.getNumberOfVertices(); vertexIndex++) {
            distTo[vertexIndex] = Double.POSITIVE_INFINITY;
        }
        distTo[startVertex] = 0.0;

        // Relax'er vertices i rækkefølge af deres længde fra startPunkt - ved brug af minimum priority queue
        pq = new IndexMinPQ<Double>(graph.getNumberOfVertices());
        pq.insert(startVertex, distTo[startVertex]);
        while (!pq.isEmpty()) {
            //System.out.println(pq.size());
            int vertexLightestWeight = pq.delMin();
            if(vertexLightestWeight == endVertex) {
                break;
            }
            else {
                for (DirectedEdge edge : graph.getEdgesAdjacentToVertex(arrayOfOSMNodesInGraph[vertexLightestWeight], mapOfOSMNodesToIndex))
                relax(edge, mapOfOSMNodesToIndex);
            }
        }

        // Check om grafen overholder alle kravene til at være korteste vej
        assert check(graph, startVertex, arrayOfOSMNodesInGraph, mapOfOSMNodesToIndex);
    }


    // Metoden der relaxer en edge og opdaterer priority queuen hvis nødvendigt
    private void relax(DirectedEdge edge, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {
        int edgeStart;
        int edgeEnd;

        edgeStart = mapOfOSMNodesToIndex.get(edge.getStart());
        edgeEnd = mapOfOSMNodesToIndex.get(edge.getEnd());

        if (distTo[edgeEnd] > distTo[edgeStart] + edge.getWeight()) {
            distTo[edgeEnd] = distTo[edgeStart] + edge.getWeight();
            edgeTo[edgeEnd] = edge;
            if (pq.contains(edgeEnd)) pq.decreaseKey(edgeEnd, distTo[edgeEnd]);
            else                      pq.insert(edgeEnd, distTo[edgeEnd]);
        }
    }


    // Returnerer vægten på den korteste vej fra start vertex til en vertex v
    public double distTo(int endVertex) {
        validateVertex(endVertex);
        return distTo[endVertex];
    }

    // Returnerer true hvis der er en vej mellem startVertex til slutVertex
    public boolean hasPathTo(int endVertex) {
        validateVertex(endVertex);
        return distTo[endVertex] < Double.POSITIVE_INFINITY;
    }

    // Returnerer den korteste vej fra startVertex til en slutVertex
    public Iterable<DirectedEdge> pathTo(int endVertex, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {
        validateVertex(endVertex);
        if (!hasPathTo(endVertex)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge edge = edgeTo[endVertex]; edge != null; edge = edgeTo[mapOfOSMNodesToIndex.get(edge.getStart())]) {
            path.push(edge);
        }
        //System.out.println("Færdig med pathTo");
        return path;
    }




    ///////////// METODER TIL VALIDERING ///////////////////

    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedDigraph G, int s, OSMNode[] arrayOfOSMNodesInGraph, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {

        // check that edge weights are non-negative
        for (DirectedEdge e : G.allEdgesInGraph(mapOfOSMNodesToIndex, arrayOfOSMNodesInGraph)) {
            if (e.getWeight() < 0) {
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
            for (DirectedEdge e : G.getEdgesAdjacentToVertex(arrayOfOSMNodesInGraph[v], mapOfOSMNodesToIndex)) {
                int w = mapOfOSMNodesToIndex.get(e.getEnd());
                if (distTo[v] + e.getWeight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.getNumberOfVertices(); w++) {
            if (edgeTo[w] == null) continue;
            DirectedEdge e = edgeTo[w];
            int v = mapOfOSMNodesToIndex.get(e.getStart());
            if (w != mapOfOSMNodesToIndex.get(e.getEnd())) return false;
            if (distTo[v] + e.getWeight() != distTo[w]) {
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
