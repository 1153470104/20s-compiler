package parser;

import java.io.IOException;
import java.util.*;

public class Parser {
    public Set<ItemSet> allSet = new HashSet<>();
    public Stack<stackUnit> stack = new Stack<>();
    public Syntax syntaxStuff = new Syntax("./src/parser/syntax.txt");

    public Parser() throws IOException {
    }

    /** the closure function */
    public void closure(ItemSet s) {
        Set<List<String>> syntaxList = syntaxStuff.syntax;
        Set<String> nt = syntaxStuff.nonterminal();
        Map<String, Set<String>> firstMap = syntaxStuff.firstSet;

        int setSize = s.itemSet.size();
        do {
            setSize = s.itemSet.size();
            //iterate every item in item set
            for (Item i : s.itemSet) {

                //if the unit next is not terminal
                if (i.ifNonTerminalNext(nt)) {
                    for (List<String> l : syntaxList) {

                        //add new item into this item set
                        if (l.get(0).equals(i.units.get(i.item))) {
                            for (String first : firstMap.get(i.nextNextUnit())) {
                                if (first.equals("epsilon"))
                                    s.addItem(new Item(l, 0, i.lookahead));
                                else
                                    s.addItem(new Item(l, 0, first));
                            }
                        }
                    }
                }

            }
        } while(setSize == s.itemSet.size());
    }

    /** goto method */
    public void Goto() {

    }

    /** the main parse method */
    public void parse() {

    }

    /** print the analysis diagram */
    public void printDiagram() {

    }

    class stackUnit {
        public ItemSet currentItemSet;
        public String currentString;

        public stackUnit(ItemSet currentItemSet, String currentString) {
            this.currentItemSet = currentItemSet;
            this.currentString = currentString;
        }
    }
}
