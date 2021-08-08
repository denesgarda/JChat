package com.denesgarda.JChatClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class Nickname extends JFrame {
    private JTextField nicknameTextField;
    private JPanel panel1;
    private JButton cancelButton;
    private JButton connectButton;

    private Socket socket;
    private Request request;

    public Nickname(Socket socket, Request request) {
        super("JChatClient Nickname");
        this.socket = socket;
        this.request = request;
        this.setSize(230, 96);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setContentPane(panel1);
        this.setVisible(true);
        JFrame frame = this;
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cont(nicknameTextField.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        nicknameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        cont(nicknameTextField.getText());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    private void cont(String nickname) throws IOException {
        if(nickname.startsWith("Version: ")) {
            JOptionPane.showMessageDialog(null, "Illegal character sequence");
            this.setVisible(false);
            return;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(nickname);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response1 = bufferedReader.readLine();
        switch(response1) {
            case "0" -> {
                this.setVisible(false);
                request.setVisible(false);
                Window window = new Window(socket);
                Server server = new Server(socket, window);
                Thread thread = new Thread(server);
                thread.start();
            }
            case "1" -> {
                JOptionPane.showMessageDialog(null, "Connection refused: Connection throttle");
                this.setVisible(false);
            }
            case "2" -> {
                JOptionPane.showMessageDialog(null, "Connection refused: Illegal nickname");
                this.setVisible(false);
            }
            case "3" -> {
                JOptionPane.showMessageDialog(null, "Connection refused: Nickname taken");
                this.setVisible(false);
            }
            default -> {
                JOptionPane.showMessageDialog(null, "Unknown response code from server: " + response1);
                this.setVisible(false);
            }
        }
    }
}
