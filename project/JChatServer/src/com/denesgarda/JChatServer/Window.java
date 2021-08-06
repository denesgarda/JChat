package com.denesgarda.JChatServer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
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
        System.setOut(new PrintStream(out));
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
                        if(input.equalsIgnoreCase("/help")) {
                            System.out.println("""
                                        HELP MENU
                                        =====================
                                        /list - List the people online
                                        /stop - Stop the server
                                        /kick <person> - Kick someone""");
                        }
                        else if(input.equalsIgnoreCase("/list")) {
                            Main.logger.log("INFO", "Executed /list");
                            LinkedList<String> names = new LinkedList<>();
                            for(Client client : Main.connected) {
                                names.add(client.username);
                            }
                            System.out.println(("List of people online: " + Arrays.toString(names.toArray())));
                        }
                        else if(input.equalsIgnoreCase("/stop")) {
                            for(Client client : Main.connected) {
                                try {
                                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                    bufferedWriter.write("Server closed");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                    Main.logger.log("INFO", "Stopping server...");
                                }
                                catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            System.exit(0);
                        }
                        else if(input.split(" ")[0].equalsIgnoreCase("/kick")) {
                            String username = input.split(" ")[1];
                            for(Client client : Main.connected) {
                                if(client.username.equals(username)) {
                                    try {
                                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
                                        bufferedWriter.write("You have been kicked from the server");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                        Main.logger.log("INFO", "Kicked " + username);
                                    }
                                    catch(Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
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
}
