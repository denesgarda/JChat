package com.denesgarda.JChatClient;

import java.io.*;
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
            BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
            while(!socket.isClosed()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if(in.ready()) {
                    String incoming = in.readLine();
                    System.out.println(incoming);
                }
                if(sysIn.ready()) {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String outgoing = "[" + username + "]: " + sysIn.readLine();
                    out.write(outgoing);
                    out.newLine();
                    out.flush();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
