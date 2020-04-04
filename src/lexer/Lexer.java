package lexer;

import java.io.*;
import java.util.*;

import symbol.*;

public class Lexer {
    public static int line = 1;
    //�ʼ�ĵط����ǻ᷵��һ����token������ԭ������Ϊ������peek û��ֵ��
    //����ϵͳ�����κ�������ֱ�ӵ���һ��token�ˡ������Ի���Ҫ��Ϊ ' ',�Ŷ�
    char peek = ' ';
    Hashtable words = new Hashtable<>();
    List<Token> tokens = new LinkedList<>();
    String filename;
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
            tokens.remove(tok);
            reader.close();
            
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
        //������ԣ�����' '������ʹ��peek�ȶ�һλ���߼�û������ʧЧ
        peek = ' ';
        return true;
    }
    
    //main function: scan
    public Token scan(Reader reader) throws IOException {
        //��������������scan��ֹ���������������forѭ�������ļ�βһֱѭ����
        //�����֮ǰ�� (; ; readch()) �Ļ�
        //but new problem occur
        //���ڵ�״̬�ǻ�����read()ȡһ��,�͵�����û�пո�Ķ����޷�ʶ��
        while(peek == ' ' || peek == '\t' || peek == '\r' || peek == '\n') {
            if(peek == '\n')
                line = line + 1;
            if(!readch(reader))
                break;
        }
        
        //ȥע��
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
