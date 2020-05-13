package parser;

import lexer.Word;

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

    public boolean lookup(String s){
        for(SymbolEntry entry: symbolEntryList) {
            if(s.equals(entry.lexeme)) {
                return true;
            }
        }
        return false;
    }

    public void enter(String type, LRStack.StackUnit lexeme, int offset) {
        this.symbolEntryList.add(new SymbolEntry(type, lexeme, offset));
    }

    public static class SymbolEntry {
        String lexeme;
        LRStack.StackUnit type;
        int offset;

        public SymbolEntry(String lexeme, LRStack.StackUnit type, int offset) {
            this.type = type;
            this.lexeme = lexeme;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "SymbolEntry{" +
                    "type='" + type.fieldMap.get("type") + '\'' +
                    ", lexeme='" + lexeme + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }
}
