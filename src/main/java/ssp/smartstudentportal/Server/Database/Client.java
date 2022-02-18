package ssp.smartstudentportal.Server.Database;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Connected to server");
            BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream()));
            writer.newLine();
            writer.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> messages = (ArrayList<String>) inputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
