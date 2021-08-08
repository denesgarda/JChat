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
            String[] address = s.split(":");
            Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("01100011 01101111 01101110 01101110 01100101 01100011 01110100");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response1 = bufferedReader.readLine();
            switch(response1) {
                case "0" -> new Nickname(socket, this);
                case "1" -> new Login(socket, this);
                default -> JOptionPane.showMessageDialog(null, "Unknown response code from server: " + response1);
            }
            /*if(response1.equals("0")) {
                bufferedWriter.write("?");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                String response2 = bufferedReader.readLine();
                switch (response2) {
                    case "0" -> JOptionPane.showMessageDialog(null, "Username is taken");
                    case "1" -> {
                        this.setVisible(false);
                        Window window = new Window(socket);
                        Server server = new Server(socket, window);
                        Thread thread = new Thread(server);
                        thread.start();
                    }
                    case "2" -> JOptionPane.showMessageDialog(null, "User name is not allowed");
                    default -> JOptionPane.showMessageDialog(null, "Received an unknown response from the server");
                }
            }
            else if(response1.equals("1")) {
                JOptionPane.showMessageDialog(null, "Connection refused: Connection throttle");
            }
            else {
                JOptionPane.showMessageDialog(null, "Received an unknown response from the server");
            }*/
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
