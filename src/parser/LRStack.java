package parser;

import java.util.Stack;

public class LRStack {
    public Stack<StackUnit> lrStack = new Stack<>();

    public static class StackUnit {
        public int status;
        public Node element;

        public StackUnit(int status, Node element) {
            this.status = status;
            this.element = element;
        }
    }
}
