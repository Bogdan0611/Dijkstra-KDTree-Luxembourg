# Route Planner - Luxembourg Map (Dijkstra + KD-Tree)

This Java application visualizes the map of Luxembourg and enables the calculation of the shortest path between two user-selected points. The project focuses on the efficiency of search algorithms and spatial data structures.

## Key Features

*   **Dijkstra's Algorithm**: Used to calculate the minimum distance between nodes in the graph.
*   **KD-Tree (K-Dimensional Tree)**: Implemented to allow rapid searching for the nearest node to the mouse coordinates (2D spatial search).
*   **SAX Parsing**: Efficient loading of geographical data from large XML files.
*   **Interactive Graphical Interface**:
    *   **Zoom**: Using the mouse wheel.
    *   **Pan (Navigation)**: Through mouse drag-and-drop.
    *   **Selection**: Click on the map to choose the start point (Green) and the destination (Blue).

## Project Structure

*   `Graph.java`: Manages the graph structure, data loading, and Dijkstra's algorithm.
*   `KDTree.java`: Data structure for efficient spatial queries.
*   `Node.java` / `Edge.java`: Data models for graph components.
*   `Main.java`: The graphical engine (Swing) and event handling.

## How to Use

1.  Ensure the `Harta_Luxemburg.xml` file is in the project's root directory.
2.  Run the `Main` class.
3.  Use the mouse to navigate the map.
4.  The first click sets the starting point.
5.  The second click sets the destination and automatically routes the shortest path (displayed in red).

## Technologies Used

*   Java SE
*   Swing (for GUI)
*   SAX Parser (for XML)
