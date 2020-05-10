package parser;

import java.util.LinkedList;
import java.util.List;

public class SignList {
    public List<SymbolEntry> symbolEntryList = new LinkedList<>();

    public SignList(){
    }

    public void printSignList() {
        System.out.println("signlist: ");
        for(SymbolEntry s: symbolEntryList) {
            System.out.println(s.toString());
        }
    }

    public void enter(String type, String lexeme, int offset) {
        this.symbolEntryList.add(new SymbolEntry(type, lexeme, offset));
    }

    public static class SymbolEntry {
        String type;
        String lexeme;
        int offset;

        public SymbolEntry(String type, String lexeme, int offset) {
            this.type = type;
            this.lexeme = lexeme;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "SymbolEntry{" +
                    "type='" + type + '\'' +
                    ", lexeme='" + lexeme + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }
}
