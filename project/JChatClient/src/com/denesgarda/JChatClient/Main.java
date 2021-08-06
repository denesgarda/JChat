package com.denesgarda.JChatClient;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        String username = "D";
        Socket socket = new Socket("localhost", 8000);
        Server server = new Server(socket, username);
        Thread thread = new Thread(server);
        thread.start();
    }
}
