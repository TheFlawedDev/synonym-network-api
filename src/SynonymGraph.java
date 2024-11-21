import edu.princeton.cs.algs4.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SynonymGraph {
    private SymbolGraph sg;
    private Graph graph;
    private ST<String, String> wordDefinitions;

    public SynonymGraph(){
        this.sg = new SymbolGraph("Resources/mthesaur.txt", ",");
        this.graph = sg.graph();
        wordDefinitions = new ST<>();
    }

    //work on find path and getconnections
    public List<String> findPath(String start, String end) {
        //check if the words are in the graph
        if(!sg.contains(start) || !sg.contains(end)){
            return null;
        }

        //get the start and end vertices
        int startVertex = sg.indexOf(start);
        int endVertex = sg.indexOf(end);

        //use bfs to find the shortest path
        BreadthFirstPaths bfs = new BreadthFirstPaths(graph, startVertex);

        //return message if there is no path
        if(!bfs.hasPathTo(endVertex)){
            return null;
        }

        //return path of vertices as a list of words
        List<String> path = new ArrayList<>();
        for(int vertex : bfs.pathTo(endVertex)){
            path.add(sg.nameOf(vertex));
        }


        return path;
    }

    public int getConnectionsLevel() {
        return 0;//TODO
    }

    public String generateWordAtDepth( String startWord, int targetDepth) {
        return null;//TODO
    }

    public static void main(String[] args) {
        SynonymGraph sg = new SynonymGraph();
        List<String> path = sg.findPath("happy", "joyful");

        if(path != null){
            System.out.println("Path from 'happy' to 'sad': ");
            System.out.println(String.join(" -> ", path));
        }else{
            System.out.println("No path found from 'happy' to 'sad'");
        }
    }
}
