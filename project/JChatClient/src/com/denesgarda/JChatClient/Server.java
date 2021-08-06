package com.denesgarda.JChatClient;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Server implements Runnable {
    private Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(!socket.isClosed()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if(in.ready()) {
                    String incoming = in.readLine();
                    System.out.println(incoming);
                    if(incoming.equals("You have been kicked from the server")) {
                        JOptionPane.showMessageDialog(null, "You have been kicked from the server");
                        System.exit(0);
                    }
                    if(incoming.equals("Server closed")) {
                        JOptionPane.showMessageDialog(null, "Server closed");
                        System.exit(0);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
