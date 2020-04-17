package lexer;

public class Word extends Token {
    public int isID = 0;
    public String lexeme = "";
    public Word(String s, int tag) {
        super(tag);
        lexeme = s;
    }
    public String toString() {
        switch(tag) {
            case 256:
                return "AND, " + lexeme;
            case 257:
                return "BASIC, " + lexeme;
            case 258:
                return "BREAK, " + lexeme;
            case 259:
                return "DO, " + lexeme;
            case 260:
                return "ELSE, " + lexeme;
            case 261:
                return "EQ, " + lexeme;
            case 262:
                return "FALSE, " + lexeme;
            case 263:
                return "GE, " + lexeme;
            case 264:
                return "ID, " + lexeme;
            case 266:
                return "INDEX, " + lexeme;
            case 267:
                return "LE, " + lexeme;
            case 268:
                return "MINUS, " + lexeme;
            case 269:
                return "NE, " + lexeme;
            case 271:
                return "OR, " + lexeme;

            case 273:
                return "TEMP, " + lexeme;
            case 274:
                return "TRUE, " + lexeme;

            case 275:
                return "WHILE, " + lexeme;
            case 276:
                return "RETURN, " + lexeme;
            case 278:
                return "RECORD, " + lexeme;
            case 279:
                return "LEFTBRACKET, " + lexeme;
            case 280:
                return "RIGHTBRACKET, " + lexeme;
            case 281:
                return "LEFTPAREN, " + lexeme;
            case 282:
                return "RIGHTPAREN, " + lexeme;
            case 283:
                return "LEFTCURLY, " + lexeme;
            case 284:
                return "RIGHTCURLY, " + lexeme;
            case 285:
                return "SEMICOLUMN, " + lexeme;

            case 286:
                return "THEN, " + lexeme;

            case 287:
                return "PLUS, " + lexeme;
            case 288:
                return "SUB, " + lexeme;
            case 289:
                return "MULTI, " + lexeme;
            case 290:
                return "DIVIDE, " + lexeme;
            case 291:
                return "GIVE, " + lexeme;

        }

        return lexeme;
            
    }

    public static final Word
        and = new Word("&&", Tag.AND),
        or = new Word("||", Tag.OR),
        eq = new Word("==", Tag.EQ),
        ne = new Word("!=", Tag.NE),
        le = new Word("<=", Tag.LE),
        ge = new Word(">=", Tag.GE),
        minus = new Word("minus", Tag.MINUS),
        True = new Word("true", Tag.TRUE),
        False = new Word("false", Tag.FALSE),
        temp = new Word("temp", Tag.TEMP),

        leftbracket = new Word("[", Tag.LEFTBRACKET),
        rightbracket = new Word("]", Tag.RIGHTBRACKET),
        leftparen = new Word("(", Tag.LEFTPAREN),
        rightparen = new Word(")", Tag.RIGHTPAREN),
        leftcurly = new Word("{", Tag.LEFTCURLY),
        rightcurly = new Word("}", Tag.RIGHTCURLY),
        semicolumn = new Word(";", Tag.SEMICOLUMN),
        plus = new Word("+", Tag.PLUS),
        sub = new Word("-", Tag.SUB),
        multi = new Word("*", Tag.MULTI),
        divide = new Word("/", Tag.DIVIDE),
        give = new Word("=", Tag.GIVE);

}
