package bfst22.model.dijkstra;

import bfst22.model.OSMNode;
import bfst22.model.algslib.Bag;

import java.io.Serializable;
import java.util.HashMap;

// En edge weighted graf der består af et antal vertices og et antal directed edges (der har et from, to og en vægt).
// Bygges som en adjacency list, hvor hver index er en vertex, med en tilhørende bag af adjacent vertices

public class EdgeWeightedDigraph implements Serializable {
    public static final long serialVersionUID = 1335234;

    //Line separator opdeler en string på to forskellige linjer. FX: "Hello," + System.lineSeparator() + "there"
    private static final String NEWLINE = System.lineSeparator();
    private final int numberOfVertices;  // Antal vertices
    private int numberOfEdges;           // Antal edges
    private Bag<DirectedEdge>[] adjacencyList;    // adj[v] = adjacency list for vertex v. Array hvor hvert index/vertex har en Bag, der indeholder de vertices, som den vertex er connected til.
    private int[] indegree;             // indegree[v] = indegree of vertex v. Hvor mange edges der kommer ind på vertex v


    // Laver en tom graf med et bestemt antal vertices og 0 edges. Tager antal vertices som parameter.
    public EdgeWeightedDigraph(int numberOfVerticesInGraph) {
        if (numberOfVerticesInGraph < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be non-negative");

        this.numberOfVertices = numberOfVerticesInGraph;
        this.numberOfEdges = 0;
        this.indegree = new int[numberOfVerticesInGraph]; // Laver tomt int-array, der skal indeholde indegree for hver vertex

        // Laver tomt array med bags
        adjacencyList = (Bag<DirectedEdge>[]) new Bag[numberOfVerticesInGraph];

        // Opretter en tom bag for hver index/vertex i adj. Den første vertex er index 0, den anden index 1, osv.
        for (int v = 0; v < numberOfVerticesInGraph; v++)
            adjacencyList[v] = new Bag<>();
    }

    // Tilføjer en Directed Edge til grafen
    public void addEdge(DirectedEdge edge, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {
        OSMNode start = edge.getStart();
        OSMNode end = edge.getEnd();

        adjacencyList[mapOfOSMNodesToIndex.get(start)].add(edge); // Tilføjer til bag for start vertex
        indegree[mapOfOSMNodesToIndex.get(end)]++; // Tæller indegree for end én op
        numberOfEdges++;
    }

    // Returnerer de edges der er adjacent med vertex v
    public Iterable<DirectedEdge> getEdgesAdjacentToVertex(OSMNode vertex, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {
        return adjacencyList[mapOfOSMNodesToIndex.get(vertex)]; //returnere bag for den valgte vertex/index
    }

    // Returnerer alle edges i grafen
    public Iterable<DirectedEdge> allEdgesInGraph(HashMap<OSMNode, Integer> mapOfOSMNodesToIndex, OSMNode[] arrayOfOSMNodesInGraph) {
        Bag<DirectedEdge> bagOfEdges = new Bag<>();

        // For hver vertex, løber den dens tilhørende edges igennem og tilføjer dem til bag med alle edges i grafen
        for (int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++) {
            for (DirectedEdge edge : getEdgesAdjacentToVertex(arrayOfOSMNodesInGraph[vertexIndex], mapOfOSMNodesToIndex)) {
                bagOfEdges.add(edge);
            }
        }
        return bagOfEdges;
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

    public int getNumberOfVertices() {return numberOfVertices;} // Antal vertices i grafen
    public int[] getIndegree() {return indegree;} // Bruges til test!

}