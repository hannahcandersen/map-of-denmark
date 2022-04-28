/*package bfst22.model.dijkstra;

import edu.princeton.cs.algs4.Bag;


// En edge weighted graf der består af et antal vertices og et antal directed edges (der har et from, to og en vægt).
// Bygges som en adjacency list, hvor hver index er en vertex, med en tilhørende bag af adjacent vertices

public class EdgeWeightedDigraph {
    private static final String NEWLINE = System.getProperty("line.separator"); //?

    private final int numberOfVertices;  // Antal vertices
    private int numberOfEdges;           // Antal edges
    private Bag<DirectedEdge>[] adjacencyList;    // adj[v] = adjacency list for vertex v. Array hvor hvert index/vertex har en Bag, der indeholder de vertices, som den vertex er connected til.
    private int[] indegree;             // indegree[v] = indegree of vertex v. Ved jeg ikke om vi skal bruge!!


    // Laver en tom graf med V vertices og 0 edges. Tager antal vertices som parameter.
    public EdgeWeightedDigraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be non-negative");
        this.numberOfVertices = V;
        this.numberOfEdges = 0;
        this.indegree = new int[V]; // Ved jeg ikke om vi skal bruge!!

        // Laver tomt array med bags
        adjacencyList = (Bag<DirectedEdge>[]) new Bag[V];

        // Opretter en tom bag for hver index/vertex i adj. Den første vertex er index 0, den anden index 1, osv.
        for (int v = 0; v < V; v++)
            adjacencyList[v] = new Bag<DirectedEdge>();
    }

    // Antal vertices i grafen
    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    //Antal edges i grafen
    public int getNumberofEdges() {
        return numberOfEdges;
    }

    // Kaster exception hvis numberOfVertices er mindre end 0, eller over V (max antal vertices)
    private void validateVertex(int v) {
        if (v < 0 || v >= numberOfVertices)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (numberOfVertices -1));
    }

    // Tilføjer en Directed Edge til grafen
    public void addEdge(DirectedEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v); // Tjekker om vertex er gyldig
        validateVertex(w);
        adjacencyList[v].add(e);
        indegree[w]++;
        numberOfEdges++;
    }

    // Returnerer de edges der er adjacent med vertex v
    public Iterable<DirectedEdge> getEdgesAdjacentToVertex(int v) {
        validateVertex(v);
        return adjacencyList[v];
    }

    // Returnerer antallet af Directed Edges fra vertex v (der går ud af den)
    public int outdegreeFromVertex(int v) {
        validateVertex(v);
        return adjacencyList[v].size();
    }

    // Returnerer antallet af Directed Edges fra vertex v (der går ind på den)
    public int indegreeFromVertex(int v) {
        validateVertex(v);
        return indegree[v];
    }

    // Returnerer alle edges i grafen
    public Iterable<DirectedEdge> allEdgesInGraph() {
        Bag<DirectedEdge> listOfEdges = new Bag<DirectedEdge>();
        for (int v = 0; v < numberOfVertices; v++) {
            for (DirectedEdge e : getEdgesAdjacentToVertex(v)) {
                listOfEdges.add(e);
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

}*/