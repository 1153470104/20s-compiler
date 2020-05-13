package parser;

import com.sun.jdi.event.StepEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CodeList {
    public List<CodeEntry> codelist = new LinkedList<>();
    public List<List<String>> quanternary = new LinkedList<>();

    public CodeList() {

    }

    public void add(String a, String b, String c, String d) {
        List<String> s = new LinkedList<>();
        s.add(a); s.add(b); s.add(c); s.add(d);
        quanternary.add(s);
    }

    public int add(String code) {
        CodeEntry newEntry = new CodeEntry(code, lineNumber());
        codelist.add(newEntry);
        return newEntry.line;
    }

    public int lineNumber() {
        return codelist.size();
    }

    public static class CodeEntry{
        String code;
        int line;
        public CodeEntry(String code, int line) {
            this.code = code;
            this.line = line;
        }
    }

    public void printCode() {
        System.out.println("The Code:");
        for(int i = 0; i < codelist.size(); i++) {
            System.out.print(codelist.get(i).code);
            System.out.print("\t\t");
            System.out.println(Arrays.toString(quanternary.get(i).toArray()));
        }
    }
}
