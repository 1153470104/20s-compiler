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

    public int lookup(String s){
        for(int i = 0; i < symbolEntryList.size(); i++) {
            if(s.equals(symbolEntryList.get(i).lexeme)) {
                return i;
            }
        }
        return -1;
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
//            System.out.println(type.fieldMap);
            if(type.fieldMap.containsKey("array")) {
                String typeString = type.fieldMap.get("type");
                int i = 1;
                while(type.fieldMap.containsKey("array" + i)) {
                    String num = type.fieldMap.get("array" + i);
                    typeString = "array(" + num + ", " + typeString + ")";
                    i++;
                }
                String numOut = type.fieldMap.get("array");
                typeString = "array(" + numOut + ", " + typeString + ")";
                return "SymbolEntry{" +
                        "type='" + typeString + '\'' +
                        ", lexeme='" + lexeme + '\'' +
                        ", offset=" + offset +
                        '}';
            }
            return "SymbolEntry{" +
                    "type='" + type.fieldMap.get("type") + '\'' +
                    ", lexeme='" + lexeme + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }
}
