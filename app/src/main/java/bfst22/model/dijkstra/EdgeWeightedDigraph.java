package bfst22.model.dijkstra;

import bfst22.model.Model;
import bfst22.model.OSMNode;
import bfst22.model.algslib.Bag;

import java.util.Map;


// En edge weighted graf der består af et antal vertices og et antal directed edges (der har et from, to og en vægt).
// Bygges som en adjacency list, hvor hver index er en vertex, med en tilhørende bag af adjacent vertices

public class EdgeWeightedDigraph {
    //Line separator opdeler en string på to forskellige linjer. FX: "Hello," + System.lineSeparator() + "there"
    private static final String NEWLINE = System.lineSeparator(); //ny måde at skrive metoden.
    // private static final String NEWLINE = System.getProperty("line.separator"); // gammel måde at skrive metoden

    private final int numberOfVertices;  // Antal vertices
    private int numberOfEdges;           // Antal edges
    private Bag<DirectedEdge>[] adjacencyList;    // adj[v] = adjacency list for vertex v. Array hvor hvert index/vertex har en Bag, der indeholder de vertices, som den vertex er connected til.
    private int[] indegree;             // indegree[v] = indegree of vertex v. Hvor mange edges der kommer ind på vertex v


    // Laver en tom graf med V vertices og 0 edges. Tager antal vertices som parameter.
    public EdgeWeightedDigraph(int numberOfVerticesInGraph) {
        if (numberOfVerticesInGraph < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be non-negative");
        this.numberOfVertices = numberOfVerticesInGraph;
        this.numberOfEdges = 0;
        this.indegree = new int[numberOfVerticesInGraph]; // Laver tomt int-array, der skal indeholde indegree for hver vertex

        // Laver tomt array med bags
        adjacencyList = (Bag<DirectedEdge>[]) new Bag[numberOfVerticesInGraph];

        // Opretter en tom bag for hver index/vertex i adj. Den første vertex er index 0, den anden index 1, osv.
        for (int v = 0; v < numberOfVerticesInGraph; v++)
            adjacencyList[v] = new Bag<DirectedEdge>();
    }

    // Antal vertices i grafen
    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    //Antal edges i grafen
    public int getNumberOfEdges() {
        return numberOfEdges;
    }


    // Tilføjer en Directed Edge til grafen
    public void addEdge(DirectedEdge edge) {
        OSMNode start = edge.getStart();
        OSMNode end = edge.getEnd();

        adjacencyList[Model.getMapOfOSMNodesToIndex().get(start)].add(edge);
        indegree[Model.getMapOfOSMNodesToIndex().get(end)]++;
        numberOfEdges++;
    }

    // Returnerer de edges der er adjacent med vertex v
    public Iterable<DirectedEdge> getEdgesAdjacentToVertex(OSMNode vertex) {
        return adjacencyList[Model.getMapOfOSMNodesToIndex().get(vertex)]; //returnere bag for den valgte vertex/index
    }

    // Returnerer antallet af Directed Edges fra vertex v (der går ud af den)
    public int outdegreeFromVertex(int vertex) {
        return adjacencyList[Model.getMapOfOSMNodesToIndex().get(vertex)].size();
    }

    // Returnerer antallet af Directed Edges fra vertex v (der går ind på den)
    public int indegreeFromVertex(OSMNode vertex) {
        return indegree[Model.getMapOfOSMNodesToIndex().get(vertex)];
    }

    // Returnerer alle edges i grafen
    public Iterable<DirectedEdge> allEdgesInGraph() {
        Bag<DirectedEdge> listOfEdges = new Bag<DirectedEdge>();
        for (int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++) {
            for (DirectedEdge edge : getEdgesAdjacentToVertex(Model.getArrayOfOSMNodesInGraph()[vertexIndex])) {
                listOfEdges.add(edge);
            }
        }
        return listOfEdges;
    }

    // String representation. Printer antal vertices, antal edges og adjacency listen
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(numberOfVertices + " " + numberOfEdges + NEWLINE);
        for (int v = 0; v < numberOfVertices; v++) {
            s.append(v + ": ");
            for (DirectedEdge e : adjacencyList[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}