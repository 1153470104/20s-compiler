package parser;

import lexer.*;
import javax.swing.text.Utilities;
import java.io.IOException;
import java.util.*;

public class Parser {
    public List<ItemSet> allSet = new LinkedList<>();
    public Stack<StackUnit> stack = new Stack<>();
    public Syntax syntaxStuff = new Syntax("./src/parser/clanguage.txt");
    public int[][] analysisChart;
    public List<String> symbolList = new LinkedList<>();
    public List<List<String>> syntaxList = new LinkedList<>();
    public Node firstNode;
    public Errors errors;

    public void printStack() {
        System.out.print("-------------stack: ");
        for(int i = 0; i < stack.size(); i++) {
            System.out.print(stack.get(i).element.nodeSymbol.element() + " ");
        }
        System.out.println();
    }

    public Node getTokenToNode(List<Token> inputList, int tokenIndex) {
        Node peek;
        if(tokenIndex == inputList.size()) {
            peek = new Node(new Word("$", '$'), new LinkedList<Node>());
            peek.nodeSymbol.line = inputList.get(inputList.size()-1).line;
        } else {
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

        // add very first input token
        Node temp = getTokenToNode(inputList, tokenIndex);
        line = temp.nodeSymbol.line;
        int indexOfPeek = symbolList.indexOf(temp.nodeSymbol.element());
        operation = analysisChart[stack.peek().status][indexOfPeek];

        //main iteration
        while(true) {
            printStack();
            System.out.println("symbol: " + temp.nodeSymbol);
            System.out.println("status: " + stack.peek().status);
            System.out.println("operation: " + operation);
            System.out.println("tokenIndex: " + tokenIndex);
            System.out.println();

            //当出错的时候
            if(operation > 5000) {
                errors.addError(operation, temp.nodeSymbol.line, stack.peek().status, analysisChart, temp.nodeSymbol.element());
                tokenIndex += 1;
                temp = getTokenToNode(inputList, tokenIndex);
                indexOfPeek = symbolList.indexOf(temp.nodeSymbol.element());
                operation = analysisChart[stack.peek().status][indexOfPeek];
            }
            //需要移入的时候
            if(operation >= 0) {
                stack.push(new StackUnit(operation, temp));

                //use analysisChart, get next operation
                tokenIndex += 1;
                temp = getTokenToNode(inputList, tokenIndex);
                indexOfPeek = symbolList.indexOf(temp.nodeSymbol.element());
                operation = analysisChart[stack.peek().status][indexOfPeek];

            //需要归约的时候
            } else if(operation != -10000) {
                //get the reduce max length
                //一个致命悬疑bug，在用正负性区分类别的时候一定要小心 0 的存在！！
                operation = operation + 5;
                int reduce = syntaxList.get(-1 * operation).size() - 1;
                List<String> product = syntaxList.get(-1 * operation);
                boolean canEmptyOrNot = syntaxStuff.evolveEmpty(product);
//                for(int i = reduce - 1; i < reduce; i++) {
//                    if(stack.get(0).element.nodeSymbol.element().equals(
//                            syntaxList.get(-1 * operation).get(0))){
//                        reduce = i;
//                        break;
//                    }
//                }

//                System.out.println("-------------------- reduce: " + reduce);
                Word ww = new Word(product.get(0), Tag.NONTERMINAL);
                ww.line = line;
                //新建一个node用来存储内容
                Node n = new Node(ww, new LinkedList<Node>());
//                for(int i = 0; i < reduce; i++) {
//                    String s = stack.peek().element.nodeSymbol.element();
//                    n.nodeSet.add(stack.peek().element);
//                    stack.pop();
//                }
                int popCount = 0;
                if(product.get(1).equals("empty")) {
                    n.nodeSet.add(new Node(new Word("empty", Tag.ID), new LinkedList<Node>()));
                } else {
                    while (popCount < product.size() - 1) {
                        //上下注释掉的部分，很牛逼。。。因为原本的方案里不会把空的非终结符填入，
                        //所以用栈顶和应当的栈顶比较，不同而且栈顶非终结符可以为空，那就改pop更前面的了
//                    String s = stack.peek().element.nodeSymbol.element();
                        n.nodeSet.add(stack.peek().element);
                        stack.pop();
                        popCount++;
//                    String p;
//                    do {
//                        p = product.get(product.size() - popCount - 1);
//                        popCount += 1;
//                    }while(!p.equals(s) && syntaxStuff.canEmpty(p));
                    }
                }

                int indexOfNt = symbolList.indexOf(n.nodeSymbol.element());
                int prevstatus = stack.peek().status;
                //give a new operation of the non-terminal symbol
                operation = analysisChart[prevstatus][indexOfNt];
                temp = n;
                tokenIndex -= 1;

            //分析结束
            } else {
                Word ww = new Word("Program", Tag.NONTERMINAL);
                ww.line = line;
                firstNode = new Node(ww, new LinkedList<>());
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
     * create a Action-Goto chart with item sets
     */
    public void createChart() {
        analysisChart = new int[allSet.size()][symbolList.size()];
        for(int ii = 0; ii < allSet.size(); ii++) {
            for(int jj = 0; jj < symbolList.size(); jj++) {
                analysisChart[ii][jj] = 10000;
            }
        }

        for(int i = 0; i < allSet.size(); i++) {
            for(Item everyItem: allSet.get(i).itemSet) {
                //acc和回收的条目
                if(everyItem.item == everyItem.units.size() || everyItem.units.get(1).equals("empty")) {
                    if(everyItem.units.get(0).equals("Program")) {
                        analysisChart[i][symbolList.indexOf("$")] = -10000;
                    } else {
                        String indexSymbol = everyItem.lookahead;
                        int syntaxIndex = indexOfSyntax(everyItem.units);
                        analysisChart[i][symbolList.indexOf(indexSymbol)]
                                = -1 * syntaxIndex - 5;
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
        errors = new Errors(symbolList);
        errors.setChart(analysisChart);
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
            //store the new added items
            Set<Item> modifySet = new HashSet<>();
            //iterate every item in item set
            for (Item i: s.itemSet) {
                //add special case of empty production
                //这里的操作会使得我原本出栈的逻辑失效。。。实现越来越不普适。。还好只有这一个empty
                // lab3加：
                //问题是后面会有毛毛多empty等着
                //这里让可能是空集的产生式 点在项目前和点在项目后的项目在一个项目集合里面

//                if(i.ifItemNextEmpty(syntaxList)) {
//                    modifySet.add(i.afterItem());
//                }

                //if the unit next is not terminal
                if (i.ifNonTerminalNext(nt)) {
                    for (List<String> l : syntaxList) {

                            //add new item into this item set
                            if (l.get(0).equals(i.units.get(i.item))) {
                                String nextNext = i.nextNextUnit();
                                if(nextNext == null || nextNext.equals("$"))
                                    modifySet.add(new Item(l,1, i.lookahead));
                                else {
                                    for (String first : firstMap.get(nextNext)) {
                                        if (first.equals("empty")) {
                                            //因为在计算first集的时候已经计算过一遍了。。。？？
                                            modifySet.add(new Item(l, 1, i.lookahead));
                                        } else {
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
                        if(!x.equals("empty")) {
                            ItemSet iset = Goto(allSet.get(count), x);
                            if (iset != null) {
                                allSet.add(iset);
                            }
                        }
                    }
                    count += 1;
                }
            }
        } while(size != allSet.size());
    }

    /** print the analysis diagram */
    public void printDiagram() {
        for (String s : symbolList) {
            System.out.print(s + "\t");
        }
        System.out.println();
        for(int i = 0; i < allSet.size(); i++) {
            for(int j = 0; j < symbolList.size(); j++) {
                System.out.print(analysisChart[i][j] + "\t");
            }
            System.out.println();
        }
    }

    static class StackUnit {
        public int status;
        public Node element;

        public StackUnit(int status, Node element) {
            this.status = status;
            this.element = element;
        }
    }

}
