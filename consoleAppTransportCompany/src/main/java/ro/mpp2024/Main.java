package ro.mpp2024;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileReader("properties/db.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find file " + e);
        }

    }
}