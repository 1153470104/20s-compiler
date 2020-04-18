package parser;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.util.*;

public class Parser {
    public List<ItemSet> allSet = new LinkedList<>();
    public Stack<stackUnit> stack = new Stack<>();
//    public Syntax syntaxStuff = new Syntax("./src/parser/syntax.txt");
    public Syntax syntaxStuff = new Syntax("./src/parser/little.txt");

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
            //因为有concurrentModificationException, 被迫加了一个Set数据结构避免iterate时候改变
            Set<Item> modifySet = new HashSet<>();
            //iterate every item in item set
            for (Item i: s.itemSet) {
                //System.out.print("try: ");s.printSet();
                //if the unit next is not terminal
                if (i.ifNonTerminalNext(nt)) {
                    for (List<String> l : syntaxList) {
                        if(!s.containsList(l)) {
                            //add new item into this item set
                            if (l.get(0).equals(i.units.get(i.item))) {
                                String nextNext = i.nextNextUnit();
                                if(nextNext == null || nextNext.equals("$"))
                                    modifySet.add(new Item(l,1, i.lookahead));
                                else {
                                    for (String first : firstMap.get(nextNext)) {
                                        if (first.equals("epsilon"))
                                            modifySet.add(new Item(l, 1, i.lookahead));
                                        else
                                            modifySet.add(new Item(l, 1, first));
                                    }
                                }
                            }
                        }

                    }
                }
//                System.out.print("try: ");s.printSet();
//                System.out.println();
            }
            for(Item i: modifySet) {
                s.addItem(i);
            }
        } while(setSize != s.itemSet.size());
        System.out.println("set size: " + setSize);
    }

    /** goto method */
    public ItemSet Goto(ItemSet s, String x) {
        ItemSet newSet = new ItemSet();
        for(Item i: s.itemSet) {
            if(i.ifItemNext(x))
                newSet.addItem(i.afterItem());
        }
        s.gotoItemSet.add(new ItemSet.GotoItem(x, newSet));
        if(newSet.itemSet.size() == 0) {
            return null;
        }
        return newSet;
    }

    /** the main parse method */
    public void parse(ItemSet veryFirst) {
        int size;
        allSet.add(veryFirst);
        int count = 0;
        do {
            size = allSet.size();
            for(; count < size; count++) {
                //把count到size的集合都扩充分了
                closure(allSet.get(count));
                //其中每个集合都发展下线
                for(String x: syntaxStuff.symbol) {
                    ItemSet i = Goto(allSet.get(count), x);
                    if(i != null)
                        allSet.add(i);
                }
            }
        } while(size != allSet.size());
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
