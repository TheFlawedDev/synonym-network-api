package synonymnetwork.domain;

/******************************************************************************
 *  Compilation:  javac SymbolGraph.java
 *  Execution:    java SymbolGraph filename.txt delimiter
 *  Dependencies: ST.java Graph.java In.java StdIn.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/41graph/routes.txt
 *                https://algs4.cs.princeton.edu/41graph/movies.txt
 *                https://algs4.cs.princeton.edu/41graph/moviestiny.txt
 *                https://algs4.cs.princeton.edu/41graph/moviesG.txt
 *                https://algs4.cs.princeton.edu/41graph/moviestopGrossing.txt
 *
 *  %  java SymbolGraph routes.txt " "
 *  JFK
 *     MCO
 *     ATL
 *     ORD
 *  LAX
 *     PHX
 *     LAS
 *
 *  % java SymbolGraph movies.txt "/"
 *  Tin Men (1987)
 *     Hershey, Barbara
 *     Geppi, Cindy
 *     Jones, Kathy (II)
 *     Herr, Marcia
 *     ...
 *     Blumenfeld, Alan
 *     DeBoy, David
 *  Bacon, Kevin
 *     Woodsman, The (2004)
 *     Wild Things (1998)
 *     Where the Truth Lies (2005)
 *     Tremors (1990)
 *     ...
 *     Apollo 13 (1995)
 *     Animal House (1978)
 *
 *
 *  Assumes that input file is encoded using UTF-8.
 *  % iconv -f ISO-8859-1 -t UTF-8 movies-iso8859.txt > movies.txt
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code SymbolGraph} class represents an undirected graph, where the vertex names are
 * arbitrary strings. By providing mappings between string vertex names and integers, it serves as a
 * wrapper around the {@link Graph} data type, which assumes the vertex names are integers between 0
 * and <em>V</em> - 1. It also supports initializing a symbol graph from a file.
 *
 * <p>This implementation uses an {@link ST} to map from strings to integers, an array to map from
 * integers to strings, and a {@link Graph} to store the underlying graph. The <em>indexOf</em> and
 * <em>contains</em> operations take time proportional to log <em>V</em>, where <em>V</em> is the
 * number of vertices. The <em>nameOf</em> operation takes constant time.
 *
 * <p>For additional documentation, see <a href="https://algs4.cs.princeton.edu/41graph">Section
 * 4.1</a> of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Jorge Velazquez, Nick Budd // Modified SymbolGraph class
 */
public class SymbolGraphMemoryEfficient {
  private ST<String, Integer> st; // string -> index
  private String[] keys; // index -> string
  private Graph graph; // the underlying graph

  /**
   * Initializes a graph from a file using the specified delimiter. Each line in the file contains
   * the name of a vertex, followed by a list of the names of the vertices adjacent to that vertex,
   * separated by the delimiter.
   *
   * @param filename the name of the file
   * @param delimiter the delimiter between fields
   */
  public SymbolGraphMemoryEfficient(String filename, String delimiter) {
    st = new ST<String, Integer>();

    // First pass builds the index by reading strings to associate
    // distinct strings with an index
    In in = new In(filename);
    // while (in.hasNextLine()) {
    while (!in.isEmpty()) {
      String[] a = in.readLine().split(delimiter);
      for (int i = 0; i < a.length; i++) {
        if (!st.contains(a[i])) st.put(a[i], st.size());
      }
    }

    // inverted index to get string keys in an array
    keys = new String[st.size()];
    for (String name : st.keys()) {
      keys[st.get(name)] = name;
    }

    // edgeTracking symbol table tracks edges in order to avoid duplicating edges in
    // the graph.
    SeparateChainingHashST<String, Boolean> edgeTracker = new SeparateChainingHashST<>();

    // second pass builds the graph by connecting first vertex on each
    // line to all others
    graph = new Graph(st.size());
    in = new In(filename);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      int v = st.get(a[0]);
      for (int i = 1; i < a.length; i++) {
        int w = st.get(a[i]);
        // create string representation of edge with smaller vertex coming first
        String edge = (v < w ? v + "-" + w : w + "-" + v);
        // Check if edge already exists
        if (!edgeTracker.contains(edge)) {
          edgeTracker.put(edge, true); // Mark the edge as added
          graph.addEdge(v, w);
        }
      }
    }
  }

  /**
   * Does the graph contain the vertex named {@code s}?
   *
   * @param s the name of a vertex
   * @return {@code true} if {@code s} is the name of a vertex, and {@code false} otherwise
   */
  public boolean contains(String s) {
    return st.contains(s);
  }

  /**
   * Returns the integer associated with the vertex named {@code s}.
   *
   * @param s the name of a vertex
   * @return the integer (between 0 and <em>V</em> - 1) associated with the vertex named {@code s}
   * @deprecated Replaced by {@link #indexOf(String)}.
   */
  @Deprecated
  public int index(String s) {
    return st.get(s);
  }

  /**
   * Returns the integer associated with the vertex named {@code s}.
   *
   * @param s the name of a vertex
   * @return the integer (between 0 and <em>V</em> - 1) associated with the vertex named {@code s}
   */
  public int indexOf(String s) {
    return st.get(s);
  }

  /**
   * Returns the name of the vertex associated with the integer {@code v}.
   *
   * @param v the integer corresponding to a vertex (between 0 and <em>V</em> - 1)
   * @throws IllegalArgumentException unless {@code 0 <= v < V}
   * @return the name of the vertex associated with the integer {@code v}
   */
  public String nameOf(int v) {
    validateVertex(v);
    return keys[v];
  }

  /**
   * Returns the graph associated with the symbol graph. It is the client's responsibility not to
   * mutate the graph.
   *
   * @return the graph associated with the symbol graph
   */
  public Graph graph() {
    return graph;
  }

  // throw an IllegalArgumentException unless {@code 0 <= v < V}
  private void validateVertex(int v) {
    int V = graph.V();
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
  }

  /**
   * Unit tests the {@code SymbolGraph} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    String filename = args[0];
    String delimiter = args[1];
    SymbolGraphMemoryEfficient sg = new SymbolGraphMemoryEfficient(filename, delimiter);
    Graph graph = sg.graph();
    while (StdIn.hasNextLine()) {
      String source = StdIn.readLine();
      if (sg.contains(source)) {
        int s = sg.index(source);
        for (int v : graph.adj(s)) {
          StdOut.println("   " + sg.nameOf(v));
        }
      } else {
        StdOut.println("input not contain '" + source + "'");
      }
    }
  }
}

