package com.denesgarda.JChatClient;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        Login login = new Login();

        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //System.out.print("Enter server address: ");
        //String[] address = in.readLine().split(":");
        //Socket socket = new Socket(address[0], Integer.parseInt(address[1]));

        /*BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write("01100011 01101111 01101110 01101110 01100101 01100011 01110100");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response1 = bufferedReader.readLine();
        if(response1.equals("0")) {
            System.out.print("Enter nickname: ");
            String username = in.readLine();

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String response2 = bufferedReader.readLine();
            if(response2.equals("0")) {
                System.out.println("Username taken");
            }
            else if(response2.equals("1")) {
                Server server = new Server(socket);
                Thread thread = new Thread(server);
                thread.start();
            }
            else if(response2.equals("2")) {
                System.out.println("Illegal name");
            }
            else {
                System.out.println("Unknown response code");
            }
        }
        else if(response1.equals("1")) {
            System.out.println("Connection refused: Connection throttle");
        }
        else {
            System.out.println("Unknown response code");
        }*/
    }
}
