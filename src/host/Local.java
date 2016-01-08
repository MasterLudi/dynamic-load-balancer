package host;

import java.net.*;
import java.io.*;

public class Local extends Host {

    private ServerSocket serverSocket;

    private boolean connected = false;

    public Local(int port) {
        super();

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen() {

        try {
            // get client connection
            socket = serverSocket.accept();

            System.out.println("Remote connected at " + socket.getInetAddress().getHostAddress());

            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());

            connected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }

    public boolean isConnected() {
        return connected;
    }
}
