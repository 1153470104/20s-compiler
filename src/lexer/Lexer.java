package lexer;

import java.io.*;
import java.util.*;

import symbol.*;

public class Lexer {
    public static int line = 1;
    //最开始的地方老是会返回一个空token，发现原来是因为本来的peek 没有值，
    //导致系统不做任何跳过就直接导出一个token了。。所以还是要设为 ' ',才对
    char peek = ' ';
    Hashtable words = new Hashtable<>();
    List<Token> tokens = new LinkedList<>();
    String filename;
    List<ErrorInfo> errors = new LinkedList<Lexer.ErrorInfo>();
    
    public class ErrorInfo {
        public int line;
        public String wrongInfo;
        public ErrorInfo(int n, String v) {
            line = n;
            wrongInfo = v;
        }
    }
    
    //judge if it's the end of the reader
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
            if(t.tag < 256) {
                System.out.println("< " + (char)t.tag + " >");
            } else {
            System.out.println("< " + t + " >");
            }
        }
    }
    
    public void errorPrint() {
        for(ErrorInfo e: errors) {
            System.out.println("Error in line " + e.line + ": " + e.wrongInfo);
        }
    }
    
    //constructor
    public Lexer(String name) {
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve(new Word("return", Tag.RETURN));

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
                //System.out.println("tok: " + tok);
                if(tok != null) 
                    tokens.add(tok);
            }
            while(judgeEnd == 0);
            //tokens.remove(tok);
            reader.close();
            
        } catch (IOException e) {
            errors.add(new ErrorInfo(line, "Incorrect Input"));
            //System.out.println("file read error.");
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
        //这个特性！返回' '的特性使得peek先读一位的逻辑没有整体失效
        peek = ' ';
        return true;
    }
    
    //main function: scan
    public Token scan(Reader reader) throws IOException {
        //问题就在这里，不是scan阻止不了它，而是这个for循环会在文件尾一直循环，
        //如果照之前的 (; ; readch()) 的话
        //but new problem occur
        //现在的状态是会先再read()取一个,就导致了没有空格的东西无法识别
        while(peek == ' ' || peek == '\t' || peek == '\r' || peek == '\n') {
            if(peek == '\n')
                line = line + 1;
            if(!readch(reader))
                break;
        }
        
        //去注释
        if(peek == '/') {
            int state = 1;
            while(judgeEnd == 0) {
                switch(state) {
                case 1:
                    readch(reader);
                    if(peek == '*')
                        state = 2;
                    else
                        return new Token('/');
                    break;
                case 2:
                    readch(reader);
                    if(peek == '*')
                        state = 3;
                    else if(peek == '"')
                        state = 5;
                    break;
                case 3:
                    readch(reader);
                    if(peek == '*')
                        state = 3;
                    else if(peek == '/') {
                        peek = ' '; 
                        return null;
                    } else {
                        state = 2;
                    }
                    if(peek == '"')
                        state = 4;
                    break;
                case 4:
                    readch(reader);
                    if(peek == '"')
                        state = 2;
                    break;
                }
            }
        }
        
        if(peek == '"' ) {
            StringBuffer b = new StringBuffer();
            b.append(peek);
            while(!readch(reader, '"')) {
                b.append(peek);
                if(judgeEnd == 1)
                    return null;
            }
            b.append('"');
            return new ConstString(b.toString());
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
            int state = 0;
            int v = 0;  float x = 0;  float d = 10;  int mul = 0; int PorN = 1;
            
            //System.out.println(peek);
            int firstValue = Character.digit(peek, 10);
            if(firstValue == 0)
                state = 10;
            while(judgeEnd == 0) {
                switch(state) {
                case 0:
                    v = firstValue;
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 1;
                    break;
                case 1:
                    v = v * 10 + Character.digit(peek, 10);
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 1;
                    else if(peek == '.')
                        state = 2;
                    else if(peek == 'E' || peek == 'e')
                        state = 4;
                    else {
                        return new Num(v);
                    }
                    break;
                case 2:
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 3;
                    else {
                        errors.add(new ErrorInfo(line, "Wrong float format"));
                        return null;
                    }
                    break;
                case 3:
                    x = x + Character.digit(peek, 10) / d;
                    d = d * 10;
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 3;
                    else if(peek == 'E' || peek == 'e')
                        state = 4;
                    else
                        return new Real(v + x);
                    break;
                case 4:
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 6;
                    else if(peek == '+')
                        state = 5;
                    else if(peek == '-')
                        PorN = -1;
                    else {
                        errors.add(new ErrorInfo(line, "Wrong scientific notation"));
                        return null;
                    }
                    break;
                case 5:
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 6;
                    else {
                        errors.add(new ErrorInfo(line, "Wrong scientific notation"));
                        return null;
                    }
                    break;
                case 6:
                    mul = mul * 10 + Character.digit(peek, 10);
                    readch(reader);
                    if(Character.isDigit(peek))
                        state = 6;
                    else {
                        if(PorN == 1)
                            return new Real((float)((v+x) * Math.pow(10, mul)));
                        else if(PorN == -1)
                            return new Real((float)((v+x) / Math.pow(10, mul)));
                    }
                    break;
                        
                //接下来识别二进制八进制十六进制
                case 10:
                    readch(reader);
                    if(Character.isDigit(peek) && peek < '8')
                        state = 11;
                    else if(peek == 'X' || peek == 'x')
                        state = 12;
                    else if(peek == 'B' || peek == 'b')
                        state = 13;
                    break;
                case 11:
                    v = v * 8 + Character.digit(peek, 8);
                    readch(reader);
                    if(Character.isDigit(peek) && peek < '8')
                        state = 11;
                    else 
                        return new Num(v);
                    break;
                    
                case 12:
                    readch(reader);
                    if(Character.isDigit(peek) ||
                            ('a' <= peek && peek <= 'f') || ('A' <= peek && peek <= 'F'))
                        state = 15;
                    else {
                        errors.add(new ErrorInfo(line, "Wrong hexadecimal format"));
                        return null;
                    }
                    break;
                case 13:
                    readch(reader);
                    if(Character.isDigit(peek) && peek < '2')
                        state = 16;
                    else {
                        errors.add(new ErrorInfo(line, "Wrong binary format"));
                        return null;
                    }
                    break;
                    
                case 15:
                    v = v * 16 + Character.digit(peek, 16);
                    readch(reader);
                    if(Character.isDigit(peek) ||
                            ('a' <= peek && peek <= 'f') || ('A' <= peek && peek <= 'F'))
                        state = 15;
                    else 
                        return new Num(v);
                    break;
                case 16:
                    v = v * 2 + Character.digit(peek, 2);
                    readch(reader);
                    if(Character.isDigit(peek) && peek < '2')
                        state = 16;
                    else 
                        return new Num(v);
                    //the biggest problem is forget to add break in switch!!!
                    //事实上时我早就改了，结果几个撤销之后我忘记我撤销了改这个bug的动作。。。太扯了吧
                    break;
                }
            }
        }
        
        
        if(Character.isLetter(peek) || peek == '_') {
            StringBuffer buffer = new StringBuffer();
            do {
                buffer.append(peek);
                readch(reader);
            } while(Character.isLetterOrDigit(peek));
            String s = buffer.toString();
            Word w = (Word)words.get(s);
            if(w != null)
                return w;
            w = new Word(s, Tag.ID);
            w.isID = 1;
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
