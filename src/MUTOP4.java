
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author Fazeel
 */
public class MUTOP4 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in); // switch the comments before submitting

        String line = "";
        line = sc.nextLine();
        String[] tokens = line.split(" ");
        Graph graph = new Graph(Integer.parseInt(tokens[0]));
        int startVertex = Integer.parseInt(tokens[1]);
        line = sc.nextLine();

        while (!line.equals("0 0 0")) {

            tokens = line.split(" ");
            graph.addEdge(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            line = sc.nextLine();
        }
        graph.dijkstra(startVertex);
        System.out.println();
        graph.kruskal();

    }

}

class Graph {

    int numOfVertices;
    int[][] adjacencyMatrix;
    ArrayList<Edge> edges;

    Graph(int n) {
        this.numOfVertices = n;
        adjacencyMatrix = new int[n + 1][n + 1];
        edges = new ArrayList<>();
    }

    public void addEdge(int v1, int v2, int weight) {
        adjacencyMatrix[v1][v2] = weight;
        adjacencyMatrix[v2][v1] = weight;
        edges.add(new Edge(v1, v2, weight));
    }

    public int size() {
        return numOfVertices;
    }

    public int[] dijkstra(int startVertex) {

        ArrayList<Integer> known = new ArrayList<>();
        int[] di = new int[numOfVertices + 1];
        int[] pi = new int[numOfVertices + 1];
        known.add(startVertex);
        Arrays.fill(di, Integer.MAX_VALUE);
        di[startVertex] = 0;
        int vertex = startVertex, prev;
        while (true) {
            //relax    
            for (int i = 1; i <= numOfVertices; i++) {

                if (adjacencyMatrix[vertex][i] > 0 && !known.contains(i)) {
                    if (di[i] > (adjacencyMatrix[vertex][i] + di[vertex])) {
                        pi[i] = vertex;
                        di[i] = adjacencyMatrix[vertex][i] + di[vertex];
                    }
                }
            }
            if (known.size() == numOfVertices) {
                break;
            }
            //Add to known Set
            vertex = 0;
            int distance = Integer.MAX_VALUE;
            prev = 0;
            for (Integer known1 : known) {
                for (int i = 1; i <= numOfVertices; i++) {
                    if (adjacencyMatrix[known1][i] > 0 && !known.contains(i)) {
                        if (distance > (adjacencyMatrix[known1][i] + di[known1])) {
                            vertex = i;
                            distance = adjacencyMatrix[known1][i] + di[known1];
                            prev = known1;
                        }
                    }
                }
            }
            known.add(vertex);
            pi[vertex] = prev;
            di[vertex] = distance;

        }

        for (int i = 1; i <= numOfVertices; i++) {
            if (i == startVertex) {
                System.out.print(startVertex + " ");
            }
            printSPath(pi, di, i);
            System.out.print(di[i] + "\n");
        }
        return di;
    }

    public void printSPath(int[] previous, int[] distances, int vertex) {
        if (previous[vertex] == 0) {
            System.out.print(vertex + " ");
        } else {
            printSPath(previous, distances, previous[vertex]);
            System.out.print(vertex + " ");
        }
    }

    void kruskal() {
        UnionFind uf = new UnionFind(numOfVertices);
        int[] vertices = null;
        Collections.sort(edges, new Comparator<Edge>() { //Sort Edges based on Distance/Weight
            @Override
            public int compare(Edge e1, Edge e2) {
                return e1.getWeight() - e2.getWeight(); // Ascending Order
            }

        });
        Iterator<Edge> iter = edges.iterator();

        while (iter.hasNext()) {
            Edge e = iter.next();
            vertices = e.getAdjVertices();
            if (!uf.union(vertices[0], vertices[1])) {
                iter.remove();
            }
        }
        Collections.sort(edges, new Comparator<Edge>() {// Sort Edges based on the adjacent vertices
            @Override
            public int compare(Edge e1, Edge e2) {
                int[] v1 = e1.getAdjVertices();
                int[] v2 = e2.getAdjVertices();
                if ((v1[0] > v1[1] ? v1[1] : v1[0]) == (v2[0] > v2[1] ? v2[1] : v2[0])) {
                    return (v1[0] < v1[1] ? v1[1] : v1[0]) - (v2[0] < v2[1] ? v2[1] : v2[0]);
                }

                return (v1[0] > v1[1] ? v1[1] : v1[0]) - (v2[0] > v2[1] ? v2[1] : v2[0]); // Ascending
            }
        });
        int[] v;
        int length = 0;
        for (Edge e : edges) { //Print Edges
            v = e.getAdjVertices();
            System.out.println((v[0] > v[1] ? v[1] : v[0]) + " " + (v[0] < v[1] ? v[1] : v[0]));
            length += e.getWeight();
        }
        System.out.println("Minimal Spanning tree length = " + length); //Print length of minimum spanning Tree
    }
}

class UnionFind {

    int[] elements;

    UnionFind(int n) { // creates a union find object for integer elements 1 ... n
        elements = new int[n + 1];
        for (int i = 0; i < elements.length; ++i) {
            elements[i] = -1; //initialize all nodes to be root
        }
    }

    boolean union(int x, int y) { // forms the union of elements x and y using the union by size strategy
        int a = find(x);               // If the sizes of the trees containing x and y are the same make
        int b = find(y);               // the tree containing y a subtree of the root of the tree containing x.
        if (a != b) {                    //If roots are not same
            if (elements[a] == elements[b] || elements[a] < elements[b]) { //If Both trees are equal in size
                elements[a] += elements[b]; //Increase the size of tree
                elements[b] = a; //Make the root of second argument to be subroot
                return true;
            } else if (elements[a] > elements[b]) { //If Second argument tree is bigger
                elements[b] += elements[a]; //Increase the size of tree
                elements[a] = b; //Make the root of first argument to be subroot
                return true;
            }
        }
        return false;
    }

    int find(int y) {                              // searches for element y and returns the key in the root of the tree
        int k = y;
        while (elements[k] >= 0) {  //If element is not root
            k = elements[k]; //Goto the root of the specific element
            
        }
        return k;
    }

}

class Edge {

    final private int weight;
    final private int v1, v2;

    public Edge(int v1, int v2, int weight) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }

    public int getAdjacent(int v) {
        return v1 != v2 ? v1 : v2;
    }

    public int[] getAdjVertices() {
        int[] adjVertices = {this.v1, this.v2};
        return adjVertices;
    }

    public int getWeight() {
        return this.weight;
    }

}
