package com.denesgarda.JChatServer.log;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class Logger {
    private PrintStream[] printStreams;

    public Logger(PrintStream[] printStreams) {
        this.printStreams = printStreams;
    }

    public String log(String level, String string) {
        String result = null;
        for(PrintStream printStream : printStreams) {
            result = Calendar.getInstance().getTime() + " [" + level + "]: " + string;
            printStream.println(result);
        }
        return result;
    }
}
