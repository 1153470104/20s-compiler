package parser;

import lexer.*;
import javax.swing.text.Utilities;
import java.io.IOException;
import java.util.*;

public class Parser {
    public List<ItemSet> allSet = new LinkedList<>();
    public Stack<StackUnit> stack = new Stack<>();
//    public Syntax syntaxStuff = new Syntax("./src/parser/syntax.txt");
    public Syntax syntaxStuff = new Syntax("./src/parser/syntax.txt");
    public int[][] analysisChart;
    public List<String> symbolList = new LinkedList<>();
    public List<List<String>> syntaxList = new LinkedList<>();
    public Node firstNode;
    List<ErrorInfo> errors = new LinkedList<>();

    public Node getTokenToNode(List<Token> inputList, int tokenIndex) {
        Node peek;
        if(tokenIndex == inputList.size()) {
            peek = new Node(new Word("$", '$'), new LinkedList<Node>());
            peek.nodeSymbol.line = inputList.get(inputList.size()-1).line;
        }else {
            peek = new Node(inputList.get(tokenIndex), new LinkedList<Node>());
        }
        return peek;
    }

    public void analyse(List<Token> inputList) {
        int line = 0;
        int tokenIndex = 0;
        int operation = 0;
        StackUnit first = new StackUnit(0, new Node(new Word("$", '$'), new LinkedList<Node>()));
        stack.push(first);

        Node temp;
        temp = getTokenToNode(inputList, tokenIndex);
        line = temp.nodeSymbol.line;
        int indexOfPeek = symbolList.indexOf(temp.nodeSymbol.element());
        operation = analysisChart[stack.peek().status][indexOfPeek];

        while(true) {
            System.out.println("symbol: " + temp.nodeSymbol);
            System.out.println("status: " + stack.peek().status);
            System.out.println("operation: " + operation);
            System.out.println("tokenIndex: " + tokenIndex);

            //当出错的时候
            if(operation > 500) {
                errors.add(new ErrorInfo(inputList.get(tokenIndex).line, "syntax error!"));
            }
            //需要移入的时候
            if(operation >= 0) {
                stack.push(new StackUnit(operation, temp));

                tokenIndex += 1;
                temp = getTokenToNode(inputList, tokenIndex);
                indexOfPeek = symbolList.indexOf(temp.nodeSymbol.element());
                operation = analysisChart[stack.peek().status][indexOfPeek];

            //需要归约的时候
            } else if(operation != -1000) {
                int reduce = syntaxList.get(-1 * operation).size() - 1;
//                System.out.println("-------------------- reduce: " + reduce);
                Word ww = new Word(syntaxList.get(-1 * operation).get(0), Tag.NONTERMINAL);
                ww.line = line;
                Node n = new Node(ww, new LinkedList<Node>());
                for(int i = 0; i < reduce; i++) {
                    n.nodeSet.add(stack.peek().element);
                    stack.pop();
                }
                int indexOfNt = symbolList.indexOf(n.nodeSymbol.element());
                int prevstatus = stack.peek().status;
                operation = analysisChart[prevstatus][indexOfNt];
                temp = n;
                tokenIndex -= 1;

            //分析结束
            } else {
                Word ww = new Word("P", Tag.NONTERMINAL);
                ww.line = line;
                firstNode = new Node(ww, new LinkedList<Node>());
                firstNode.nodeSet.add(stack.peek().element);
                break;
            }
//            System.out.println("status after: " + stack.peek().status);
//            System.out.println();
        }
    }

    public int indexOfSyntax(List<String> l) {
        for(int i = 0; i < syntaxList.size(); i++) {
            List<String> tempList = syntaxList.get(i);
            boolean isSame = true;
            if(l.size() == tempList.size()) {
                for(int j = 0; j < l.size(); j++) {
                    if(!l.get(j).equals(tempList.get(j))) {
                        isSame = false;
                        break;
                    }
                }
            } else {
                isSame = false;
            }
            if(isSame)
                return i;
        }
        return 6666;
    }

    public int indexOfSet(ItemSet s) {
        for(int i = 0; i < allSet.size(); i++) {
            if(allSet.get(i).setEquals(s)) {
                return i;
            }
        }
        return 8888;
    }
    /**
     * 接下来指定LR分析表的读取规则
     */
    public void createChart() {
        analysisChart = new int[allSet.size()][symbolList.size()];
        for(int ii = 0; ii < allSet.size(); ii++) {
            for(int jj = 0; jj < symbolList.size(); jj++) {
                analysisChart[ii][jj] = 1000;
            }
        }

        for(int i = 0; i < allSet.size(); i++) {
            for(Item everyItem: allSet.get(i).itemSet) {
                //acc和回收的条目
                if(everyItem.item == everyItem.units.size()) {
                    if(everyItem.units.get(0).equals("P")) {
                        analysisChart[i][symbolList.indexOf("$")] = -1000;
                    } else {
                        String indexSymbol = everyItem.lookahead;
                        int syntaxIndex = indexOfSyntax(everyItem.units);
                        analysisChart[i][symbolList.indexOf(indexSymbol)]
                                = -1 * syntaxIndex;
                    }
                //跳转的条目
                } else {
                    String nextUnit = everyItem.units.get(everyItem.item);
                    for (ItemSet.GotoItem g : allSet.get(i).gotoItemSet) {
                        if (g.gotoSet.equals(nextUnit)) {
                            analysisChart[i][symbolList.indexOf(nextUnit)]
                                    = indexOfSet(g.itemSet);
                        }
                    }
                }
            }
        }
    }

    public Parser() throws IOException {
        symbolList.addAll(syntaxStuff.terminal());
        symbolList.add("$");
        symbolList.addAll(syntaxStuff.nonterminal());
        syntaxList.addAll(syntaxStuff.syntax);
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
//                        if(!s.containsList(l)) {
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
//                        }
                    }
                }
//                System.out.print("try: ");s.printSet();
//                System.out.println();
            }
            for(Item i: modifySet) {
                s.addItem(i);
            }
        } while(setSize != s.itemSet.size());

//        System.out.println("set size: " + setSize);
    }

    /** goto method */
    public ItemSet Goto(ItemSet s, String x) {
        ItemSet newSet = new ItemSet();
        //对每一个set里面的item
        for(Item i: s.itemSet) {
            //检查下一个item是不是x
            if(i.ifItemNext(x)) {
                newSet.addItem(i.afterItem());
            }
        }
        s.gotoItemSet.add(new ItemSet.GotoItem(x, newSet));
        newSet.sourceItemSet.add(new ItemSet.GotoItem(x, s));
        if(newSet.itemSet.size() == 0) {
            return null;
        }return newSet;
    }
    /** the main parse method */
    public void parse(ItemSet veryFirst) {
        int size;
        allSet.add(veryFirst);
        int count = 0;
        do {
            size = allSet.size();
            for(; count < size; ) {
//                System.out.println("          the count: " + count); //把count到size的集合都扩充分了
//                System.out.println("          the size: " + size);
                closure(allSet.get(count)); //其中每个集合都发展下线

                //检查一下有没有已经有过的项目集出现，直接合并
                boolean deleteOrNot = false;
                for(int i = 0; i < count; i++) {
                    if(allSet.get(i).setEquals(allSet.get(count))) {
//                        System.out.println("combine!!");
                        size -= 1;
                        allSet.get(i).combineSourceSet(allSet.get(count));
                        allSet.remove(count);
                        deleteOrNot = true;
                        break;
                    }
                }

                //allSet.get(count).printSet()
                if(!deleteOrNot) {
                    for (String x : syntaxStuff.symbol) {
                        ItemSet iset = Goto(allSet.get(count), x);
                        if (iset != null) {
                            allSet.add(iset);
                        }
                    }
                    count += 1;
                }
            }
        } while(size != allSet.size());
    }

    /** print the analysis diagram */
    public void printDiagram() {
        for(int k = 0; k < symbolList.size(); k++) {
            System.out.print(symbolList.get(k) + "\t");
        }
        System.out.println();
        for(int i = 0; i < allSet.size(); i++) {
            for(int j = 0; j < symbolList.size(); j++) {
                System.out.print(analysisChart[i][j] + "\t");
            }
            System.out.println();
        }
    }

    class StackUnit {
        public int status;
        public Node element;

        public StackUnit(int status, Node element) {
            this.status = status;
            this.element = element;
        }
    }

    class ErrorInfo {
        public int line;
        public String errorInfo;

        public ErrorInfo(int line, String errorInfo) {
            this.line = line;
            this.errorInfo = errorInfo;
        }
    }
}
