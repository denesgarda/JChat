package com.denesgarda.JChatServer;

import com.denesgarda.JChatServer.log.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client implements Runnable {
    private Socket socket;
    private String username;

    public Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(!socket.isClosed()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String incoming = bufferedReader.readLine();
                if(Main.requested.contains(this)) {
                    if (incoming.equals("01100011 01101111 01101110 01101110 01100101 01100011 01110100")) {
                        if(Main.connected.size() >= Integer.parseInt(Main.config.getProperty("max-connections"))) {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("1");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                        else {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("0");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                    else {
                        if(incoming.equalsIgnoreCase("INFO") || incoming.equalsIgnoreCase("WARN") || incoming.equalsIgnoreCase("ERROR") || incoming.equalsIgnoreCase("NOTE")) {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("2");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            return;
                        }
                        else {
                            for (Client client : Main.connected) {
                                if (client.username.equals(incoming)) {
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bufferedWriter.write("0");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                    return;
                                }
                            }
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("1");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            username = incoming;
                            Main.requested.remove(this);
                            Main.connected.add(this);
                            String message = username + " joined";
                            for (Client client : Main.connected) {
                                BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                cbw.write(message);
                                cbw.newLine();
                                Main.logger.log("INFO", message);
                                cbw.flush();
                            }
                        }
                    }
                }
                else {
                    if(incoming != null) {
                        for (Client client : Main.connected) {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                            bufferedWriter.write("[" + username + "]: " + incoming);
                            bufferedWriter.newLine();
                            Main.logger.log(username, incoming);
                            bufferedWriter.flush();
                        }
                    }
                    else {
                        leave(this);
                    }
                }
            }
            leave(this);
        }
        catch(NullPointerException e) {
            leave(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void leave(Client client) {
        Main.connected.remove(client);
        String message = client.username + " left";
        for (Client c : Main.connected) {
            try {
                BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(c.socket.getOutputStream()));
                cbw.write(message);
                cbw.newLine();
                Main.logger.log("INFO", message);
                cbw.flush();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
