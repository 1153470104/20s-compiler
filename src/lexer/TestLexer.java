package lexer;


public class TestLexer {

    public static void test1() {
        Lexer l1 = new Lexer("./src/lexer/test/1.txt");
        l1.tokenPrint();
    }
    
    public static void test2() {
        Lexer l1 = new Lexer("./src/lexer/test/2.txt");
        l1.tokenPrint();
    }

    public static void test3() {
        Lexer l1 = new Lexer("./src/lexer/test/3.txt");
        l1.tokenPrint();
    }
    public static void main(String[] args) {
        //System.out.println("hexadecimal test: " + Character.digit('F', 16));
        //test1();
        //test2();
        test3();
    }
}
