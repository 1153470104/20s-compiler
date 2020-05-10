package parser;

import java.util.LinkedList;
import java.util.List;

public class CodeList {
    public List<CodeEntry> codelist = new LinkedList<>();

    public CodeList() {

    }

    public void gen(String code, int line) {
        codelist.add(new CodeEntry(code, line));
    }

    public static class CodeEntry{
        String code;
        int line;

        public CodeEntry(String code, int line) {
            this.code = code;
            this.line = line;
        }
    }
}
