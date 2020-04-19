package lexer;


public class Num extends Token {
    public final int value;
    public Num(int v) {
        super(Tag.NUM);
        value = v;
    }

    @Override
    public String element() {
        return "integer";
    }

    //the wierdest bug i've ever meet before, i misspell toString ...
    public String toString() {
        return "INT, " + value;
    }
}
