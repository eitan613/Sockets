import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Socket server;
    private ServerSocket serverSocket;
    private Client client;

    private DataInputStream in ;
    private DataOutputStream out;

    private JFrame frame = new JFrame("Server");
    private JTextArea display = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(display);
    private JTextField userInput = new JTextField();
    private DefaultCaret caret;


    public Server(){
        try {
            serverSocket = new ServerSocket(91);
            frame.setSize(500,500);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            display.setEditable(false);
            scrollPane.setPreferredSize(new Dimension(500,300));
            caret = (DefaultCaret)display.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            frame.add(scrollPane,BorderLayout.NORTH);
            frame.add(userInput,BorderLayout.SOUTH);
            userInput.addActionListener(e -> {
                display.append("You: " + userInput.getText()+"\n");
                sendData();
            });
            frame.setVisible(true);
        } catch (IOException e) {e.printStackTrace();}
    }
    public void accept(Client client){
        try{
            this.client = client;
            server = serverSocket.accept();
            display.append("We just connected to " + server.getRemoteSocketAddress()+"\n");
        } catch (Exception e){e.printStackTrace();}
    }

    public void getData(){
        try {
            in = new DataInputStream(server.getInputStream());
            display.append("Client says: " + in.readUTF()+"\n");
        } catch (IOException e) {e.printStackTrace();}
    }

    public void sendData(){
        try {
            out = new DataOutputStream(server.getOutputStream());
            out.writeUTF(userInput.getText());
            userInput.setText(null);
            client.getData();
        } catch (IOException e) {e.printStackTrace();}
    }

    public boolean isActive() {
        return frame.isVisible();
    }

    public void close() {
        try {
            server.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}