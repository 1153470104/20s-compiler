package parser;

import java.io.FileNotFoundException;
import java.util.Stack;

public class LRStack {
    public Stack<StackUnit> lrStack = new Stack<>();
    public Semantic semantic = new Semantic("" +
            "./src/parser/clanguage.txt", "./src/parser/codesemantic.txt");

    public LRStack() throws FileNotFoundException {
    }

    public static class StackUnit {
        public int status;
        public Node element;

        public StackUnit(int status, Node element) {
            this.status = status;
            this.element = element;
        }
    }
}
