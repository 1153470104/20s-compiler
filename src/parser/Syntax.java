package parser;

import java.io.*;
import java.util.*;

public class Syntax {
    public Set<List<String>> syntax = new HashSet<>();
    public Set<String> symbol = new HashSet<>();
    public Map<String, Set<String>> firstSet = new HashMap<>();

    public Syntax(String fileName) throws IOException {
        this.addSyntax(fileName);
        this.scanSymbol();
        this.addFirstSet();
    }

    public void addFirstSet() {
        Set<String> nt = nonterminal();
        //first add terminal's first set
        for(String s: symbol) {
            if(isTerminal(s)){
                Set<String> set = new HashSet<>();
                set.add(s);
                firstSet.put(s, set);
            } else {
                firstSet.put(s, new HashSet<>());
            }
        }
        int count = countFirst();
        //then iterate to calculate non-terminal's set
         do {
            count = countFirst();
            System.out.println(count);
            for(String s: nt) {
                Set<String> set = new HashSet<>();
                for(List<String> l: syntax) {
                    if(l.get(0).equals(s)) {
                        for(int i = 1; i < l.size(); i++) {
                            addFirst(s, l.get(i));
                            if(!hasEpsilon(l.get(i)))
                                break;
                        }
                    }
                }
            }
        } while(count != countFirst());

    }

    public int countFirst(){
        int count = 0;
        for(String s: firstSet.keySet()) {
            count += firstSet.get(s).size();
        }
        return count;
    }

    public void addFirst(String t, String s) {
        Set<String> set = firstSet.get(s);
        for(String first: set) {
            firstSet.get(t).add(first);
        }
    }

    public boolean hasEpsilon(String s) {
        Set<String> set = firstSet.get(s);
        for(String first: set) {
            if(first.equals("epsilon"))
                return true;
        }
        return false;
    }

    public boolean isTerminal(String s) {
        return terminal().contains(s);
    }

    public void scanSymbol() {
        for(List<String> l: syntax) {
            for(String s: l) {
                if(!symbol.contains(s)) {
                    symbol.add(s);
                }
            }
        }
    }

    public Set<String> nonterminal() {
        Set<String> t = new HashSet<>();
        for(List<String> l: syntax) {
            t.add(l.get(0));
        }
        return t;
    }

    public Set<String> terminal() {
        Set<String> nt = this.nonterminal();
        Set<String> t = new HashSet<>();
        for(String s: symbol) {
            if(!nt.contains(s)) {
                t.add(s);
            }
        }
        return t;
    }

    public void addSyntax(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String[] line = new String[1];
        try{
            String stringLine;
            while (null != (stringLine = br.readLine())) {
                line = stringLine.split(" ");
                List<String> list = new LinkedList<>();
                for(int i = 0; i < line.length; i++) {
                    list.add(line[i]);
                }
                syntax.add(list);
            }

        } catch(Exception e) {
            System.out.println("IOException!");
        }
    }
}
