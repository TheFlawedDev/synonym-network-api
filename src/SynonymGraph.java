import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.SymbolDigraph;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SynonymGraph {
    private SymbolDigraph symbolDigraph;
    private Digraph graph;
    private ST<String, String> wordDefinitions;

    public SynonymGraph(){
        symbolDigraph = new SymbolDigraph("Resources/Thesaurus.txt", ",");
        graph = symbolDigraph.G();
        wordDefinitions = new ST<>();
    }

    public void addSynonym(String word) throws IOException {
        //check if the word is already in the graph
        if (symbolDigraph.contains(word)) {
            return;
        }

        //add new word to the graph
        try (FileWriter fw = new FileWriter("Resources/Thesaurus.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.newLine();
            bw.write(word + ",");
        }

        //reset the symbolDigraph and graph to reflect the new word addition
        symbolDigraph = new SymbolDigraph("Resources/Thesaurus.txt", ",");
        graph = symbolDigraph.G();
    }

    //work on find path and getconnections
    public List<String> findPath(String start, String end) {
        return null;//TODO
    }

    public int getConnectionsLevel() {
        return 0;//TODO
    }

    public String generateWordAtDepth(String startWord, int targetDepth) {
        return null;//TODO
    }

//    public String getDefinition(String word) {
//        return null; //TODO
//    }
//
//    private void loadWordDefinitions() {
//    }


    public static void main(String[] args) {
        try {
            // Create a new SynonymGraph instance
            SynonymGraph synonymGraph = new SynonymGraph();

            // Test existing words in the graph
            System.out.println("\nTesting initial graph content:");
            testWord(synonymGraph, "happy");
            testWord(synonymGraph, "joyful");

            // Test adding new words
            System.out.println("\nTesting adding new words:");

            // Try adding a new word
            System.out.println("Adding 'ecstatic' to the graph...");
            synonymGraph.addSynonym("ecstatic");
            synonymGraph.addSynonym("happryiess");
            testWord(synonymGraph, "ecstatic");

            // Try adding an existing word
            System.out.println("\nTrying to add 'happy' (existing word)...");
            synonymGraph.addSynonym("happy");
            testWord(synonymGraph, "happy");

            // Add another new word
            System.out.println("\nAdding 'delighted' to the graph...");
            synonymGraph.addSynonym("delighted");
            testWord(synonymGraph, "delighted");

            // Print all words in the graph
            System.out.println("\nAll words in the graph:");
            printAllWords(synonymGraph);

        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to test if a word is in the graph
    private static void testWord(SynonymGraph graph, String word) {
        boolean exists = graph.symbolDigraph.contains(word);
        System.out.println("Word '" + word + "' " +
                (exists ? "exists" : "does not exist") + " in the graph");
    }

    // Helper method to print all words in the graph
    private static void printAllWords(SynonymGraph graph) {
        Digraph g = graph.graph;
        System.out.println("Number of vertices: " + g.V());
        for (int v = 0; v < g.V(); v++) {
            String word = graph.symbolDigraph.nameOf(v);
            System.out.println("Vertex " + v + ": " + word);
            System.out.println("  Outgoing edges to: ");
            for (int w : g.adj(v)) {
                System.out.println("    " + graph.symbolDigraph.nameOf(w));
            }
        }
    }
}
