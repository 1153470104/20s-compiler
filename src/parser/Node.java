package parser;

import java.util.*;

public class Node {
    public String nodeSymbol;
    public Set<Node> nodeSet = new HashSet<>();

    public Node(String nodeSymbol, Set<Node> nodeSet) {
        this.nodeSymbol = nodeSymbol;
        this.nodeSet = nodeSet;
    }

    public void addNode(Node n) {
        this.nodeSet.add(n);
    }

    public void printNode() {
        while(this.nodeSet != null) {
            for(Node i: this.nodeSet) {
                i.printNode();
            }
        }
        System.out.println(this.nodeSymbol);
    }
}
