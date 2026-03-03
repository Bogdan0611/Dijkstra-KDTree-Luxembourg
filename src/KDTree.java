import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KDTree {
    private class KDNode {
        Node node;
        KDNode left, right;
        public KDNode(Node node) { this.node = node; }
    }

    private KDNode root;

    public void build(List<Node> nodes) {
        root = buildRecursive(nodes, 0);
    }

    private KDNode buildRecursive(List<Node> nodes, int depth) {
        if (nodes.isEmpty()) return null;
        int axis = depth % 2;
        if (axis == 0) nodes.sort(Comparator.comparingInt(Node::getLongitude));
        else nodes.sort(Comparator.comparingInt(Node::getLatitude));

        int mid = nodes.size() / 2;
        KDNode kdNode = new KDNode(nodes.get(mid));
        kdNode.left = buildRecursive(new ArrayList<>(nodes.subList(0, mid)), depth + 1);
        kdNode.right = buildRecursive(new ArrayList<>(nodes.subList(mid + 1, nodes.size())), depth + 1);
        return kdNode;
    }

    public Node findNearest(int x, int y) {
        return findRecursive(root, x, y, 0, null);
    }

    private Node findRecursive(KDNode current, int x, int y, int depth, Node best) {
        if (current == null) return best;
        if (best == null || dist(current.node, x, y) < dist(best, x, y)) best = current.node;

        int axis = depth % 2;
        int diff = (axis == 0) ? x - current.node.getLongitude() : y - current.node.getLatitude();
        
        KDNode near = (diff < 0) ? current.left : current.right;
        KDNode far = (diff < 0) ? current.right : current.left;

        best = findRecursive(near, x, y, depth + 1, best);
        if (Math.abs(diff) < dist(best, x, y)) {
            best = findRecursive(far, x, y, depth + 1, best);
        }
        return best;
    }

    private double dist(Node n, int x, int y) {
        return Math.sqrt(Math.pow(n.getLongitude() - x, 2) + Math.pow(n.getLatitude() - y, 2));
    }
}