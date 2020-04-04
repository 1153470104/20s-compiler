package lexer;


public class TestLexer {

    public static void main(String[] args) {
        Lexer l = new Lexer("./src/lexer/test/1.txt");
        //System.out.println("get here!");
        //System.out.println(l.tokens);
        l.tokenPrint();
    }
}
