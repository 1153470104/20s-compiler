package lexer;


public class Real extends Token {
    public final float value;
    public Real(float v) {
        super(Tag.REAL);
        value = v;
    }

    @Override
    public String element() {
        return "digit";
    }

    public String toString() {
        return "FLOAT, " + value;
    }
}
