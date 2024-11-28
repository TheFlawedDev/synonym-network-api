import edu.princeton.cs.algs4.*;
import org.graphstream.graph.implementations.SingleGraph;
import java.util.*;

/**
 * SynonymGraph represents a graph-based structure for exploring relationships
 * between words and their synonyms. It provides functionality to find paths,
 * calculate connection levels, and retrieve synonyms in the graph.
 *
 * <p>The graph is built using data from a file containing synonyms, where each
 * line represents a pair of related words. SynonymGraph leverages efficient
 * memory usage and the SymbolGraphMemoryEfficient class for indexing and lookup.
 *
 * <p>Features include:
 * <ul>
 *   <li>Finding the shortest path between two words using breadth-first search.</li>
 *   <li>Calculating the minimum number of synonym connections (levels) between two words.</li>
 *   <li>Fetching limited synonyms for words along a given path.</li>
 * </ul>
 *
 * Dependencies:
 * <ul>
 *   <li>edu.princeton.cs.algs4.Graph</li>
 *   <li>edu.princeton.cs.algs4.BreadthFirstPaths</li>
 *   <li>edu.princeton.cs.algs4.ST</li>
 *   <li>SymbolGraphMemoryEfficient - a custom implementation of SymbolGraph Class.</li>
 * </ul>
 *
 * Example Usage:
 * <pre>
 * {@code
 * SynonymGraph sg = new SynonymGraph();
 * List<String> path = sg.findPath("happy", "joyful");
 * int level = sg.getConnectionsLevel("happy", "joyful");
 * Map<String, Set<String>> synonyms = sg.getPathSynonyms("happy", "joyful");
 * }
 * </pre>
 *
 * <p>Input files must be formatted as plain text with synonyms separated by a delimiter
 * (e.g., commas). By default, the file is located at "Resources/".
 *
 * @author Jorge Velazquez, Nick Budd
 * @version 1.2
 */
public class SynonymGraph {
    private SymbolGraphMemoryEfficient sg;
    private Graph graph;
    private ST<String, String> wordDefinitions;


    public SynonymGraph() {
        this.sg = new SymbolGraphMemoryEfficient("src/main/resources/synonyms.txt", ",");
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
     * TODO: Improve path generation to guarantee target depth
     *
     * Current implementation:
     * - Uses MAX_ATTEMPTS (100) to repeatedly try generating valid paths
     * - Returns null if no path of exact target depth is found
     *
     * Limitation: Current approach relies on random generation which may miss valid paths
     *
     * Proposed improvement:
     * - Use graph traversal to first identify all words that exist at the target depth
     * - Then generate paths specifically to those pre-identified target words
     *
     * Challenge to solve: How to efficiently identify all words at a specific depth
     * from the start word without having a target end word?
     *
     * Possible approach:
     * - Use BFS/DFS to map all words at each depth level from start word
     * - Select target word from the mapped depth level
     * - Generate path to chosen target word
     */

    /**
     * Generates a list of connected words randomly from the start word. The count
     * of connections will be the same number as the target depth.
     *
     * @param startWord
     * @param targetDepth
     * @return list of random connected words from starting word to the last word at
     *         the indicated depth level
     */
    public List<String> generateWordAtDepth(String startWord, int targetDepth) {
        if (!sg.contains(startWord))
            return null;

        // NEW: Maximum attempts counter
        final int MAX_ATTEMPTS = 100;

        // NEW: Outer retry loop
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            List<String> randomWordPath = new ArrayList<String>();
            randomWordPath.add(startWord);

            // NEW: Path validity tracker
            boolean pathFound = true;

            for (int i = 0; i < targetDepth; i++) {
                String previousWord = randomWordPath.get(randomWordPath.size() - 1);
                List<String> adjList = new ArrayList<>();
                for (int v : graph.adj(sg.indexOf(previousWord))) {
                    String adjacentWord = sg.nameOf(v);
                    if (!randomWordPath.contains(adjacentWord)) {
                        adjList.add(adjacentWord);
                    }
                }

                // MODIFIED: Empty list handling
                if (adjList.isEmpty()) {
                    pathFound = false;  // NEW
                    break;
                }

                int randomIndex = StdRandom.uniformInt(0, adjList.size());
                randomWordPath.add(adjList.get(randomIndex));
            }

            // NEW: Check for valid path before returning
            if (pathFound && randomWordPath.size() == targetDepth + 1) {
                return randomWordPath;
            }
        }

        // NEW: Return null if no valid path found after all attempts
        return null;
    }
    public Graph getGraph() {
        return graph;
    }

    public boolean contains(String word) {
        return sg.contains(word);
    }

    public int indexOf(String word) {
        return sg.indexOf(word);
    }

    public String nameOf(int v) {
        return sg.nameOf(v);
    }

    /**
     * Gets a limited number of synonyms for words in the path from start to end
     *
     * @param path List of words forming the path
     * @return Map of each word in path to its limited set of synonyms
     */
    public Map<String, Set<String>> getPathSynonyms(List<String> path) {
        if (path == null)
            return null;

        Map<String, Set<String>> allSynonyms = new HashMap<>();
        int maxSynonymsPerWord = 4; // Limit the number of synonyms per word

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
                if (count >= maxSynonymsPerWord)
                    break;
            }

            allSynonyms.put(word, synonyms);
        }

        return allSynonyms;
    }

    public static void main(String[] args) {
        SynonymGraph sg = new SynonymGraph();
        In scanner = new In();

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Find connection between two words");
            System.out.println("2. Generate a random path of connected words");
            System.out.println("3. Test modified SymbolGraph class");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.readInt();

            switch (choice) {
                case 1:
                    System.out.println();
                    wordConnection(sg, scanner);
                    break;

                case 2:
                    System.out.println();
                    generateRandomWord(sg, scanner);
                    break;
                case 3:
                    System.out.println();
                    testModifiedSymbolGraph();
                    break;

                case 4:
                    System.out.println("\nExiting the program");
                    return;

                default:
                    System.out.println("\nInvalid choice. Please enter 1, 2, or 3.");
                    break;
            }
        }
    }

    private static void testModifiedSymbolGraph() {
        SymbolGraph sg = new SymbolGraph("src/main/resources/mthesaur.txt", ",");
        Graph graph = sg.graph();
        System.out.println("Original Class");
        System.out.println("Edges: " + graph.E());
        System.out.println("Vertices: " + graph.V());
        System.out.println();

        SymbolGraphMemoryEfficient sg2 = new SymbolGraphMemoryEfficient("src/main/resources/synonyms.txt", ",");
        Graph graph2 = sg2.graph();
        System.out.println("Modified Class");
        System.out.println("Edges: " + graph2.E());
        System.out.println("Vertices: " + graph2.V());
        System.out.println();

        System.out.println("Edges duplicated: " + (graph.E() - graph2.E()));
        System.out.println();

        System.setProperty("org.graphstream.ui", "javafx");
    }

    private static void wordConnection(SynonymGraph sg, In in) {
        List<String> path;

        System.out.print("Enter starting word: ");

        String start = in.readString().trim().toLowerCase();

        System.out.print("Enter target word: ");
        String end = in.readString().trim().toLowerCase();
        path = sg.findPath(start, end);
        if (path == null) {
            System.out.println("No path found between these two words");
        } else {
            System.out.println("Path: " + String.join(" -> ", path));
            System.out.println("Connection Level: " + (path.size() - 1));
        }
        System.out.println();
    }

    private static void generateRandomWord(SynonymGraph sg, In in) {
        List<String> path;

        System.out.print("Enter starting word: ");

        String start = in.readString().trim().toLowerCase();

        System.out.print("Enter the depth level: ");
        int depthLevel = in.readInt();
        path = sg.generateWordAtDepth(start, depthLevel);
        if (path == null) {
            System.out.println("Word not found");
        } else {
            System.out.println("Path: " + String.join(" -> ", path));
            System.out.println("Connection Level: " + (path.size() - 1));
        }
        System.out.println();

    }
}