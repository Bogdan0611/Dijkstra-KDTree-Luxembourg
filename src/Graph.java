import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

/**
 * Manages the graph structure and shortest path search algorithms.
 * Uses a SAX Parser for efficient loading of geographical data.
 */
public class Graph {
    private Map<Integer, Node> nodes = new HashMap<>();
    private Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private int minLat = Integer.MAX_VALUE, maxLat = Integer.MIN_VALUE;
    private int minLon = Integer.MAX_VALUE, maxLon = Integer.MIN_VALUE;
    private KDTree kdTree;
    private int maxNodeId = 0;

    /**
     * Loads nodes and arcs from a specified XML file.
     * @param filePath Path to the XML file (Luxembourg Map).
     */
    public void loadFromXML(String filePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("node")) {
                        int id = Integer.parseInt(attributes.getValue("id"));
                        int lat = Integer.parseInt(attributes.getValue("latitude"));
                        int lon = Integer.parseInt(attributes.getValue("longitude"));
                        Node node = new Node(id, lat, lon);
                        nodes.put(id, node);
                        if (id > maxNodeId) maxNodeId = id;
                        if (lat < minLat) minLat = lat;
                        if (lat > maxLat) maxLat = lat;
                        if (lon < minLon) minLon = lon;
                        if (lon > maxLon) maxLon = lon;
                    } else if (qName.equalsIgnoreCase("arc")) {
                        int from = Integer.parseInt(attributes.getValue("from"));
                        int to = Integer.parseInt(attributes.getValue("to"));
                        int length = Integer.parseInt(attributes.getValue("length"));
                        adjacencyList.putIfAbsent(from, new ArrayList<>());
                        adjacencyList.get(from).add(new Edge(to, length));
                    }
                }
            };

            File file = new File(filePath);
            if (file.exists()) {
                saxParser.parse(file, handler);
                kdTree = new KDTree();
                kdTree.build(new ArrayList<>(nodes.values()));
            }
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
        }
    }

    /**
     * Calculates the shortest path between two nodes using Dijkstra's algorithm.
     * @param start The starting node.
     * @param end The destination node.
     * @return A list of nodes representing the shortest path, or null if none exists.
     */
    public List<Node> getShortestPath(Node start, Node end) {
        if (start == null || end == null) return null;

        // Use an array for performance, given the sequential IDs
        int[] distances = new int[maxNodeId + 1];
        int[] parents = new int[maxNodeId + 1];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(parents, -1);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        distances[start.getId()] = 0;
        pq.add(new int[]{start.getId(), 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];

            if (d > distances[u]) continue;
            if (u == end.getId()) break;

            List<Edge> neighbors = adjacencyList.get(u);
            if (neighbors != null) {
                for (Edge edge : neighbors) {
                    int v = edge.getTo();
                    int weight = edge.getLength();
                    if (distances[u] + weight < distances[v]) {
                        distances[v] = distances[u] + weight;
                        parents[v] = u;
                        pq.add(new int[]{v, distances[v]});
                    }
                }
            }
        }

        List<Node> path = new ArrayList<>();
        int curr = end.getId();
        if (parents[curr] == -1 && curr != start.getId()) return null;

        while (curr != -1) {
            path.add(nodes.get(curr));
            curr = parents[curr];
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Finds the nearest node to a screen location using the KD-Tree.
     */
    public Node getNearestNode(int mouseX, int mouseY, int width, int height, double scale, int offsetX, int offsetY) {
        if (kdTree == null) return null;
        int lon = (int) ((mouseX - offsetX) / scale) + minLon;
        int lat = (int) ((height - mouseY - offsetY) / scale) + minLat;
        return kdTree.findNearest(lon, lat);
    }


    public Map<Integer, Node> getNodes() { return nodes; }
    public Map<Integer, List<Edge>> getAdjacencyList() { return adjacencyList; }
    public int getMinLat() { return minLat; }
    public int getMaxLat() { return maxLat; }
    public int getMinLon() { return minLon; }
    public int getMaxLon() { return maxLon; }
}