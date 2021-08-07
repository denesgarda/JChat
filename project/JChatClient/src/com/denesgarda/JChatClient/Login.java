package com.denesgarda.JChatClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public class Login extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton connectButton;
    private JTextField nicknameTextField;

    public Login() {
        super("JChatClient Login");
        this.setSize(270, 128);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);
        this.setVisible(true);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cont(textField1.getText(), nicknameTextField.getText());
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cont(textField1.getText(), nicknameTextField.getText());
                }
            }
        });
        nicknameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cont(textField1.getText(), nicknameTextField.getText());
                }
            }
        });
    }

    private void cont(String s, String username) {
        try {
            String[] address = s.split(":");
            Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("01100011 01101111 01101110 01101110 01100101 01100011 01110100");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response1 = bufferedReader.readLine();
            if(response1.equals("0")) {
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                String response2 = bufferedReader.readLine();
                if(response2.equals("0")) {
                    JOptionPane.showMessageDialog(null, "Username is taken");
                }
                else if(response2.equals("1")) {
                    this.setVisible(false);
                    new Window(socket);
                    Server server = new Server(socket);
                    Thread thread = new Thread(server);
                    thread.start();
                }
                else if(response2.equals("2")) {
                    JOptionPane.showMessageDialog(null, "User name is not allowed");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Received an unknown response from the server");
                }
            }
            else if(response1.equals("1")) {
                JOptionPane.showMessageDialog(null, "Connection refused: Connection throttle");
            }
            else {
                JOptionPane.showMessageDialog(null, "Received an unknown response from the server");
            }
        }
        catch(SocketException e) {
            JOptionPane.showMessageDialog(null, "Socket error: Either you are not connected to the internet, or the server is not running.");
        }
        catch(ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Unknown format; please use the format <ip>:<port>\nex. 11.222.333.444:9000");
        }
        catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred");
        }
    }
}
