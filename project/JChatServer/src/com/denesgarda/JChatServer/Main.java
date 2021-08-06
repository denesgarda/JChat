package com.denesgarda.JChatServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Main {
    public static LinkedList<Client> clients = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8000);
        while(true) {
            Socket socket = serverSocket.accept();
            Client client = new Client(socket);
            Thread thread = new Thread(client);
            thread.start();
            clients.add(client);
        }
    }
}
