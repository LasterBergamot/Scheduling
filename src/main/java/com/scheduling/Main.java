package com.scheduling;

import com.scheduling.graph.Graph;

public class Main {

    public static void main(String[] args) {

        // Object of graph is created.
        Graph<Integer> graph = new Graph<>();

        // edges are added.
        // Since the graph is bidirectional,
        // so boolean bidirectional is passed as true.
        graph.addEdge(0, 1, true);
        graph.addEdge(0, 4, true);
        graph.addEdge(1, 2, true);
        graph.addEdge(1, 3, true);
        graph.addEdge(1, 4, true);
        graph.addEdge(2, 3, true);
        graph.addEdge(3, 4, true);

        // print the graph.
        System.out.println("Graph:\n" + graph.toString());

        // gives the no of vertices in the graph.
        System.out.println(String.format("Number of vertices: %d", graph.getVertexCount()));

        // gives the no of edges in the graph.
        System.out.println(String.format("Number of edges: %d", graph.getEdgesCount(true)));

        // tells whether the edge is present or not.
        System.out.println(String.format("Is there and edge between node %d and %d? %s", 3 , 4, graph.hasEdge(3, 4)));

        // tells whether vertex is present or not
        System.out.println(String.format("Is vertex %d present? %s", 5, graph.hasVertex(5)));

    }
}
