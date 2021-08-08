package com.denesgarda.JChatClient;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Server implements Runnable {
    private Socket socket;
    private JFrame frame;

    public Server(Socket socket, JFrame frame) {
        this.socket = socket;
        this.frame = frame;
    }

    @Override
    public void run() {
        try {
            while(!socket.isClosed()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if(in.ready()) {
                    String incoming = in.readLine();
                    incoming = incoming.replace("<nl>", "\n");
                    if(incoming.equals("You have been kicked from the server")) {
                        System.out.println(incoming);
                        JOptionPane.showMessageDialog(null, "You have been kicked from the server");
                        frame.setVisible(false);
                        new Request();
                    }
                    else if(incoming.equals("You have been banned from the server")) {
                        System.out.println(incoming);
                        JOptionPane.showMessageDialog(null, "You have been banned from the server");
                        frame.setVisible(false);
                        new Request();
                    }
                    else if(incoming.equals("Connection reset")) {
                        System.out.println(incoming);
                        JOptionPane.showMessageDialog(null, "Connection reset");
                        frame.setVisible(false);
                        new Request();
                    }
                    else if(incoming.equals("Server closed")) {
                        System.out.println(incoming);
                        JOptionPane.showMessageDialog(null, "Server closed");
                        frame.setVisible(false);
                        new Request();
                    }
                    else if(incoming.startsWith("Name: ")) {
                        frame.setName("JChatClient - " + incoming.substring(7));
                    }
                    else {
                        System.out.println(incoming);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
