import javax.swing.*;

public class CompilerGUI {
    private JButton translate;
    private JPanel p1;
    private JTextField t1;
    private JTextArea textArea1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Compiler");          //new JFrame
        frame.setSize(400, 200);
        frame.setContentPane(new CompilerGUI().p1);             //set content pane
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);                                 //set visible
    }
}
