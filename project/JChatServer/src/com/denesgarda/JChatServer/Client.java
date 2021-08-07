package com.denesgarda.JChatServer;

import com.denesgarda.JChatServer.log.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Client implements Runnable {
    public Socket socket;
    public String username;

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
                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect but got rejected due to connection throttle");
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("1");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            Main.requested.remove(this);
                        }
                        else {
                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " requested to connect; querying nickname");
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("0");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                    else {
                        if(incoming.equalsIgnoreCase("INFO") || incoming.equalsIgnoreCase("WARN") || incoming.equalsIgnoreCase("ERROR") || incoming.equalsIgnoreCase("NOTE") || incoming.equalsIgnoreCase("SERVER") || incoming.isBlank() || incoming.contains(" ")) {
                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to use an illegal nickname");
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("2");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            Main.requested.remove(this);
                            return;
                        }
                        else {
                            for (Client client : Main.connected) {
                                if (client.username.equals(incoming)) {
                                    Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to use an taken nickname");
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
                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " joined with the username " + username);
                            String message = Main.logger.log("INFO", username + " joined");
                            for (Client client : Main.connected) {
                                BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                cbw.write(message);
                                cbw.newLine();
                                cbw.flush();
                            }
                        }
                    }
                }
                else {
                    if(incoming != null) {
                        if(incoming.contains("<nl>")) {
                            Main.logger.log("INFO", username + " tried to send the message: " + incoming);
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bufferedWriter.write("Message could not send; Illegal character sequence");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                        else if(incoming.charAt(0) == '/') {
                            String[] split = incoming.substring(1).split(" ");
                            String command = split[0];
                            String[] args = Arrays.copyOf(split, split.length - 1);
                            for(int i = 0; i < split.length - 1; i++) {
                                args[i] = split[i + 1];
                            }
                            command(command, args);
                        }
                        else {
                            String message = Main.logger.log(username, incoming);
                            for (Client client : Main.connected) {
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                bufferedWriter.write(message);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                        }
                    }
                    else {
                        leave(this);
                        return;
                    }
                }
            }
            leave(this);
        }
        catch(NullPointerException e) {
            leave(this);
        }
        catch(SocketException ignored) {}
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void leave(Client client) {
        Main.connected.remove(client);
        String message = Main.logger.log("INFO", client.username + " left");
        for (Client c : Main.connected) {
            try {
                BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(c.socket.getOutputStream()));
                cbw.write(message);
                cbw.newLine();
                cbw.flush();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void send(String content) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        bufferedWriter.write(content);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void command(String command, String[] args) throws IOException {
        Main.logger.log("INFO", username + " executed /"  + command + " with the arguments " + Arrays.toString(args));
        if(command.equalsIgnoreCase("help")) {
            if(args.length == 0) {
                this.send("HELP MENU<nl>==================<nl>/list - List the people online<nl>/leave - Leave the server<nl>/quit - Quit the application<nl>/exit - Quit the application");
            }
            else {
                this.send("Invalid arguments");
            }
        }
        else if(command.equalsIgnoreCase("list")) {
            if(args.length == 0) {
                LinkedList<String> names = new LinkedList<>();
                for(Client client : Main.connected) {
                    names.add(client.username);
                }
                this.send("List of people online: " + Arrays.toString(names.toArray()));
            }
            else {
                this.send("Invalid arguments");
            }
        }
        else {
            this.send("Unknown command; Do \"/help\" for help");
        }
    }
}
