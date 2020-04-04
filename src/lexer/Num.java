package lexer;


public class Num extends Token {
    public final int value;
    public Num(int v) {
        super(Tag.NUM);
        value = v;
    }
    
    //the wierdest bug i've ever meet before, i misspell toString ...
    public String toString() {
        return "int, " + value;
    }
}
