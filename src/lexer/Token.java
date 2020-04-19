package lexer;


public class Token {
    public final int tag;
    public int line = 0;
    public Token(int t) {
        tag = t;
    }

    public String element() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "" + (char)tag;
    }

}
