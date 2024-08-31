package carcassonne.model

import carcassonne.model.board.Graph
import org.scalatest.funsuite.AnyFunSuite

class TestGraph extends Graph[String]

class GraphSuite extends AnyFunSuite {

  test("addNode should add a node to the graph") {
    val graph = new TestGraph
    graph.addNode("A")

    assert(graph.getConnections("A").isEmpty, "Node 'A' should have no connections after being added")
  }

  test("addEdge should add an edge between two nodes") {
    val graph = new TestGraph
    graph.addNode("A")
    graph.addNode("B")
    graph.addEdge("A", "B")

    assert(graph.getConnections("A") == List("B"), "Node 'A' should be connected to 'B'")
    assert(graph.getConnections("B") == List("A"), "Node 'B' should be connected to 'A'")
  }

  test("addEdge should handle multiple edges correctly") {
    val graph = new TestGraph
    graph.addNode("A")
    graph.addNode("B")
    graph.addNode("C")

    graph.addEdge("A", "B")
    graph.addEdge("A", "C")

    assert(graph.getConnections("A").sorted == List("B", "C").sorted, "Node 'A' should be connected to 'B' and 'C'")
    assert(graph.getConnections("B") == List("A"), "Node 'B' should be connected to 'A'")
    assert(graph.getConnections("C") == List("A"), "Node 'C' should be connected to 'A'")
  }

  test("getConnections should return an empty list if node has no connections") {
    val graph = new TestGraph
    graph.addNode("A")

    assert(graph.getConnections("A").isEmpty, "Node 'A' should have no connections")
  }

  test("getConnections should return an empty list for non-existent nodes") {
    val graph = new TestGraph

    assert(graph.getConnections("NonExistent").isEmpty, "Non-existent node should have no connections")
  }

  test("removeNode should remove a node and its edges") {
    val graph = new TestGraph
    graph.addNode("A")
    graph.addNode("B")
    graph.addEdge("A", "B")

    graph.removeNode("A")

    assert(graph.getConnections("A").isEmpty, "Node 'A' should be removed and have no connections")
    assert(graph.getConnections("B").isEmpty, "Node 'B' should have no connections after 'A' is removed")
  }

  test("removeNode should handle removal of non-existent nodes gracefully") {
    val graph = new TestGraph
    graph.addNode("A")
    graph.addNode("B")
    graph.addEdge("A", "B")

    graph.removeNode("NonExistent")

    assert(graph.getConnections("A") == List("B"), "Node 'A' should still be connected to 'B'")
    assert(graph.getConnections("B") == List("A"), "Node 'B' should still be connected to 'A'")
  }

  test("removeNode should correctly update other nodes' connections") {
    val graph = new TestGraph
    graph.addNode("A")
    graph.addNode("B")
    graph.addNode("C")

    graph.addEdge("A", "B")
    graph.addEdge("B", "C")

    graph.removeNode("B")

    assert(graph.getConnections("A").isEmpty, "Node 'A' should have no connections after 'B' is removed")
    assert(graph.getConnections("C").isEmpty, "Node 'C' should have no connections after 'B' is removed")
  }

}
