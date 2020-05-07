package parser;

import lexer.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ParserClient {

    public static void testSyntax() throws IOException {
        Syntax s = new Syntax("./src/parser/syntax.txt");

        System.out.println(s.syntax.size());
        Set<String> t = s.terminal();
        Set<String> nt = s.nonterminal();

        System.out.println("symbol");
        System.out.println(s.symbol);
        System.out.println("terminal");
        System.out.println(t);
        System.out.println("nonterminal");
        System.out.println(nt);
        System.out.println("first");
        System.out.println(s.firstSet);
    }

    public static void testClosure() throws IOException {
        Parser p = new Parser();
        Item i = new Item(List.of("P", "P'"), 1, "$");
        ItemSet set = new ItemSet();
        set.addItem(i);

        set.printSet();
        System.out.println();
        p.closure(set);
        set.printSet();
    }

    public static void testParse() throws IOException {
        Parser p = new Parser();
        Item i = new Item(List.of("P", "P'"), 1, "$");
        ItemSet set = new ItemSet();
        set.addItem(i);

        p.parse(set);
        for(int j = 0; j < p.allSet.size(); j++) {
            System.out.println();
            p.allSet.get(j).printSet();
        }
    }

    public static void testChart() throws IOException {
        Parser p = new Parser();
        Item i = new Item(List.of("P", "P'"), 1, "$");
        ItemSet set = new ItemSet();
        set.addItem(i);

        p.parse(set);
        p.createChart();
        p.printDiagram();
    }

    public static void testAnalyse(String filename) throws IOException {
        Lexer l1 = new Lexer(filename);
        for(Token t: l1.tokens) {
            System.out.print(t.line + "      \t");
            System.out.print(t + "      \t");
            System.out.println(t.element());
        }

        Parser p = new Parser();
        System.out.println(p.syntaxStuff.firstSet);
        Item i = new Item(List.of("P", "P'"), 1, "$");
//        Item i = new Item(List.of("Program", "P"), 1, "$");
        ItemSet set = new ItemSet();
        set.addItem(i);

//        System.out.println("1/////////////////////");

        p.parse(set);
        for(int j = 0; j < p.allSet.size(); j++) {
            System.out.println();
            p.allSet.get(j).printSet();
        }

//        System.out.println("2/////////////////////");
        p.createChart();
        p.printDiagram();

        p.analyse(l1.tokens);
        p.firstNode.printNode(0);
        p.errors.errorPrint();
    }

    public static void main(String[] args) throws IOException {
//        testClosure();
//        testSyntax();
//        testParse();
//        testChart();
        testAnalyse("./src/lexer/test/2.txt");
//        testAnalyse("./src/lexer/test/4.txt");
//        testAnalyse("./src/lexer/test/5.txt");
//        testAnalyse("./src/lexer/test/6.txt");
//        testAnalyse("./src/lexer/test/2.txt");
    }
}
