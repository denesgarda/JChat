package com.denesgarda.JChatServer;

import com.denesgarda.JChatServer.enums.ServerType;
import com.denesgarda.JChatServer.log.Logger;
import com.denesgarda.Prop4j.data.PropertiesFile;

import javax.swing.*;
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
                    if (incoming.startsWith("Version: ")) {
                        double clientVersion = Double.parseDouble(incoming.substring(9));
                        if(Main.version != clientVersion) {
                            this.send("2");
                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect using an incompatible version");
                            Main.requested.remove(this);
                        }
                        else {
                            if (Main.serverType == ServerType.WITH_NICKNAMES) {
                                this.send("0");
                            }
                            else if (Main.serverType == ServerType.WITH_ACCOUNTS) {
                                this.send("1");
                            }
                        }
                    }
                    else {
                        if(Main.serverType == ServerType.WITH_NICKNAMES) {
                            if (Main.connected.size() >= Integer.parseInt(Main.config.getProperty("max-connections"))) {
                                Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect but got rejected due to connection throttle");
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                bufferedWriter.write("1");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                Main.requested.remove(this);
                            } else if (incoming.equalsIgnoreCase("INFO") || incoming.equalsIgnoreCase("WARN") || incoming.equalsIgnoreCase("ERROR") || incoming.equalsIgnoreCase("NOTE") || incoming.equalsIgnoreCase("SERVER") || incoming.isBlank() || incoming.contains(" ")) {
                                Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect using an illegal nickname");
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                bufferedWriter.write("2");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                Main.requested.remove(this);
                                return;
                            } else {
                                for (Client client : Main.connected) {
                                    if (client.username.equals(incoming)) {
                                        Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect using a taken nickname");
                                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                        bufferedWriter.write("3");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                        Main.requested.remove(this);
                                        return;
                                    }
                                }
                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                bufferedWriter.write("0");
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
                        else if(Main.serverType == ServerType.WITH_ACCOUNTS) {
                            String[] args = incoming.split(" ");
                            boolean createAccount = Boolean.parseBoolean(args[0]);
                            if(createAccount) {
                                if(Boolean.parseBoolean(Main.config.getProperty("allow-account-creation"))) {
                                    File acc = new File("accounts/" + args[1] + ".properties");
                                    if(acc.exists()) {
                                        Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to create an account with a taken username");
                                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                        bufferedWriter.write("3");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                        Main.requested.remove(this);
                                    }
                                    else {
                                        boolean successful = acc.createNewFile();
                                        if(!successful) {
                                            Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to create an account but the process failed");
                                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                            bufferedWriter.write("4");
                                            bufferedWriter.newLine();
                                            bufferedWriter.flush();
                                            Main.requested.remove(this);
                                            break;
                                        }
                                        PropertiesFile na = new PropertiesFile(acc.getPath());
                                        na.setProperty("password", args[2]);
                                        na.setProperty("administrator", "false");
                                        na.setProperty("banned", "false");
                                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                        bufferedWriter.write("0");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                        username = args[1];
                                        Main.requested.remove(this);
                                        Main.connected.add(this);
                                        Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " created a new account and connected as " + args[1]);
                                        String message = Main.logger.log("INFO", username + " joined");
                                        for (Client client : Main.connected) {
                                            BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                            cbw.write(message);
                                            cbw.newLine();
                                            cbw.flush();
                                        }
                                    }
                                }
                                else {
                                    Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to create an account");
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bufferedWriter.write("1");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                    Main.requested.remove(this);
                                }
                            }
                            else {
                                File accDir = new File("accounts");
                                if(accDir.listFiles() != null) {
                                    boolean found = false;
                                    for(File file : accDir.listFiles()) {
                                        if(args[1].equals(file.getName().replace(".properties", ""))) {
                                            PropertiesFile propertiesFile = new PropertiesFile(file.getPath());
                                            if(args[2].equals(propertiesFile.getProperty("password"))) {
                                                found = true;
                                                if(Boolean.parseBoolean(propertiesFile.getProperty("banned"))) {
                                                    Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " tried to connect as " + args[1] + " but rejected because they are banned");
                                                    this.send("5");
                                                    Main.requested.remove(this);
                                                }
                                                else {
                                                    for(Client client : Main.connected) {
                                                        if(client.username.equals(args[1])) {
                                                            client.send("Connection reset");
                                                            String message = Main.logger.log("INFO", client.username + "'s connection got reset");
                                                            for(Client client1 : Main.connected) {
                                                                client1.send(message);
                                                            }
                                                            client.socket.close();
                                                            Main.connected.remove(client);
                                                        }
                                                    }
                                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                                    bufferedWriter.write("0");
                                                    bufferedWriter.newLine();
                                                    bufferedWriter.flush();
                                                    username = args[1];
                                                    Main.requested.remove(this);
                                                    Main.connected.add(this);
                                                    Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " connected as " + args[1]);
                                                    String message = Main.logger.log("INFO", username + " joined");
                                                    for (Client client : Main.connected) {
                                                        BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                                        cbw.write(message);
                                                        cbw.newLine();
                                                        cbw.flush();
                                                    }
                                                }
                                            }
                                            else {
                                                Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " connected using invalid credentials");
                                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                                bufferedWriter.write("2");
                                                bufferedWriter.newLine();
                                                bufferedWriter.flush();
                                                Main.requested.remove(this);
                                                return;
                                            }
                                        }
                                    }
                                    if(!found) {
                                        Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " connected using invalid credentials");
                                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                        bufferedWriter.write("2");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                        Main.requested.remove(this);
                                    }
                                }
                                else {
                                    Main.logger.log("INFO", socket.getInetAddress().toString().replace("/", "") + " connected using invalid credentials");
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bufferedWriter.write("2");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                    Main.requested.remove(this);
                                }
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
        if(client.username != null) {
            String message = Main.logger.log("INFO", client.username + " left");
            for (Client c : Main.connected) {
                try {
                    BufferedWriter cbw = new BufferedWriter(new OutputStreamWriter(c.socket.getOutputStream()));
                    cbw.write(message);
                    cbw.newLine();
                    cbw.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
        if(Main.serverType == ServerType.WITH_NICKNAMES) {
            if (command.equalsIgnoreCase("help")) {
                if (args.length == 0) {
                    this.send("HELP MENU<nl>==================<nl>/list - List the people online<nl>/leave - Leave the server<nl>/quit - Quit the application<nl>/exit - Quit the application");
                } else {
                    this.send("Invalid arguments");
                }
            }
            else if (command.equalsIgnoreCase("list")) {
                if (args.length == 0) {
                    LinkedList<String> names = new LinkedList<>();
                    for (Client client : Main.connected) {
                        names.add(client.username);
                    }
                    this.send("List of people online: " + Arrays.toString(names.toArray()));
                } else {
                    this.send("Invalid arguments");
                }
            } else {
                this.send("Unknown command; Do \"/help\" for help");
            }
        }
        else if(Main.serverType == ServerType.WITH_ACCOUNTS) {
            boolean administrator = false;
            File accDir = new File("accounts");
            for(File file : accDir.listFiles()) {
                if(file.getName().replace(".properties", "").equals(this.username)) {
                    PropertiesFile acc = new PropertiesFile(file.getPath());
                    if(Boolean.parseBoolean(acc.getProperty("administrator"))) {
                        administrator = true;
                    }
                }
            }
            if(administrator) {
                if (command.equalsIgnoreCase("help")) {
                    if (args.length == 0) {
                        this.send("HELP MENU<nl>==================<nl>/list - List the people online<nl>/kick <person> - Kick someone<nl>/ban <person> - Ban someone<nl>/pardon <person> - Unban someone<nl>/banlist - See everyone who is banned<nl>/leave - Leave the server<nl>/quit - Quit the application<nl>/exit - Quit the application");
                    } else {
                        this.send("Invalid arguments");
                    }
                } else if (command.equalsIgnoreCase("list")) {
                    if (args.length == 0) {
                        LinkedList<String> names = new LinkedList<>();
                        for (Client client : Main.connected) {
                            names.add(client.username);
                        }
                        this.send("List of people online: " + Arrays.toString(names.toArray()));
                    } else {
                        this.send("Invalid arguments");
                    }
                }
                else if (command.equalsIgnoreCase("kick")) {
                    if (args.length == 1) {
                        String username = args[0];
                        for (Client client : Main.connected) {
                            if (client.username.equals(username)) {
                                try {
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                    bufferedWriter.write("You have been kicked from the server");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                    client.socket.close();
                                    Main.connected.remove(client);
                                    String message = Main.logger.log("INFO", username + " has been kicked from the server");
                                    for (Client newClient : Main.connected) {
                                        BufferedWriter newBufferedWriter = new BufferedWriter(new OutputStreamWriter(newClient.socket.getOutputStream()));
                                        newBufferedWriter.write(message);
                                        newBufferedWriter.newLine();
                                        newBufferedWriter.flush();
                                    }
                                    return;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        this.send("Could not find a user with the username " + username);
                    } else {
                        this.send("Invalid arguments");
                    }
                }
                else if(command.equalsIgnoreCase("ban")) {
                    if(args.length == 1) {
                        accDir = new File("accounts");
                        if(accDir.listFiles() != null) {
                            for(File file : accDir.listFiles()) {
                                if(file.getName().replace(".properties", "").equals(args[0])) {
                                    PropertiesFile acc = new PropertiesFile(file.getPath());
                                    acc.setProperty("banned", "true");
                                    String username = args[0];
                                    for (Client client : Main.connected) {
                                        if (client.username.equals(username)) {
                                            try {
                                                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                                bufferedWriter.write("You have been banned from the server");
                                                bufferedWriter.newLine();
                                                bufferedWriter.flush();
                                                client.socket.close();
                                                Main.connected.remove(client);
                                                String message = Main.logger.log("INFO", username + " has been banned from the server");
                                                for (Client newClient : Main.connected) {
                                                    BufferedWriter newBufferedWriter = new BufferedWriter(new OutputStreamWriter(newClient.socket.getOutputStream()));
                                                    newBufferedWriter.write(message);
                                                    newBufferedWriter.newLine();
                                                    newBufferedWriter.flush();
                                                }
                                                return;
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                            this.send("Could not find a user with the username " + args[0]);
                        }
                    }
                    else {
                        this.send("Invalid arguments");
                    }
                }
                else if(command.equalsIgnoreCase("pardon")) {
                    if(args.length == 1) {
                        accDir = new File("accounts");
                        if(accDir.listFiles() != null) {
                            for(File file : accDir.listFiles()) {
                                if(file.getName().replace(".properties", "").equals(args[0])) {
                                    PropertiesFile acc = new PropertiesFile(file.getPath());
                                    acc.setProperty("banned", "false");
                                    String message = Main.logger.log("INFO", args[0] + " has been unbanned from the server");
                                    for(Client client : Main.connected) {
                                        client.send(message);
                                    }
                                    return;
                                }
                            }
                            this.send("Could not find a user with the username " + args[0]);
                        }
                    }
                    else {
                        this.send("Invalid arguments");
                    }
                }
                else if(command.equalsIgnoreCase("banlist")) {
                    if(args.length == 0) {
                        LinkedList<String> names = new LinkedList<>();
                        accDir = new File("accounts");
                        if(accDir.listFiles() != null) {
                            for (File file : accDir.listFiles()) {
                                PropertiesFile acc = new PropertiesFile(file.getPath());
                                if(Boolean.parseBoolean(acc.getProperty("banned"))) {
                                    names.add(file.getName().replace(".properties", ""));
                                }
                            }
                        }
                        this.send("Banned accounts are: " + Arrays.toString(names.toArray()));
                    }
                    else {
                        this.send("Invalid arguments");
                    }
                }
                else {
                    this.send("Unknown command; Do \"/help\" for help");
                }
            }
            else {
                if (command.equalsIgnoreCase("help")) {
                    if (args.length == 0) {
                        this.send("HELP MENU<nl>==================<nl>/list - List the people online<nl>/leave - Leave the server<nl>/quit - Quit the application<nl>/exit - Quit the application");
                    } else {
                        this.send("Invalid arguments");
                    }
                } else if (command.equalsIgnoreCase("list")) {
                    if (args.length == 0) {
                        LinkedList<String> names = new LinkedList<>();
                        for (Client client : Main.connected) {
                            names.add(client.username);
                        }
                        this.send("List of people online: " + Arrays.toString(names.toArray()));
                    } else {
                        this.send("Invalid arguments");
                    }
                } else {
                    this.send("Unknown command; Do \"/help\" for help");
                }
            }
        }
    }
}
