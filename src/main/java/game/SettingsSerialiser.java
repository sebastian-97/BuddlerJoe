package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SettingsSerialiser {

    private Settings settings;

    public SettingsSerialiser(Settings settings) {
        this.settings = settings;
    }

    public void serialiseSettings(){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/config/Settings.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(settings);
            out.close();
            fileOut.close();
            return;
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Settings readSettings() {
        try {
            FileInputStream fileIn = new FileInputStream("/config/Settings.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            settings = (Settings) in.readObject();
            in.close();
            fileIn.close();
            return settings;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Settings not found");
            c.printStackTrace();
            return null;
        }
    }
}
