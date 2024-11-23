import edu.princeton.cs.algs4.*;
import java.util.*;

public class SynonymGraph {
    private SymbolGraph sg;
    private Graph graph;
    private ST<String, String> wordDefinitions;

    public SynonymGraph() {
        this.sg = new SymbolGraph("Resources/mthesaur.txt", ",");
        this.graph = sg.graph();
        wordDefinitions = new ST<>();
    }

    /**
     * Finds the shortest path between two words in the synonym graph.
     * Uses breadth-first search to determine the sequence of words connecting start to end.
     *
     * @param start The starting word to find a path from
     * @param end   The target word to find a path to
     * @return List of words forming the shortest path from start to end, or empty list if no path exists
     */
    public List<String> findPath(String start, String end) {
        //check if the words are in the graph
        if (!sg.contains(start) || !sg.contains(end)) {
            return null;
        }

        //get the start and end vertices
        int startVertex = sg.indexOf(start);
        int endVertex = sg.indexOf(end);

        //use bfs to find the shortest path
        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, startVertex);

        //return message if there is no path
        if (!bfs.hasPathTo(endVertex)) {
            return null;
        }

        //return path of vertices as a list of words
        List<String> path = new ArrayList<>();
        for (int vertex : bfs.pathTo(endVertex)) {
            path.add(sg.nameOf(vertex));
        }


        return path;
    }

    /**
     * Calculates the minimum number of synonym connections between two words.
     * Returns 0 if words are identical, -1 if no connection exists.
     *
     * @param start The starting word to measure distance from
     * @param end   The target word to measure distance to
     * @return Integer representing number of synonym steps between words
     */
    public int getConnectionsLevel(String start, String end) {
        //returns 0 if both words are equal
        if (start.equals(end)) {
            return 0;
        }

        //returns -1 if the graph does not contain start or ending word
        if (!sg.contains(start) || !sg.contains(end)) {
            return -1;
        }

        int startVertex = sg.indexOf(start);
        int endVertex = sg.indexOf(end);

        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, startVertex);

        //returns -1 if no path was found to ending word
        if (!bfs.hasPathTo(endVertex)) {
            return -1;
        }

        // Count edges (connections), not vertices
        int connections = -1;
        for (int vertex : bfs.pathTo(endVertex)) {
            connections++;
        }

        return connections;
    }

    public String generateWordAtDepth(String startWord, int targetDepth) {
        return null;//TODO
    }

    /**
     * Gets a limited number of synonyms for words in the path from start to end
     * @param start Starting word
     * @param end Target word
     * @return Map of each word in path to its limited set of synonyms
     */
    public Map<String, Set<String>> getPathSynonyms(String start, String end) {
        List<String> path = findPath(start, end);
        if (path == null) return null;

        Map<String, Set<String>> allSynonyms = new HashMap<>();
        int maxSynonymsPerWord = 5; // Limit the number of synonyms per word

        // For each word in the path, get limited adjacent vertices
        for (String word : path) {
            Set<String> synonyms = new HashSet<>();
            int wordVertex = sg.indexOf(word);

            // Get all adjacent vertices
            int count = 0;
            for (int adj : graph.adj(wordVertex)) {
                String synonym = sg.nameOf(adj);
                // Only add synonym if it's not in the main path and we haven't hit our limit
                if (!path.contains(synonym) && count < maxSynonymsPerWord) {
                    synonyms.add(synonym);
                    count++;
                }
                if (count >= maxSynonymsPerWord) break;
            }

            allSynonyms.put(word, synonyms);
        }

        return allSynonyms;
    }

    public static void main(String[] args) {
        SynonymGraph sg = new SynonymGraph();
        List<String> path;
        String input1;
        String input2;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter starting word (or ' ' to exit): ");

            String start = scanner.nextLine().trim().toLowerCase();

            if (start.isEmpty()) {
                break;
            }

            System.out.print("Enter target word: ");
            String end = scanner.nextLine().trim().toLowerCase();
            path = sg.findPath(start, end);
            int level = sg.getConnectionsLevel(start, end);
            if (path == null) {
                System.out.println("No path found between these two words");
            } else {
                System.out.println("Path: " + String.join(" -> ", path));
                System.out.println("Connection Level: " + level);
            }
            System.out.println();
        }
        scanner.close();
    }
}