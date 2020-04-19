package lexer;


public class Token {
    public final int tag;
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
