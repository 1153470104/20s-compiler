package lexer;

import java.io.*;
import java.util.*;
import symbol.*;

public class Lexer {
    public static int line = 1;
    char peek;
    Hashtable words = new Hashtable<>();
    List<Token> tokens = new LinkedList<>();
    String filename;
    private static int judgeEnd = 0;
    
    /**store word in words */
    void reserve(Word w) {
        words.put(w.lexeme, w);
    }
    
    /**
     * print tokens
     */
    public void tokenPrint() {
        for(Token t: tokens) {
            System.out.println("< " + (char)t.tag + ", " + t + " >");
        }
    }
    
    //constructor
    public Lexer(String name) {
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Char);
        reserve(Type.Bool);
        reserve(Type.Int);
        reserve(Type.Float);
        
        this.filename = name;
        File file = new File(filename);
        Token tok = new Token(peek);
        try {
            Reader reader = new InputStreamReader(new FileInputStream(file));
            do {
                tok = scan(reader);
                tokens.add(tok);
            }
            while(judgeEnd == 0);
            tokens.remove(tok);
            
        } catch (IOException e) {
            System.out.println("file read error.");
        }
    }
    
    //read next char
    //the thing is, if read() can't get a char, it return -1
    boolean readch(Reader reader) throws IOException {
        int temp = reader.read();
        //System.out.println(temp);
        if(temp < 0) {
            judgeEnd = 1;
            peek = ' ';
            return false;
        }
        
        peek = (char)temp;
        //System.out.println(peek);
        return true;
    }
    boolean readch(Reader reader, char c) throws IOException {
        readch(reader);
        if(peek != c)
            return false;
        peek = ' ';
        return true;
    }
    
    //main function: scan
    public Token scan(Reader reader) throws IOException {
        //问题就在这里，不是scan阻止不了它，而是这个for循环会在文件尾一直循环，
        //如果照之前的 (; ; readch()) 的话
        for( ; readch(reader); ) {
            if(peek == ' ' || peek == '\t')
                continue;
            else if(peek == '\n')
                line = line + 1;
            else
                break;
        }
        
        switch(peek) {
        case '&':
            if(readch(reader, '&'))  return Word.and;
            else  return new Token('&');
        case '|':
            if(readch(reader, '|'))  return Word.or;
            else   return new Token('|');
        case '=':
            if(readch(reader, '='))   return Word.eq;
            else   return new Token('=');
        case '!':
            if(readch(reader, '='))   return Word.ne;
            else   return new Token('!');
        case '<':
            if(readch(reader, '='))   return Word.le;
            else   return new Token('<');
        case '>':
            if(readch(reader, '='))  return Word.ge;
            else  return new Token('>');
        }
        
        if(Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                readch(reader);
            } while(Character.isDigit(peek));
            if(peek != '.')
                return new Num(v);
            float x = v;
            float d = 10;
            for(;;) {
                readch(reader);
                if(!Character.isDigit(peek))
                    break;
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            return new Real(x);
        }
        
        if(Character.isLetter(peek) || peek == '_') {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                readch(reader);
            } while(Character.isLetterOrDigit(peek));
            String s = b.toString();
            Word w = (Word)words.get(s);
            if(w != null)
                return w;
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        //System.out.println("judgeEnd: " + judgeEnd);
        //it's mysterious.. must instantiate it first
        Token returnTok = new Token(peek);
        if(judgeEnd == 1) {
            returnTok = null;
        }
        peek = ' ';
        return returnTok;
    }
    
}
