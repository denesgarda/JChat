package com.denesgarda.JChatServer;

import com.denesgarda.Prop4j.data.PropertiesFile;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
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
                            for(int i = 0; i < split.length - 1; i++) {
                                args[i] = split[i + 1];
                            }
                            command(command, args);
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

    private void command(String command, String[] args) {
        Main.logger.log("INFO", "Executed /"  + command + " with the arguments " + Arrays.toString(args));
        if(command.equalsIgnoreCase("help")) {
            if(args.length == 0) {
                System.out.println("""
                    HELP MENU
                    =====================
                    /list - List the people online
                    /stop - Stop the server
                    /kick <person> - Kick someone""");
            }
            else {
                System.out.println("Invalid arguments");
            }
        }
        else if(command.equalsIgnoreCase("list")) {
            if(args.length == 0) {
                LinkedList<String> names = new LinkedList<>();
                for(Client client : Main.connected) {
                    names.add(client.username);
                }
                System.out.println("List of people online: " + Arrays.toString(names.toArray()));
            }
            else {
                System.out.println("Invalid arguments");
            }
        }
        else if(command.equalsIgnoreCase("stop")) {
            if(args.length == 0) {
                Main.logger.log("INFO", "Stopping server...");
                for(Client client : Main.connected) {
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                        bufferedWriter.write("Server closed");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.exit(0);
            }
            else {
                System.out.println("Invalid arguments");
            }
        }
        else if(command.equalsIgnoreCase("kick")) {
            if(args.length == 1) {
                String username = args[0];
                for(Client client : Main.connected) {
                    if(client.username.equals(username)) {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                            bufferedWriter.write("You have been kicked from the server");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            client.socket.close();
                            Main.connected.remove(client);
                            String message = Main.logger.log("INFO", username + " has been kicked from the server");
                            for(Client newClient : Main.connected) {
                                BufferedWriter newBufferedWriter = new BufferedWriter(new OutputStreamWriter(newClient.socket.getOutputStream()));
                                newBufferedWriter.write(message);
                                newBufferedWriter.newLine();
                                newBufferedWriter.flush();
                            }
                            return;
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.out.println("Could not find a user with the username " + username);
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
