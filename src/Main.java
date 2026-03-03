import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class Main extends JPanel {
    private Graph graph = new Graph();
    private Node startNode, endNode;
    private List<Node> path;
    private double zoom = 1.0;
    private int panX = 0, panY = 0, lastMouseX, lastMouseY;

    public Main() {
        String fileName = "Harta_Luxemburg.xml";
        File f = new File(fileName);
        if (!f.exists()) f = new File("../" + fileName);
        
        graph.loadFromXML(f.getAbsolutePath());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (graph.getNodes().isEmpty()) return;
                double scale = calculateScale() * zoom;
                int offX = calculateOffsetX(scale);
                int offY = calculateOffsetY(scale);

                Node clicked = graph.getNearestNode(e.getX(), e.getY(), getWidth(), getHeight(), scale, offX, offY);
                if (clicked != null) {
                    if (startNode == null || (startNode != null && endNode != null)) {
                        startNode = clicked;
                        endNode = null;
                        path = null;
                    } else {
                        endNode = clicked;
                        path = graph.getShortestPath(startNode, endNode);
                    }
                    repaint();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                panX += e.getX() - lastMouseX;
                panY += e.getY() - lastMouseY;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) zoom *= 1.1;
            else zoom /= 1.1;
            repaint();
        });
    }

    private double calculateScale() {
        return Math.min((double) getWidth() / (graph.getMaxLon() - graph.getMinLon()), 
                        (double) getHeight() / (graph.getMaxLat() - graph.getMinLat()));
    }
    private int calculateOffsetX(double scale) { return (getWidth() - (int) ((graph.getMaxLon() - graph.getMinLon()) * scale)) / 2 + panX; }
    private int calculateOffsetY(double scale) { return (getHeight() - (int) ((graph.getMaxLat() - graph.getMinLat()) * scale)) / 2 + panY; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph.getNodes().isEmpty()) return;

        double scale = calculateScale() * zoom;
        int offX = calculateOffsetX(scale);
        int offY = calculateOffsetY(scale);

        g.setColor(Color.LIGHT_GRAY);
        Map<Integer, Node> nodes = graph.getNodes();
        for (Map.Entry<Integer, List<Edge>> entry : graph.getAdjacencyList().entrySet()) {
            Node n1 = nodes.get(entry.getKey());
            int x1 = (int) ((n1.getLongitude() - graph.getMinLon()) * scale) + offX;
            int y1 = getHeight() - (int) ((n1.getLatitude() - graph.getMinLat()) * scale) - offY;
            for (Edge edge : entry.getValue()) {
                Node n2 = nodes.get(edge.getTo());
                if (n2 != null) {
                    int x2 = (int) ((n2.getLongitude() - graph.getMinLon()) * scale) + offX;
                    int y2 = getHeight() - (int) ((n2.getLatitude() - graph.getMinLat()) * scale) - offY;
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }

        if (path != null) {
            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            for (int i = 0; i < path.size() - 1; i++) {
                Node n1 = path.get(i), n2 = path.get(i + 1);
                int x1 = (int) ((n1.getLongitude() - graph.getMinLon()) * scale) + offX;
                int y1 = getHeight() - (int) ((n1.getLatitude() - graph.getMinLat()) * scale) - offY;
                int x2 = (int) ((n2.getLongitude() - graph.getMinLon()) * scale) + offX;
                int y2 = getHeight() - (int) ((n2.getLatitude() - graph.getMinLat()) * scale) - offY;
                g.drawLine(x1, y1, x2, y2);
            }
        }

        if (startNode != null) { g.setColor(Color.GREEN); drawDot(g, startNode, scale, offX, offY); }
        if (endNode != null) { g.setColor(Color.BLUE); drawDot(g, endNode, scale, offX, offY); }
    }

    private void drawDot(Graphics g, Node n, double scale, int offX, int offY) {
        int x = (int) ((n.getLongitude() - graph.getMinLon()) * scale) + offX;
        int y = getHeight() - (int) ((n.getLatitude() - graph.getMinLat()) * scale) - offY;
        g.fillOval(x - 5, y - 5, 10, 10);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tema 5 AG - Dijkstra");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.add(new Main());
        frame.setVisible(true);
    }
}