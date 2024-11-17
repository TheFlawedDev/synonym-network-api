import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.SymbolDigraph;

import java.io.IOException;
import java.util.List;

public class SynonymGraph {
    private SymbolDigraph symbolDigraph;
    private Digraph graph;
    private ST<String, String> wordDefinitions;

    public SynonymGraph(String filename) throws IOException {}

    public void addSynonym(String word, String definition) {}

    public List<String> findPath(String start, String end) {
        return null;//TODO
    }
    public int getConnectionsLevel(){
        return 0;//TODO
    }

    public String generateWordAtDepth(String startWord, int targetDepth){
        return null;//TODO
    }
    public String getDefinition(String word){
        return null; //TODO
    }
    private void loadWordDefinitions() {}
    public static void main(String[] args) {}
}
