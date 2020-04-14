package lexer;


public class ConstString extends Token {
    public final String value;
    public ConstString(String v) {
        super(Tag.STRING);
        value = v;
    }
    
    public String toString() {
        return "STRING, " + value;
    }
}
