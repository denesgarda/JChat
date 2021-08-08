package com.denesgarda.JChatServer;

import com.denesgarda.JChatServer.enums.ServerType;
import com.denesgarda.Prop4j.data.PropertiesFile;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

public class Window extends JFrame {
    private JPanel panel;
    private JTextField textField1;
    private JTextArea textArea1;
    private JScrollPane scrollPane1;

    public Window() {
        super("JChatServer");
        textArea1.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        TextAreaOutputStream out = new TextAreaOutputStream(textArea1, "");
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        System.setErr(ps);
        this.setSize(1024, 512);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.setVisible(true);
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(!textField1.getText().isBlank()) {
                        String input = textField1.getText();
                        if(input.charAt(0) == '/') {
                            String[] split = input.substring(1).split(" ");
                            String command = split[0];
                            String[] args = Arrays.copyOf(split, split.length - 1);
                            System.arraycopy(split, 1, args, 0, split.length - 1);
                            try {
                                command(command, args);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        else {
                            String message = Main.logger.log("SERVER", input);
                            for (Client client : Main.connected) {
                                try {
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                    bufferedWriter.write(message);
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    textField1.setText("");
                }
            }
        });
    }

    private void command(String command, String[] args) throws IOException {
        Main.logger.log("INFO", "Executed /"  + command + " with the arguments " + Arrays.toString(args));
        if(Main.serverType == ServerType.WITH_NICKNAMES) {
            if (command.equalsIgnoreCase("help")) {
                if (args.length == 0) {
                    System.out.println("""
                            HELP MENU
                            =====================
                            /list - List the people online
                            /stop - Stop the server
                            /kick <person> - Kick someone""");
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("list")) {
                if (args.length == 0) {
                    LinkedList<String> names = new LinkedList<>();
                    for (Client client : Main.connected) {
                        names.add(client.username);
                    }
                    System.out.println("List of people online: " + Arrays.toString(names.toArray()));
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("stop")) {
                if (args.length == 0) {
                    Main.logger.log("INFO", "Stopping server...");
                    for (Client client : Main.connected) {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                            bufferedWriter.write("Server closed");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.exit(0);
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("kick")) {
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
                    System.out.println("Could not find a user with the username " + username);
                } else {
                    System.out.println("Invalid arguments");
                }
            } else {
                System.out.println(("Unknown command; Do \"/help\" for help"));
            }
        }
        else if(Main.serverType == ServerType.WITH_ACCOUNTS) {
            if (command.equalsIgnoreCase("help")) {
                if (args.length == 0) {
                    System.out.println("""
                            HELP MENU
                            =====================
                            /list - List the people online
                            /stop - Stop the server
                            /kick <person> - Kick someone
                            /ban <person> - Ban someone
                            /pardon <person> - Unban someone
                            /banlist - See everyone who is banned
                            /op <person> - Make someone an administrator
                            /deop <person> - Make someone no longer an administrator
                            /oplist - See everyone who is an administrator""");
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("list")) {
                if (args.length == 0) {
                    LinkedList<String> names = new LinkedList<>();
                    for (Client client : Main.connected) {
                        names.add(client.username);
                    }
                    System.out.println("List of people online: " + Arrays.toString(names.toArray()));
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("stop")) {
                if (args.length == 0) {
                    Main.logger.log("INFO", "Stopping server...");
                    for (Client client : Main.connected) {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                            bufferedWriter.write("Server closed");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.exit(0);
                } else {
                    System.out.println("Invalid arguments");
                }
            } else if (command.equalsIgnoreCase("kick")) {
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
                    System.out.println("Could not find a user with the username " + username);
                } else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("ban")) {
                if(args.length == 1) {
                    File accDir = new File("accounts");
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
                        System.out.println("Could not find a user with the username " + args[0]);
                    }
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("pardon")) {
                if(args.length == 1) {
                    File accDir = new File("accounts");
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
                        System.out.println("Could not find a user with the username " + args[0]);
                    }
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("banlist")) {
                if(args.length == 0) {
                    LinkedList<String> names = new LinkedList<>();
                    File accDir = new File("accounts");
                    if(accDir.listFiles() != null) {
                        for (File file : accDir.listFiles()) {
                            PropertiesFile acc = new PropertiesFile(file.getPath());
                            if(Boolean.parseBoolean(acc.getProperty("banned"))) {
                                names.add(file.getName().replace(".properties", ""));
                            }
                        }
                    }
                    System.out.println("Banned accounts are: " + Arrays.toString(names.toArray()));
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("op")) {
                if(args.length == 1) {
                    File accDir = new File("accounts");
                    if(accDir.listFiles() != null) {
                        for(File file : accDir.listFiles()) {
                            if(file.getName().replace(".properties", "").equals(args[0])) {
                                PropertiesFile acc = new PropertiesFile(file.getPath());
                                acc.setProperty("administrator", "true");
                                String username = args[0];
                                for (Client client : Main.connected) {
                                    if (client.username.equals(username)) {
                                        try {
                                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                            bufferedWriter.write("You have been made a server administrator");
                                            bufferedWriter.newLine();
                                            bufferedWriter.flush();
                                            Main.logger.log("INFO", username + " has been made a server administrator");
                                            return;
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                return;
                            }
                        }
                        System.out.println("Could not find a user with the username " + args[0]);
                    }
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("deop")) {
                if(args.length == 1) {
                    File accDir = new File("accounts");
                    if(accDir.listFiles() != null) {
                        for(File file : accDir.listFiles()) {
                            if(file.getName().replace(".properties", "").equals(args[0])) {
                                PropertiesFile acc = new PropertiesFile(file.getPath());
                                acc.setProperty("administrator", "false");
                                Main.logger.log("INFO", args[0] + " has been made no longer a server administrator");
                                return;
                            }
                        }
                        System.out.println("Could not find a user with the username " + args[0]);
                    }
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else if(command.equalsIgnoreCase("oplist")) {
                if(args.length == 0) {
                    LinkedList<String> names = new LinkedList<>();
                    File accDir = new File("accounts");
                    if(accDir.listFiles() != null) {
                        for (File file : accDir.listFiles()) {
                            PropertiesFile acc = new PropertiesFile(file.getPath());
                            if(Boolean.parseBoolean(acc.getProperty("administrator"))) {
                                names.add(file.getName().replace(".properties", ""));
                            }
                        }
                    }
                    System.out.println("Administrators are: " + Arrays.toString(names.toArray()));
                }
                else {
                    System.out.println("Invalid arguments");
                }
            }
            else {
                System.out.println(("Unknown command; Do \"/help\" for help"));
            }
        }
    }
}
