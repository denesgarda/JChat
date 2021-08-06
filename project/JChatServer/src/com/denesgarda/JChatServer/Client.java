package com.denesgarda.JChatServer;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(!socket.isClosed()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String incoming = bufferedReader.readLine();
                for(Client client : Main.clients) {
                    if(client.socket.isClosed()) {
                        Main.clients.remove(client);
                    }
                    else {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                        bufferedWriter.write(incoming);
                        bufferedWriter.newLine();
                        System.out.println(incoming);
                        bufferedWriter.flush();
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
