package com.denesgarda.JChatClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class Login extends JFrame {
    private JPanel panel1;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JCheckBox createAccountCheckBox;
    private JButton cancelButton;
    private JButton connectButton;

    private Socket socket;
    private Request request;

    public Login(Socket socket, Request request) {
        super("JChatClient Login");
        this.socket = socket;
        this.request = request;
        this.setSize(230, 160);
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
                    cont(usernameTextField.getText(), passwordTextField.getText(), createAccountCheckBox.isSelected());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        usernameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        cont(usernameTextField.getText(), passwordTextField.getText(), createAccountCheckBox.isSelected());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        passwordTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        cont(usernameTextField.getText(), passwordTextField.getText(), createAccountCheckBox.isSelected());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    private void cont(String username, String password, boolean createAccount) throws IOException {
        if(username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Credentials cannot contain spaces");
            this.setVisible(false);
        }
        else {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(createAccount + " " + username + " " + password);
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
                    JOptionPane.showMessageDialog(null, "Account creation is not allowed on this server");
                    this.setVisible(false);
                }
                case "2" -> {
                    JOptionPane.showMessageDialog(null, "Invalid credentials");
                    this.setVisible(false);
                }
                case "3" -> {
                    JOptionPane.showMessageDialog(null, "Username is taken");
                    this.setVisible(false);
                }
                case "4" -> {
                    JOptionPane.showMessageDialog(null, "Account creation failed for unknown reason");
                    this.setVisible(false);
                }
                case "5" -> {
                    JOptionPane.showMessageDialog(null, "You are banned from this server");
                    this.setVisible(false);
                }
                default -> {
                    JOptionPane.showMessageDialog(null, "Unknown response code from server: " + response1);
                    this.setVisible(false);
                }
            }
        }
    }
}
