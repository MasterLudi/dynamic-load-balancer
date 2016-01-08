package host;

import java.io.*;
import java.net.*;

public class Remote extends Host {

    public Remote(String server, int serverPort) {
        super();

        try {
            // initialize socket
            socket = new Socket(server, serverPort);
            System.out.println("Connected to server... IP: " + socket.getInetAddress() + "  port: " + socket.getPort());

            // initialize input, output streams
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());

            // object has to implement serializable
            objIn = new ObjectInputStream(socket.getInputStream());
            objOut = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("input/output streams initialized");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
