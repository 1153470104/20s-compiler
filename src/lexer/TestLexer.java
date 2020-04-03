package lexer;


public class TestLexer {

    public static void main(String[] args) {
        Lexer l = new Lexer("./test/1.txt");
        System.out.println(l.tokens);
    }
}
