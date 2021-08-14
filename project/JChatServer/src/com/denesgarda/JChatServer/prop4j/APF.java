package com.denesgarda.JChatServer.prop4j;

import com.denesgarda.Prop4j.data.PropertiesFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class APF extends PropertiesFile {
    public APF(String path) {
        super(path);
    }

    public String getPropertyNotNull(String key, String defaultValue) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(this.getPath());
        properties.load(fileInputStream);
        String result = properties.getProperty(key);
        fileInputStream.close();
        if(result == null) {
            this.setProperty(key, defaultValue);
        }
        return result;
    }
}
