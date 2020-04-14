import javax.swing.*;

public class CompilerGUI {
    private JButton translate;
    private JPanel p1;
    private JTextField t1;
    private JTextField t2;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CompilerGUI");
        frame.setContentPane(new CompilerGUI().p1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
