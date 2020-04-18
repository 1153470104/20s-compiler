package lexer;

import java.io.*;
import java.util.*;

import symbol.*;

public class Lexer {
    public static int line = 1;
    char peek = ' ';
    String filename;
    
    Hashtable words = new Hashtable<>();
    List<Token> tokens = new LinkedList<>();
    List<ErrorInfo> errors = new LinkedList<Lexer.ErrorInfo>();
    List<Comment> comments = new LinkedList<Lexer.Comment>();

    public class Comment {
        public int line;
        public String commentString;
        public Comment(int l, String c) {
            line = l;
            commentString = c;
        }
    }
    
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
            System.out.println("Error "+ "[" + e.line + "]" + ": " + e.wrongInfo);
        }
    }
    
    public void commentPrint() {
        for(Comment e: comments) {
            System.out.println("Comment "+ "[" + e.line + "]" + ": " + e.commentString);
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

        reserve(new Word("then", Tag.THEN));
        reserve(new Word("record", Tag.RECORD));

        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Char);
        reserve(Type.Bool);
        reserve(Type.Int);
        reserve(Type.Float);

        reserve(Word.leftbracket);
        reserve(Word.leftcurly);
        reserve(Word.leftparen);
        reserve(Word.rightbracket);
        reserve(Word.rightcurly);
        reserve(Word.rightparen);
        reserve(Word.semicolumn);
        reserve(Word.plus);
        reserve(Word.sub);
        reserve(Word.multi);
        reserve(Word.divide);
        reserve(Word.give);
        reserve(Word.comma);

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
        peek = ' ';
        return true;
    }
    

    public Token scan(Reader reader) throws IOException {

        //去掉空格
        while(peek == ' ' || peek == '\t' || peek == '\r' || peek == '\n') {
            if(peek == '\n')
                line = line + 1;
            if(!readch(reader))
                break;
        }
        
        //去注释
        if(peek == '/') {
            StringBuffer buffer = new StringBuffer();
            
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
                        state = 4;
                    else {
                        buffer.append(peek);
                        state = 2;
                    }
                    break;
                case 3:
                    readch(reader);
                    if(peek == '*') {
                        state = 3;
                        buffer.append(peek);
                    }
                    else if(peek == '/') {
                        peek = ' '; 
                        comments.add(new Comment(line, buffer.toString()));
                        return null;
                    } else {
                        state = 2;
                        buffer.append(peek);
                    }
                    if(peek == '"')
                        state = 4;
                        buffer.append(peek);
                    break;
                case 4:
                    readch(reader);
                    if(peek == '"') {
                        state = 2;
                        buffer.append(peek);
                    }
                    break;
                }
            }
        }
        
        //识别字符串型常量
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

        //识别比较运算符
        switch(peek) {
        case '&':
            if(readch(reader, '&'))  return Word.and;
            else  return new Token('&');
        case '|':
            if(readch(reader, '|'))  return Word.or;
            else   return new Token('|');
        case '=':
            if(readch(reader, '='))   return Word.eq;
            else   return Word.give;
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
            
            //识别数字和科学计数法
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
                    else
                        return new Num(v);
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
                        else
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
                    break;
                }
            }
        }
        
        //识别标识符
        if(Character.isLetter(peek) || peek == '_') {
            StringBuffer buffer = new StringBuffer();
            do {
                buffer.append(peek);
                readch(reader);
            } while(Character.isLetterOrDigit(peek));
            String s = buffer.toString();
            Word w = (Word)words.get(s);//其中可以去除保留状态
            if(w != null)
                return w;
            w = new Word(s, Tag.ID);
            w.isID = 1;
            words.put(s, w);
            return w;
        }

        //接下来就是识别 各种括号括号了
        Token returnTok = new Token(peek);
        Word w = (Word)words.get(returnTok.toString());
        if(w != null) {
//            System.out.println(w);
            peek = ' ';
            return w;
        }
        if(judgeEnd == 1) {
            returnTok = null;
        }
//        System.out.println(returnTok);
        errors.add(new ErrorInfo(line, "Illigal Token"));
        peek = ' ';
        return null;
    }
}
