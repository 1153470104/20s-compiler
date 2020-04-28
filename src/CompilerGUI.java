import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lexer.*;

public class CompilerGUI {
    private JButton translate;
    private JPanel p1;
    private JTextArea t1;
    private JTextArea t2;

    public CompilerGUI() {
        translate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    t1.setText(fileString("./src/lexer/test/2.txt"));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                t2.setText(LexerClient.tokens("./src/lexer/test/2.txt"));
            }
        });
    }

    public static String fileString(String filename) throws FileNotFoundException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer output = new StringBuffer();
        try{
            String stringLine;
            while (null != (stringLine = br.readLine())) {
                output.append(stringLine).append("\n");
            }

        } catch(Exception e) {
            System.out.println("IOException!");
        }
        return output.toString();

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Compiler");          //new JFrame
        frame.setSize(400, 200);
        frame.setContentPane(new CompilerGUI().p1);             //set content pane
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);                                 //set visible
    }
}
