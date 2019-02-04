import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {

    private Socket client;
    private Server server;

    private JFrame frame = new JFrame("Client");
    private JTextArea display = new JTextArea();
    private JTextField userInput = new JTextField();
    private JScrollPane scrollPane = new JScrollPane(display);
    private DefaultCaret caret;

    private OutputStream outToServer;
    private DataOutputStream out;
    private InputStream inFromServer;
    private DataInputStream in;

    public Client() {
        try {
            server = new Server();
            client = new Socket("127.0.0.1", 91);
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            display.setEditable(false);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            caret = (DefaultCaret) display.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            frame.add(scrollPane, BorderLayout.NORTH);
            frame.add(userInput, BorderLayout.SOUTH);
            userInput.addActionListener(e -> {
                display.append("You: " + userInput.getText() + "\n");
                sendData();
            });
            frame.setVisible(true);
            server.accept(this);
            display.append("We just connected to " + client.getRemoteSocketAddress() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getData() {
        try {
            inFromServer = client.getInputStream();
            in = new DataInputStream(inFromServer);
            display.append("Server says: " + in.readUTF() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendData() {
        try {
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            out.writeUTF(userInput.getText());
            userInput.setText(null);
            server.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        while (true)
            if (!frame.isVisible() || !server.isActive()) {
                try {
                    client.close();
                    in.close();
                    out.close();
                    outToServer.close();
                    inFromServer.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.close();
    }
}
