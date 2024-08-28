package carcassonne.model

/**
 * Represents a generic graph with nodes and edges.
 *
 * @tparam T The type of nodes in the graph.
 */
trait Graph[T]:
  private var adjacencyList: Map[T, List[T]] = Map.empty

  /**
   * Adds a node to the graph.
   *
   * @param node The node to add.
   */
  def addNode(node: T): Unit =
    adjacencyList += (node -> List.empty)

  /**
   * Adds an edge between two nodes.
   *
   * @param node1 The first node.
   * @param node2 The second node.
   */
  def addEdge(node1: T, node2: T): Unit = {
    val connections1 = adjacencyList.getOrElse(node1, List.empty)
    val connections2 = adjacencyList.getOrElse(node2, List.empty)

    adjacencyList += (node1 -> (node2 :: connections1))
    adjacencyList += (node2 -> (node1 :: connections2))
  }

  /**
   * Retrieves the connections for a given node.
   *
   * @param node The node to retrieve connections for.
   * @return A list of connected nodes.
   */
  def getConnections(node: T): List[T] =
    adjacencyList.getOrElse(node, List.empty)

  /**
   * Removes a node from the graph.
   *
   * @param node The node to remove.
   */
  def removeNode(node: T): Unit = {
    adjacencyList -= node

    // Remove connections to this node from other nodes
    adjacencyList = adjacencyList.map { case (otherNode, connections) =>
      otherNode -> connections.filter(_ != node)
    }
  }

