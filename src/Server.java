import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // GUI Components
    private JLabel heading = new JLabel("Server");
    private JTextArea msgArea = new JTextArea();
    private JTextField msgInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Server() {

        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready...");
            System.out.println("Waiting for client...");

            socket = server.accept();
            System.out.println("Client Connected");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // GUI Setup
    private void createGUI() {

        this.setTitle("Server Messenger");
        this.setSize(600, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== HEADER =====
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        heading.setOpaque(true);
        heading.setBackground(new Color(0, 123, 255)); // blue
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // ===== MESSAGE AREA =====
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setBackground(new Color(245, 245, 245));
        msgArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(msgArea);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // ===== INPUT AREA =====
        msgInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        msgInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(40, 167, 69)); // green
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        // Panel for input + button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(msgInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // ===== MAIN LAYOUT =====
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);

        // ===== BUTTON ACTION =====
        sendButton.addActionListener(e -> {
            String content = msgInput.getText();

            if (!content.trim().isEmpty()) {
                msgArea.append("Me : " + content + "\n");
                msgArea.setCaretPosition(msgArea.getDocument().getLength());

                out.println(content);
                out.flush();

                msgInput.setText("");
                msgInput.requestFocus();
            }
        });
    }

    // Handle Enter Key
    private void handleEvents() {

        msgInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == 10) {

                    String content = msgInput.getText();

                    msgArea.append("Me : " + content + "\n");
                    msgArea.setCaretPosition(msgArea.getDocument().getLength());

                    out.println(content);
                    out.flush();

                    msgInput.setText("");
                    msgInput.requestFocus();

                    if (content.equals("exit")) {
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        msgInput.setEnabled(false);
                    }
                }
            }

            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
        });
    }

    // Read Messages
    public void startReading() {

        Runnable r1 = () -> {
            System.out.println("Reader Started...");

            try {
                while (!socket.isClosed()) {

                    String msg = br.readLine();

                    if (msg == null) break;

                    if (msg.equals("exit")) {
                        JOptionPane.showMessageDialog(this, "Client Terminated Chat");
                        socket.close();
                        msgInput.setEnabled(false);
                        break;
                    }

                    msgArea.append("Client : " + msg + "\n");
                    msgArea.setCaretPosition(msgArea.getDocument().getLength());
                }

            } catch (Exception e) {
                System.out.println("Connection Closed");
            }
        };

        new Thread(r1).start();
    }

    public static void main(String[] args) {
        new Server();
    }
}