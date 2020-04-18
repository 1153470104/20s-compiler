package parser;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class TestParser {

    public static void testSyntax() throws IOException {
        Syntax s = new Syntax("./src/parser/syntax.txt");

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

    public void testClosure() {
        
    }

    public static void main(String[] args) throws IOException {

    }
}
