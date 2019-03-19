package ch.uzh.seal.utils;


import ch.uzh.seal.App;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyManagement {
    private static Properties properties_instance = null;

    public static String getProperty(String propertyName) {
        String property = getProperties() != null ? getProperties().getProperty(propertyName) : null;
        if (property == null) {
            log.error(String.format("%s is not set in the config.properties file", propertyName));
            return null;
        }
        return property;
    }

    private static Properties getProperties() {
        if (properties_instance == null) {
            properties_instance = new Properties();
            InputStream input = null;
            try {
                String filename = "config.properties";
                input = App.class.getClassLoader().getResourceAsStream(filename);
                if(input==null){
                   log.error("Sorry, unable to find config file " + filename);
                    return null;
                }
                properties_instance.load(input);
          } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                if(input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return properties_instance;
    }

}
