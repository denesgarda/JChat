package com.denesgarda.JChatClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server implements Runnable {
    private Socket socket;
    private String username;

    public Server(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
            while(!socket.isClosed()) {
                if(in.ready()) {
                    String incoming = in.readLine();
                    System.out.println(incoming);
                }
                if(sysIn.ready()) {
                    String input = sysIn.readLine();
                    out.write("[" + username + "]: " + input);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
