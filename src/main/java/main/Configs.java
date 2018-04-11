package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Configs {
    protected ArrayList<String> takeProperties(){
        String user = null;
        String password = null;
        String url = null;
        String driver = null;
        Properties properties = new Properties();
        try(InputStream fis = ClassLoader.getSystemResourceAsStream("config.properties")){
            properties.load(fis);
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            url = properties.getProperty("url");
            driver = properties.getProperty("driver");
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Erorr in properties file");
        }
        ArrayList<String> propertiesArr = new ArrayList<>();
        propertiesArr.add(user);
        propertiesArr.add(password);
        propertiesArr.add(url);
        propertiesArr.add(driver);
        return propertiesArr;
    }
}