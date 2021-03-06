package lexer;


public class LexerClient {

    public static void test1() {
        Lexer l1 = new Lexer("./src/lexer/test/1.txt");
        l1.tokenPrint();
    }
    
    public static void test2() {
        Lexer l1 = new Lexer("./src/lexer/test/2.txt");
        l1.tokenPrint();
    }

    public static void test3() {
        Lexer l1 = new Lexer("./src/lexer/test/6.txt");
        l1.tokenPrint();
        System.out.println();
        l1.errorPrint();
        System.out.println();
        l1.commentPrint();
    }

    public static void testElement() {
        Lexer l1 = new Lexer("./src/lexer/test/3.txt");
        for(Token t: l1.tokens) {
            System.out.print(t.line + "      \t");
            System.out.print(t + "      \t");
            System.out.println(t.element());
        }
    }

    public static String tokens(String filename) {
        Lexer l1 = new Lexer(filename);
        return l1.tokenString();
    }

    public static void main(String[] args) {
        //System.out.println("hexadecimal test: " + Character.digit('F', 16));
        //test1();
        //test2();
        test3();
//        testElement();
        
    }
}
