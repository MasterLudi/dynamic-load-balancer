package runner;

import controller.*;
import host.*;
import job.*;

import java.io.IOException;

/**
 * Remote-side Dynamic Load Balancer.
 */
public class RemoteHostRunner {

    public static void main(String[] args) {

        // parse inputs
        if (args.length != 2) {
            System.out.println("Usage: java -jar RemoteHostRunner.jar [server_ip] [server_port]");
        }

        // connect to server
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Remote remote = new Remote(serverIP, serverPort);
        JobQueue<Job> jobQueue = new JobQueue();
        JobQueue<Job> resultsQueue = new JobQueue();

        TransferHandler transferHandler = new TransferHandler(jobQueue, resultsQueue, remote);
        remote.sendMessage("ready to handle jobs");

        while (true) {}

    }
}
