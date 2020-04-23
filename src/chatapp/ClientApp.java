package chatapp;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Mochammad Alie
 */
public class ClientApp {
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private JFrame jFrame = new JFrame("Kabar-Kabur");
    private JTextField jTextField = new JTextField(40);
    private JTextArea jTextArea = new JTextArea(8, 40);
    
    public ClientApp() {
        jTextField.setEditable(false);
        jTextArea.setEditable(false);
        jFrame.setSize(500, 500);
        jFrame.getContentPane().add(jTextField, "North");
        jFrame.getContentPane().add(new JScrollPane(jTextArea), "Center");
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setLocation((dimension.width / 2) - (jFrame.getSize().width / 2),
                (dimension.height / 2) - (jFrame.getSize().height / 2));
        
        jTextField.addActionListener(((ae) -> {
            printWriter.println(jTextField.getText());
            jTextField.setText("");
        }));
    }
    
    public String getServerAddress()  {
      return JOptionPane.showInputDialog(
              jFrame,
              "Enter the IP Address",
              "Welcome to Kabar-Kabur",
              JOptionPane.QUESTION_MESSAGE
      );
    }
    
    public String getName() {
        return JOptionPane.showInputDialog(
              jFrame,
              "Enter Your Name",
              "Welcome to Kabar-Kabur",
              JOptionPane.QUESTION_MESSAGE
        );
    }
    
    private void run() throws IOException {
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 1813);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        
        while (true) {            
            String line = bufferedReader.readLine();
            
            if (line.startsWith("Submit Name")) {
                printWriter.println(getName());
            } else if (line.startsWith("Name Accepted")) {
                jTextField.setEditable(true);
            } else if (line.startsWith("Message")) {
                jTextArea.append(line.substring(8) + "\n");
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        ClientApp client = new ClientApp();
        client.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.jFrame.setVisible(true);
        client.run();
                
    }
}
