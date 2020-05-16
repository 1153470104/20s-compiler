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

    public void append(String content, int index) {
        codelist.get(index).code = codelist.get(index).code + " " + content;
        quanternary.get(index).remove(3);
        quanternary.get(index).add(content);
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
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(String s: quanternary.get(i)) {
                if(s != null) {
                    sb.append(" " + s + ",");
                }
                if(s == null) {
                    sb.append(" -,");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(" ]");
            System.out.println(sb.toString());
        }
    }
}
