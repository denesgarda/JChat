package com.denesgarda.JChatServer.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Files {
    public static String[] getAllLines(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str;
        List<String> list = new ArrayList<String>();
        while((str = in.readLine()) != null){
            list.add(str);
        }
        return list.toArray(new String[0]);
    }
}
