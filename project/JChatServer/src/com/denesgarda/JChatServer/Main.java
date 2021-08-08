package com.denesgarda.JChatServer;

import com.denesgarda.JChatServer.enums.ServerType;
import com.denesgarda.JChatServer.log.Logger;
import com.denesgarda.Prop4j.data.PropertiesFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    public static Logger logger;
    public static PropertiesFile config;
    public static ServerType serverType;
    
    public static LinkedList<Client> requested = new LinkedList<>();
    public static LinkedList<Client> connected = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        Window window = new Window();

        File logDir = new File("logs");
        if(!logDir.exists()) {
            boolean successful = logDir.mkdirs();
            if(!successful) {
                System.out.println("Initialization failed");
                System.exit(-1);
            }
        }
        PrintStream printStream = new PrintStream(new FileOutputStream("logs/" + Calendar.getInstance().getTime() + ".log"));
        logger = new Logger(new PrintStream[]{System.out, printStream});
        logger.log("INFO", "Starting server");
        logger.log("INFO", "Searching for config files");
        config = new PropertiesFile("config.properties");
        if(config.exists()) {
            logger.log("INFO", "Config files found");
        }
        else {
            logger.log("WARN", "Config files not found");
            logger.log("INFO", "Creating new files");
            boolean successful = config.getAsFile().createNewFile();
            if(!successful) {
                logger.log("ERROR", "Failed to create new files");
                System.exit(-1);
            }
            logger.log("INFO", "Configuring files");
            config.setProperty("port", "6577");
            config.setProperty("max-connections", "5");
            config.setProperty("use-accounts", "false");
            config.setProperty("allow-account-creation", "false");
            int number = new Random().nextInt(100000);
            config.setProperty("server-name", "Server" + number);
            logger.log("NOTE", "To manually configure new files, stop the server after a safe start");
            logger.log("INFO", "Configuration complete");
        }
        logger.log("INFO", "Configuring server");
        window.setTitle("JChatServer - " + config.getProperty("server-name"));
        boolean useAccounts = Boolean.parseBoolean(config.getProperty("use-accounts"));
        if(useAccounts) {
            serverType = ServerType.WITH_ACCOUNTS;
            File accDir = new File("accounts");
            if(!accDir.exists()) {
                boolean successful = accDir.mkdirs();
                if(!successful) {
                    System.out.println("Initialization failed");
                    System.exit(-1);
                }
                File df = new File("accounts/default.properties");
                successful = df.createNewFile();
                if(!successful) {
                    System.out.println("Initialization failed");
                    System.exit(-1);
                }
                PropertiesFile def = new PropertiesFile("accounts/default.properties");
                def.setProperty("password", "password");
                def.setProperty("administrator", "false");
                def.setProperty("banned", "false");
            }
        }
        else {
            serverType = ServerType.WITH_NICKNAMES;
        }
        logger.log("INFO", "Opening socket");
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(config.getProperty("port")));
        logger.log("INFO", "Server started on port " + config.getProperty("port"));
        while(true) {
            Socket socket = serverSocket.accept();
            Client client = new Client(socket);
            Thread thread = new Thread(client);
            thread.start();
            requested.add(client);
        }
    }
}
