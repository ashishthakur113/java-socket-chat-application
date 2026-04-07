import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Client extends  JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // Declare Components for Swing
    private JLabel heading = new JLabel("Client");
    private JTextArea msgArea = new JTextArea();
    private JTextField msgInput = new JTextField();
    private  Font font = new Font("Roboto",Font.PLAIN , 20);


    //Constructor
    public Client() {

        try {
            System.out.println("Sending request to Server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
            //startWriting();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void handleEvents() {
        msgInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                     // System.out.println("Key released "+e.getKeyCode());
                if(e.getKeyCode()==10){
                    //System.out.println("You have pressed enter button");
                    String contentToSend = msgInput.getText();
                    msgArea.append("Me : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    msgInput.setText("");
                    msgInput.requestFocus();
                }
            }
        });
    }

    private void createGUI(){

        this.setTitle("Client Messenger");
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

        // ===== BUTTON ACTION (same as Enter key) =====
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

    //start Reading
    public void startReading() {

        Runnable r1 = () -> {
            System.out.println("Reader Started...");

            try {
                while (!socket.isClosed()) {

                    String msg = br.readLine();

                    if (msg == null) {
                        break;
                    }

                    if (msg.equals("exit")) {
                        System.out.println("Server Terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server Terminated the Chat ");
                        msgInput.setEnabled(false);
                        socket.close();
                        break;
                    }
//                    System.out.println("Server : " + msg);
                    msgArea.append("Server : "+msg+"\n");
                }


            } catch (Exception e) {
                System.out.println("Connection Closed");
            }
        };
        new Thread(r1).start();
    }

    // Start Writing
    public void startWriting() {

        Runnable r2 = () -> {
            System.out.println("Writer Started...");

            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                while (!socket.isClosed()) {

                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        new Client();
    }
}