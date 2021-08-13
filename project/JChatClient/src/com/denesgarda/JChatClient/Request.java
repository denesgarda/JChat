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

public class Request extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton connectButton;
    private JButton cancelButton;

    public Request() {
        super("JChatClient");
        this.setSize(270, 96);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);
        this.setVisible(true);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cont(textField1.getText());
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cont(textField1.getText());
                }
            }
        });
    }

    private void cont(String s) {
        try {
            String[] address = new String[2];
            if(s.contains(":")) {
                address = s.split(":");
            }
            else {
                address[0] = s;
                address[1] = "6577";
            }
            Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("Version: " + Main.version);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response1 = bufferedReader.readLine();
            switch(response1) {
                case "0" -> new Nickname(socket, this);
                case "1" -> new Login(socket, this);
                case "2" -> JOptionPane.showMessageDialog(null, "Incompatible version");
                default -> JOptionPane.showMessageDialog(null, "Unknown response code from server: " + response1);
            }
        }
        catch(SocketException e) {
            JOptionPane.showMessageDialog(null, "Socket error: Either you are not connected to the internet, or the server is not running.");
        }
        catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred");
        }
    }
}
