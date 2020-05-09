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

    public Set<String> nextFirstInProduct(Item item) {
        int length = item.units.size();
        int current = item.item;
        Set<String> bigFirstSet = new HashSet<>();
        for(int i = current + 1; i < length; i++) {
            bigFirstSet.addAll(firstSet.get(item.units.get(i)));
            if(!firstSet.get(item.units.get(i)).contains("empty")) {
                break;
            }
        }
        return bigFirstSet;
    }

    public boolean canEmpty(String s) {
        for(List<String> l: syntax) {
            if(l.get(0).equals(s) && l.get(1).equals("empty")) {
                return true;
            }
        }
        return false;
    }

    public boolean evolveEmpty(List<String> l) {
        if(l.get(1).equals("empty")) {
            return true;
        }
        for(String s: l) {
            for(List<String> l1: syntax) {
                if(l1.get(0).equals(s)){
                    if(l1.get(1).equals("empty"))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * calculate the FIRST set of terminals
     */
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
        int count;
        //then iterate to calculate non-terminal's set
         do {
            count = countFirst();
            for(String s: nt) {
                Set<String> set = new HashSet<>();
                for(List<String> l: syntax) {
                    if(l.get(0).equals(s)) {
                        for(int i = 1; i < l.size(); i++) {
                            addFirst(s, l.get(i));
                            if(!hasEmpty(l.get(i)))
                                break;
                        }
                    }
                }
            }
        } while(count != countFirst());
    }

    /**
     * count the sum of first set
     * use to judge if first algorithm is finished
     */
    public int countFirst(){
        int count = 0;
        for(String s: firstSet.keySet()) {
            count += firstSet.get(s).size();
        }
        return count;
    }

    /**
     * add one terminal's first set to another
     */
    public void addFirst(String t, String s) {
        Set<String> set = firstSet.get(s);
//        System.out.println(set);
        for(String first: set) {
            firstSet.get(t).add(first);
        }
    }

    /**
     * judge if empty terminal symbol exist in "s" first set
     */
    public boolean hasEmpty(String s) {
        Set<String> set = firstSet.get(s);
        for(String first: set) {
            if(first.equals("empty"))
                return true;
        }
        return false;
    }

    public boolean isTerminal(String s) {
        return terminal().contains(s);
    }

    /**
     * generate symbol set
     */
    public void scanSymbol() {
        for(List<String> l: syntax) {
            symbol.addAll(l);
        }
    }

    /**
     * generate non-terminal symbol set
     */
    public Set<String> nonterminal() {
        Set<String> t = new HashSet<>();
        for(List<String> l: syntax) {
            t.add(l.get(0));
        }
        return t;
    }

    /**
     * generate terminal symbol set
     */
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
        String[] line = new String[0];
        try{
            String stringLine;
            while (null != (stringLine = br.readLine())) {
                line = stringLine.split(" ");
                List<String> list = new LinkedList<>(Arrays.asList(line));
                syntax.add(list);
            }

        } catch(Exception e) {
            System.out.println("IOException!");
        }
    }
}
