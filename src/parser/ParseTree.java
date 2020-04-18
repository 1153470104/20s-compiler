package parser;

import java.util.*;

public class ParseTree {
    public Node firstNode;

    public ParseTree(Node firstNode) {
        this.firstNode = firstNode;
    }
}

class Node {
    public String nodeSymbol;
    public Set<Node> nodeSet = new HashSet<>();

    public Node(String nodeSymbol, Set<Node> nodeSet) {
        this.nodeSymbol = nodeSymbol;
        this.nodeSet = nodeSet;
    }

    public void addNode(Node n) {
        this.nodeSet.add(n);
    }
}
