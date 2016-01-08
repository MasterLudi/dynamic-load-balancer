package host;

import java.io.*;
import java.net.*;

public abstract class Host {

    protected Socket socket;
    /** for messages */
    protected DataInputStream dataIn;
    protected DataOutputStream dataOut;
    /** for state object, work object */
    protected ObjectInputStream objIn;
    protected ObjectOutputStream objOut;

    public void sendMessage(String message){
        try {
            dataOut.writeUTF(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        try {
            return dataIn.readUTF();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendObj(Object obj) {
        try {
            objOut.writeObject(obj);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getObj() {
        try {
            return objIn.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
