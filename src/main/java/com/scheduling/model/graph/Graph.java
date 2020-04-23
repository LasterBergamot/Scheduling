package com.scheduling.model.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph<T> {

    private static final String COLON_AND_SPACE = ": ";
    private static final String SPACE = " ";
    private static final String NEW_LINE = "\n";

    // We use Hashmap to store the edges in the graph
    private Map<T, List<T>> graph = new HashMap<>();

    /**
     * This function adds the edge between source to destination
     * @param source
     * @param destination
     * @param bidirectional
     */
    public void addEdge(T source, T destination, boolean bidirectional) {
        if (!graph.containsKey(source)) {
            addVertex(source);
        }

        if (!graph.containsKey(destination)) {
            addVertex(destination);
        }

        graph.get(source).add(destination);

        if (bidirectional) {
            graph.get(destination).add(source);
        }
    }

    /**
     * This function adds a new vertex to the graph
     * @param vertex
     */
    private void addVertex(T vertex) {
        graph.put(vertex, new LinkedList<T>());
    }

    /**
     * This function gives the count of vertices
     */
    public int getVertexCount() {
        return graph.keySet().size();
    }

    /**
     * This function gives the count of edges
     * @param bidirectional
     */
    public int getEdgesCount(boolean bidirectional) {
        int count = 0;

        int asd = graph.keySet().stream().map(t -> graph.get(t).size()).reduce(0, Integer::sum);

        for (T v : graph.keySet()) {
            count += graph.get(v).size();
        }

        return bidirectional ? count / 2 : count;
    }

    /**
     * This function gives whether  a vertex is present or not.
     * @param vertex
     */
    public boolean hasVertex(T vertex) {
        return graph.containsKey(vertex);
    }

    /**
     * This function gives whether an edge is present or not.
     * @param source
     * @param destination
     */
    public boolean hasEdge(T source, T destination) {
        return graph.get(source).contains(destination);
    }

    /**
     * Prints the adjacency list of each vertex.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (T v : graph.keySet()) {
            builder.append(v.toString()).append(COLON_AND_SPACE);

            for (T w : graph.get(v)) {
                builder.append(w.toString()).append(SPACE);
            }

            builder.append(NEW_LINE);
        }

        return builder.toString();
    }
}
