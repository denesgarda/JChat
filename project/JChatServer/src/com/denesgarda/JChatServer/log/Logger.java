package com.denesgarda.JChatServer.log;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class Logger {
    private PrintStream[] printStreams;

    public Logger(PrintStream[] printStreams) {
        this.printStreams = printStreams;
    }

    public void log(String level, String string) {
        for(PrintStream printStream : printStreams) {
            printStream.println(Calendar.getInstance().getTime() + " [" + level + "]: " + string);
        }
    }
}
