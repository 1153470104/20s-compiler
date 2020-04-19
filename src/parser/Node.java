package parser;

import lexer.*;

import java.util.*;

public class Node {
    public Token nodeSymbol;
    public List<Node> nodeSet = new LinkedList<>();

    public Node(Token nodeSymbol, List<Node> nodeSet) {
        this.nodeSymbol = nodeSymbol;
        this.nodeSet = nodeSet;
    }

    public void addNode(Node n) {
        this.nodeSet.add(n);
    }

    public void printNode(int i) {
        for(int j = 0; j < i; j++) {
            System.out.print("  ");
        }
        System.out.print(this.nodeSymbol.element());
        if(nodeSymbol.tag == 264)
            System.out.print(" " + ((Word)nodeSymbol).lexeme);
        System.out.println(" (" + this.nodeSymbol.line + ")");

        for(int k = nodeSet.size() - 1; k >= 0; k--) {
            nodeSet.get(k).printNode(i + 1);
        }
    }
}
